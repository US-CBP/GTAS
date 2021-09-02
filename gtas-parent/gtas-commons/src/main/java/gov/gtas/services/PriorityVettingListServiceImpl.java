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
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.HitViewStatusRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.PriorityVettingListDTO;
import gov.gtas.services.dto.PriorityVettingListRequest;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.CaseVo;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PriorityVettingListServiceImpl implements PriorityVettingListService {
	
	private static final Logger logger = LoggerFactory.getLogger(PriorityVettingListServiceImpl.class);

	private final UserService userService;

	private final PassengerRepository passengerRepository;

	private final HitViewStatusRepository hitViewStatusRepository;

	private final POEService poeService;

	public PriorityVettingListServiceImpl(UserService userService, PassengerRepository passengerRepository,
			HitViewStatusRepository hitViewStatusRepository, POEService poeService) {
		this.userService = userService;
		this.passengerRepository = passengerRepository;
		this.hitViewStatusRepository = hitViewStatusRepository;
		this.poeService = poeService;
	}

	@Override
	@Transactional
	@PVLRequestAuditFirstArgRequest
	public PriorityVettingListDTO generateDtoFromRequest(PriorityVettingListRequest request, String userId) {
		long start = System.nanoTime();
		Set<UserGroup> userGroups = userService.fetchUserGroups(userId);
		logger.info("Usergroups found in {}.", (System.nanoTime() - start) / 1000000);

		if (request == null) {
			return new PriorityVettingListDTO(new ArrayList<>());
		}
		start = System.nanoTime();
		Pair<Long, List<Long>> immutablePair = passengerRepository.priorityVettingListQuery(request, userGroups,
				userId);
		logger.info("PVL Query found in {}.", (System.nanoTime() - start) / 1000000);

		List<CaseVo> caseVOS = new ArrayList<>();
	
		Set<Long> passengerIds = immutablePair.getRight() == null? new HashSet<>() : new HashSet<>(immutablePair.getRight());
		
		/*
		 * Need to get passengers with
		 * Hit Details
		 * 	Hit View Status
		 * 	Hit Makers
		 * 	Hit Category
		 * 	User Groups
		 * Documents
		 * Flight	
		 * */
		start = System.nanoTime();
		Set<Passenger> fullPassengers; 
		Map<Long, Set<HitDetail>> paxIdKeyHitDetailSetValueMap = new HashMap<>();
		Set<Long> hitDetailIdSet;
		Map<Long, HitCategory> hitCategoryMap = new HashMap<>();
		Map<Long, Set<HitViewStatus>> paxIdKeyHitViewSetValueMap = new HashMap<>();
		Map<Long, Set<Document>> paxIdKeyDocumentValueMap = new HashMap<>();

		if (!passengerIds.isEmpty()) {
			fullPassengers = passengerRepository.getPriorityVettingListPassengers(passengerIds);
			paxIdKeyHitDetailSetValueMap = createHitDetailMap(passengerIds);
			hitDetailIdSet = new HashSet<>();
			Collection<Set<HitDetail>> values =	paxIdKeyHitDetailSetValueMap.values();
			for (Set<HitDetail> hdSet : values) {
				Set<Long> hdIds = hdSet.stream().map(hd -> hd.getId()).collect(Collectors.toSet());
				hitDetailIdSet.addAll(hdIds);
			}
			hitCategoryMap = getHitCategories(hitDetailIdSet);
			paxIdKeyHitViewSetValueMap = createHitViewMap(hitDetailIdSet);
			paxIdKeyDocumentValueMap = createDocumentMap(passengerIds);
			
		} else {
			fullPassengers = Collections.emptySet();
		}
		logger.info("Passenger hydration in {}.", (System.nanoTime() - start) / 1000000);
		final Map<Long, HitCategory> finalHitCategoryMap = new HashMap<>(hitCategoryMap);

		for (Passenger passenger : fullPassengers) {

			CaseVo caseVo = new CaseVo();
			int highPrioHitCount = 0;
			int medPrioHitCount = 0;
			int lowPrioHitCount = 0;
			ArrayList<String> hitDetailsTitles = new ArrayList<>();
			List<HitViewStatusEnum> hvsEnums = new ArrayList<>();
			List<HitViewStatus> hvsToUpdate = new ArrayList<>();
			String lookoutStatus = new String();
			Set<HitDetail> hitDetailSet = paxIdKeyHitDetailSetValueMap.get(passenger.getId());
			List<HitDetail> hitDetailsList = new ArrayList<>(hitDetailSet);
			hitDetailsList.sort((hd1, hd2) -> {
				HitCategory hd1Category1 = finalHitCategoryMap.get(hd1.getId());
				HitCategory hd1Category2 = finalHitCategoryMap.get(hd2.getId());
				HitSeverityEnum hse1 = hd1Category1.getSeverity();
				HitSeverityEnum hse2 = hd1Category2.getSeverity();
				return Integer.compare(hse1.ordinal(), hse2.ordinal());
			});
			for (HitDetail hd : hitDetailsList) {
				HitCategory hdHitCategory = finalHitCategoryMap.get(hd.getId());
				Set<UserGroup> hitUserGroups =hdHitCategory.getUserGroups();
				String severity = hdHitCategory.getSeverity().toString();
				if (!Collections.disjoint(hitUserGroups, userGroups)) {
					String title = hd.getTitle();

					if (passenger.getDataRetentionStatus().requiresMaskedPnrAndApisMessage())  {
						title = "MASKED";
					}

          hitDetailsTitles.add(severity + " | " + hdHitCategory.getName() + " | " + title
							+ " | " + hd.getHitEnum().getDisplayName());
					switch(severity){
						case "Top":
							highPrioHitCount++;
							break;
						case "High":
							medPrioHitCount++;
							break;
						case "Normal":
							lowPrioHitCount++;
							break;
					}

					for (HitViewStatus hvs : paxIdKeyHitViewSetValueMap.get(hd.getId())) {
						if (userGroups.contains(hvs.getUserGroup())) {
							hvsEnums.add(hvs.getHitViewStatusEnum());
							if(poeService.lookoutIsMissedOrInactiveAndUpdate(hvs)){
								hvsToUpdate.add(hvs);
							}
							lookoutStatus=hvs.getLookoutStatusEnum().name(); // All lookout status' for a given user group should be the same.
						}
					}
				}
			}

			String docNum = "";
			String docType = "";
			Set<Document> docList =  paxIdKeyDocumentValueMap.get(passenger.getId());
			// Some passengers will not have documents.
			if (docList != null) {
				for (Document doc : paxIdKeyDocumentValueMap.get(passenger.getId())) {
					docNum = doc.getDocumentNumber();
					docType = doc.getDocumentType();
					if ("P".equalsIgnoreCase(doc.getDocumentType())) {
						break;
					}
				}
			}

			if(!hvsToUpdate.isEmpty()){ //This collection should be extremely small any time an update is actually required
				hitViewStatusRepository.saveAll(hvsToUpdate);
			}

			caseVo.setDocument(docNum);
			caseVo.setDocType(docType);
			hvsEnums.sort(Comparator.naturalOrder());
			caseVo.setlookoutStatus(lookoutStatus);
			caseVo.setStatus(hvsEnums.get(0).toString());
			caseVo.setGender(passenger.getPassengerDetails().getGender());
			caseVo.setHitNames(hitDetailsTitles);
			caseVo.setNationality(passenger.getPassengerDetails().getNationality());
			caseVo.setDob(passenger.getPassengerDetails().getDob());
			caseVo.setFirstName(passenger.getPassengerDetails().getFirstName());
			caseVo.setLastName(passenger.getPassengerDetails().getLastName());
			caseVo.setFlightId(passenger.getFlight().getId());
			caseVo.setPaxId(passenger.getId());
			caseVo.setFlightNumber(passenger.getFlight().getFullFlightNumber());
			caseVo.setFlightDirection(passenger.getFlight().getDirection());
			caseVo.setFlightETADate(passenger.getFlight().getMutableFlightDetails().getEta());
			caseVo.setFlightETDDate(passenger.getFlight().getMutableFlightDetails().getEtd());
			caseVo.setFlightOrigin(passenger.getFlight().getOrigin());
			caseVo.setFlightDestination(passenger.getFlight().getDestination());
			caseVo.setHighPrioHitCount(highPrioHitCount);
			caseVo.setMedPrioHitCount(medPrioHitCount);
			caseVo.setLowPrioHitCount(lowPrioHitCount);
			if (passenger.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()) {
				caseVo.maskPII();
			}
			caseVOS.add(caseVo);
		}
		return new PriorityVettingListDTO(caseVOS);
	}
	
	private Map<Long, HitCategory> getHitCategories(Set<Long> hitDetailIdSet) {
		Map<Long, HitCategory> objectMap = new HashMap<>();
		List<Object[]> oList = passengerRepository.getHitIdAndCategory(hitDetailIdSet);
		for (Object[] answerKey : oList) {
			Long hcId = (Long) answerKey[0];
			HitCategory object = (HitCategory) answerKey[1];
			objectMap.put(hcId, object);
		}
		return objectMap;
	}

	public Map<Long, Set<HitDetail>> createHitDetailMap(Set<Long> passengerIds) {
		Map<Long, Set<HitDetail>> objectMap = new HashMap<>();
		List<Object[]> oList = passengerRepository.getHitDetailsAndPaxId(passengerIds);
		for (Object[] answerKey : oList) {
			Long passengerId = (Long) answerKey[0];
			HitDetail object = (HitDetail) answerKey[1];
			processObject(object, objectMap, passengerId);
		}
		return objectMap;
	}

	public Map<Long, Set<Document>> createDocumentMap(Set<Long> passengerIds) {
		Map<Long, Set<Document>> objectMap = new HashMap<>();
		List<Object[]> oList = passengerRepository.getPaxIdAndDocuments(passengerIds);
		for (Object[] answerKey : oList) {
			Long passengerId = (Long) answerKey[0];
			Document object = (Document) answerKey[1];
			processObject(object, objectMap, passengerId);
		}
		return objectMap;
	}
	
	public Map<Long, Set<HitCategory>> createHitCategoryMap(Set<Long> passengerIds) {
		Map<Long, Set<HitCategory>> objectMap = new HashMap<>();
		List<Object[]> oList = passengerRepository.getPaxIdAndDocuments(passengerIds);
		for (Object[] answerKey : oList) {
			Long passengerId = (Long) answerKey[0];
			HitCategory object = (HitCategory) answerKey[1];
			processObject(object, objectMap, passengerId);
		}
		return objectMap;
	}

	public Map<Long, Set<HitViewStatus>> createHitViewMap(Set<Long> hdIds) {
		Map<Long, Set<HitViewStatus>> objectMap = new HashMap<>();
		List<Object[]> oList = passengerRepository.getHdIdAndHitViewStatus(hdIds);
		for (Object[] answerKey : oList) {
			Long hdId = (Long) answerKey[0];
			HitViewStatus object = (HitViewStatus) answerKey[1];
			processObject(object, objectMap, hdId);
		}
		return objectMap;
	}
	
	private static <T> void processObject(T type, Map<Long, Set<T>> map, Long passengerId) {
		if (map.containsKey(passengerId)) {
			map.get(passengerId).add(type);
		} else {
			Set<T> objectHashSet = new HashSet<>(map.values().size() * 50);
			objectHashSet.add(type);
			map.put(passengerId, objectHashSet);
		}
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
