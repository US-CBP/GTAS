/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.aop.annotations.PVLRequestAuditFirstArgRequest;
import gov.gtas.enumtype.HitSeverityEnum;
import gov.gtas.enumtype.HitViewStatusEnum;
import gov.gtas.model.*;
import gov.gtas.model.dto.ViewUpdateDTo;
import gov.gtas.repository.HitViewStatusRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.PriorityVettingListDTO;
import gov.gtas.services.dto.PriorityVettingListRequest;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.CountDownVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Component
public class PriorityVettingListServiceImpl implements PriorityVettingListService {

	private final UserService userService;

	private final PassengerRepository passengerRepository;

	private final HitViewStatusRepository hitViewStatusRepository;

	public PriorityVettingListServiceImpl(UserService userService, PassengerRepository passengerRepository,
			HitViewStatusRepository hitViewStatusRepository) {
		this.userService = userService;
		this.passengerRepository = passengerRepository;
		this.hitViewStatusRepository = hitViewStatusRepository;
	}

	@Override
	@Transactional
	@PVLRequestAuditFirstArgRequest
	public PriorityVettingListDTO generateDtoFromRequest(PriorityVettingListRequest request, String userId) {
		Set<UserGroup> userGroups = userService.fetchUserGroups(userId);

		if (request == null) {
			return new PriorityVettingListDTO(new ArrayList<>(), 0L);
		}

		Pair<Long, List<Passenger>> immutablePair = passengerRepository.priorityVettingListQuery(request, userGroups,
				userId);
		long count = immutablePair.getLeft();
		List<CaseVo> caseVOS = new ArrayList<>();

		for (Passenger passenger : immutablePair.getRight()) {

			//todo: implement smarter logic that goes beyond default behavior for data masking.
			if (passenger.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()){
				continue;
			}
			CaseVo caseVo = new CaseVo();
			Date countDownTo = passenger.getFlight().getFlightCountDownView().getCountDownTimer();
			CountDownCalculator countDownCalculator = new CountDownCalculator();
			CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownTo, 30, 30);
			ArrayList<String> hitDetailsTitles = new ArrayList<>();
			List<HitViewStatusEnum> hvsEnums = new ArrayList<>();

			List<HitDetail> hitDetailsList = new ArrayList<>(passenger.getHitDetails());
			hitDetailsList.sort((hd1, hd2) -> {
				HitSeverityEnum hse1 = hd1.getHitMaker().getHitCategory().getSeverity();
				HitSeverityEnum hse2 = hd2.getHitMaker().getHitCategory().getSeverity();
				return Integer.compare(hse1.ordinal(), hse2.ordinal());
			});
			for (HitDetail hd : hitDetailsList) {
				Set<UserGroup> hitUserGroups = hd.getHitMaker().getHitCategory().getUserGroups();
				String severity = hd.getHitMaker().getHitCategory().getSeverity().toString();
				if (!Collections.disjoint(hitUserGroups, userGroups)) {
					String title;
					if (hd.getTitle().length() > 8) {
						title = hd.getTitle().substring(0, 8) + "...";
					} else {
						title = hd.getTitle();
					}

					if (passenger.getDataRetentionStatus().requiresMaskedPnrAndApisMessage())  {
						title = "MASKED";
					}
					hitDetailsTitles.add(severity + " | " + hd.getHitMaker().getHitCategory().getName() + " | " + title
							+ "(" + hd.getHitEnum().getDisplayName() + ")");
					for (HitViewStatus hvs : hd.getHitViewStatus()) {
						if (userGroups.contains(hvs.getUserGroup())) {
							hvsEnums.add(hvs.getHitViewStatusEnum());
						}
					}
				}
			}

			String docNum = "";
			String docType = "";
			for (Document doc : passenger.getDocuments()) {
				docNum = doc.getDocumentNumber();
				docType = doc.getDocumentType();
				if ("P".equalsIgnoreCase(doc.getDocumentType())) {
					break;
				}
			}

			caseVo.setDocument(docNum);
			caseVo.setDocType(docType);
			hvsEnums.sort(Comparator.naturalOrder());
			caseVo.setStatus(hvsEnums.get(0).toString());
			caseVo.setGender(passenger.getPassengerDetails().getGender());
			caseVo.setHitNames(hitDetailsTitles);
			caseVo.setCountdownTime(countDownTo);
			caseVo.setCloseToCountDown(countDownVo.isCloseToCountDown());
			caseVo.setNationality(passenger.getPassengerDetails().getNationality());
			caseVo.setCountDownTimeDisplay(countDownVo.getCountDownTimer());
			caseVo.setCurrentTime(new Date());
			caseVo.setDob(passenger.getPassengerDetails().getDob());
			caseVo.setFirstName(passenger.getPassengerDetails().getFirstName());
			caseVo.setLastName(passenger.getPassengerDetails().getLastName());
			caseVo.setPaxName(passenger.getPassengerDetails().getFirstName() + " "
					+ passenger.getPassengerDetails().getLastName());
			caseVo.setFlightId(passenger.getFlight().getId());
			caseVo.setPaxId(passenger.getId());
			caseVo.setFlightNumber(passenger.getFlight().getFullFlightNumber());
			caseVo.setFlightDirection(passenger.getFlight().getDirection());
			caseVo.setFlightETADate(passenger.getFlight().getMutableFlightDetails().getEta());
			caseVo.setFlightETDDate(passenger.getFlight().getMutableFlightDetails().getEtd());
			caseVo.setFlightOrigin(passenger.getFlight().getOrigin());
			caseVo.setFlightDestination(passenger.getFlight().getDestination());
			if (passenger.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()) {
				caseVo.maskPII();
			}
			caseVOS.add(caseVo);
		}
		return new PriorityVettingListDTO(caseVOS, count);
	}

	@Override
	@Transactional
	public synchronized void update(ViewUpdateDTo viewUpdateDTo, String userId) {
		Long paxId = viewUpdateDTo.getPassengerId();
		Passenger p = passengerRepository.findById(paxId).orElseThrow(RuntimeException::new);
		Set<HitViewStatus> hitViewStatuses = hitViewStatusRepository.findAllByPassenger(p);
		HitViewStatusEnum hitViewStatusEnum = HitViewStatusEnum.fromString(viewUpdateDTo.getStatus())
				.orElseThrow(RuntimeException::new);
		for (HitViewStatus hvs : hitViewStatuses) {
			hvs.setHitViewStatusEnum(hitViewStatusEnum);
			hvs.setUpdatedBy(userId);
		}
		hitViewStatusRepository.saveAll(hitViewStatuses);
	}
}
