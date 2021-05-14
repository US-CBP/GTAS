/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler;

import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.services.AdditionalProcessingService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.RuleHitPersistenceService;
import gov.gtas.svc.util.TargetingResultUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class AsyncHitPersistenceThread extends RuleThread {

	Logger logger = LoggerFactory.getLogger(AsyncHitPersistenceThread.class);

	private Set<Long> flightIds;
	private PendingHitDetailRepository pendingHitDetailRepository;
	private ApplicationContext applicationContext;
	private PassengerService passengerService;
	private AdditionalProcessingService additionalProcessingService;

	public AsyncHitPersistenceThread(PendingHitDetailRepository pendingHitDetailRepository,
			ApplicationContext applicationContext, PassengerService passengerService,
			AdditionalProcessingService additionalProcessingService) {
		this.pendingHitDetailRepository = pendingHitDetailRepository;
		this.applicationContext = applicationContext;
		this.additionalProcessingService = additionalProcessingService;
		this.passengerService = passengerService;
	}

	@Override
	public Boolean call() {
		if (flightIds == null) {
			throw new IllegalArgumentException("flightIds must be set");
		}
		boolean success = true;
		RuleHitPersistenceService persistenceService = applicationContext.getBean(RuleHitPersistenceService.class);

		List<PendingHitDetails> pendingHitDetails = pendingHitDetailRepository
				.getPendingHitDetailsByFlightIds(flightIds);
		try {
			Set<HitDetail> hitDetails = createHitDetails(pendingHitDetails);
			List<Set<HitDetail>> batchedHitDetails = TargetingResultUtils.batchResults(hitDetails, 150);
			List<Long> passengerIds = hitDetails.stream().map(HitDetail::getPassengerId).collect(Collectors.toList());
			Set<Passenger> passengers = passengerService.findPassengerFromPassengerIds(passengerIds);
			Set<Long> latestMessages = new HashSet<>();
			for (Passenger p : passengers) {
				latestMessages.add(p.getPassengerTripDetails().getMostRecentMessageId());
			}
			List<MessageStatus> messageStatusList = persistenceService.getRelevantMessages(latestMessages);
			processHits(messageStatusList, persistenceService, batchedHitDetails, additionalProcessingService);
		} catch (Exception e) {
			success = false;
			logger.error("Error in rule persistence!", e);
		}
		Set<Long> flightIds = pendingHitDetails.stream().map(PendingHitDetails::getFlightId)
				.collect(Collectors.toSet());
		try {
			persistenceService.updateFlightHitCounts(flightIds);
		} catch (Exception e) {
			success = false;
			logger.error("Error updating flight counts!", e);
		}
		pendingHitDetailRepository.deleteAll(pendingHitDetails); // Delete all pending hits - no saving for now.
		return success;
	}

	protected Set<HitDetail> createHitDetails(List<PendingHitDetails> pendingHitDetails) {
		logger.debug("In async creating hit details.");
		Set<HitDetail> hitDetails = new HashSet<>();
		if (!pendingHitDetails.isEmpty()) {
			for (PendingHitDetails pendingHitDetail : pendingHitDetails) {
				HitDetail hitDetail = HitDetail.from(pendingHitDetail);
				hitDetails.add(hitDetail);
			}
		}
		return hitDetails;
	}

	public void setFlightIds(Set<Long> flightIds) {
		this.flightIds = flightIds;
	}
}
