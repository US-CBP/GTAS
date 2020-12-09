/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.HitDetail;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.services.dto.ApplicationStatisticsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminServiceImpl implements AdminService {

	private Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

	private MessageService messageService;

	private HitDetailRepository hitDetailRepository;

	@Autowired
	public AdminServiceImpl(MessageService messageService, HitDetailRepository hitDetailRepository) {
		this.messageService = messageService;
		this.hitDetailRepository = hitDetailRepository;
	}

	@Override
	public ApplicationStatisticsDTO createApplicationStatisticsDto() {
		List<Message> messageList = messageService.getMostRecent500Messages();
		if (messageList.isEmpty()) {
			return new ApplicationStatisticsDTO();
		}
		int passengerCount = getPassengerCount(messageList);
		ApplicationStatisticsDTO applicationStatisticsDTO = new ApplicationStatisticsDTO();
		applicationStatisticsDTO.setPassengerCount(passengerCount);
		messageList.stream().findFirst()
				.ifPresent(m -> applicationStatisticsDTO.setLastMessageInSystem(m.getCreateDate()));
		List<MessageStatus> msList = messageList.stream().map(Message::getStatus).collect(Collectors.toList());
		populateApplicationStatistics(applicationStatisticsDTO, msList);
		HitDetail mostRecentHitDetail = hitDetailRepository.findFirstByOrderByIdDesc();
		if (mostRecentHitDetail != null) {
			applicationStatisticsDTO.setMostRecentRuleHit(mostRecentHitDetail.getCreatedDate());
		}
		return applicationStatisticsDTO;
	}

	@SuppressWarnings("WeakerAccess") // For test.
	protected void populateApplicationStatistics(ApplicationStatisticsDTO applicationStatisticsDTO,
			List<MessageStatus> msList) {
		applicationStatisticsDTO.setLastMessageAnalyzedByDrools(Date.from(Instant.EPOCH));
		for (MessageStatus ms : msList) {
			switch (ms.getMessageStatusEnum()) {
			case RECEIVED:
			case PARSED:
				applicationStatisticsDTO.setParsed(applicationStatisticsDTO.getParsed() + 1);
				break;
			case LOADED:
				applicationStatisticsDTO.setLoadedCount(applicationStatisticsDTO.getLoadedCount() + 1);
				break;
			case RUNNING_RULES:
				applicationStatisticsDTO.setRunningRules(applicationStatisticsDTO.getRunningRules() + 1);
				break;
			case ANALYZED:
				updateLastAnalyzed(applicationStatisticsDTO, ms);
				applicationStatisticsDTO.setAnalyzedCount(applicationStatisticsDTO.getAnalyzedCount() + 1);
				break;
			case NEO_LOADED:
				applicationStatisticsDTO.setLoadedInNeo4JCount(applicationStatisticsDTO.getLoadedInNeo4JCount() + 1);
				break;
			case NEO_ANALYZED:
				applicationStatisticsDTO.setNeoAnalyzedCount(applicationStatisticsDTO.getNeoAnalyzedCount() + 1);
				updateLastAnalyzed(applicationStatisticsDTO, ms);
				break;
			case FAILED_PRE_PARSE:
			case FAILED_PRE_PROCESS:
			case FAILED_PARSING:
			case DUPLICATE_MESSAGE:
				applicationStatisticsDTO.setFailedParsingCount(applicationStatisticsDTO.getFailedParsingCount() + 1);
				break;
			case FAILED_LOADING:
				applicationStatisticsDTO.setFailedLoadCount(applicationStatisticsDTO.getFailedLoadCount() + 1);
				break;
			case FAILED_ANALYZING:
				applicationStatisticsDTO.setFailedAnalyzedCount(applicationStatisticsDTO.getFailedAnalyzedCount() + 1);
				break;
			case FAILED_NEO_4_J:
				applicationStatisticsDTO.setFailedNeo4jCount(applicationStatisticsDTO.getFailedNeo4jCount() + 1);
				break;
			case PARTIAL_ANALYZE:
				applicationStatisticsDTO
						.setPartialAnalyzedCount(applicationStatisticsDTO.getPartialAnalyzedCount() + 1);
				break;
			case APIS_DATA_MASKED:
			case PNR_DELETE_ERROR:
			case APIS_DELETE_ERROR:
			case APIS_MASK_ERROR:
			case PNR_MASK_ERROR:
			case APIS_DATA_DELETED:
			case PNR_DATA_DELETED:
			case PNR_DATA_MASKED:
				updateLastAnalyzed(applicationStatisticsDTO, ms);
				break;
			default:
				logger.warn("Un-implemented field." + ms.getMessageStatusEnum().toString());
			}
		}
	}

	private void updateLastAnalyzed(ApplicationStatisticsDTO applicationStatisticsDTO, MessageStatus ms) {
		if (ms.getAnalyzedTimestamp() != null
				&& ms.getAnalyzedTimestamp().after(applicationStatisticsDTO.getLastMessageAnalyzedByDrools())) {
			applicationStatisticsDTO.setLastMessageAnalyzedByDrools(ms.getAnalyzedTimestamp());
		}
	}

	protected int getPassengerCount(List<Message> messageList) {
		int count = 0;
		for (Message m : messageList) {
			if (m.getPassengerCount() != null) {
				count += m.getPassengerCount();
			}
		}
		return count;
	}
}
