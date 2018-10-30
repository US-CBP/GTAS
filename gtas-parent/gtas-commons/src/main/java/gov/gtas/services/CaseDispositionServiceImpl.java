/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import gov.gtas.repository.*;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import gov.gtas.constant.OneDayLookoutContants;
import gov.gtas.enumtype.CaseDispositionStatusEnum;
import gov.gtas.model.Attachment;
import gov.gtas.model.Case;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPax;
import gov.gtas.model.HitsDisposition;
import gov.gtas.model.HitsDispositionComments;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.model.lookup.CaseDispositionStatus;
import gov.gtas.model.lookup.DispositionStatusCode;
import gov.gtas.model.lookup.HitDispositionStatus;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.util.EntityResolverUtils;
import gov.gtas.vo.OneDayLookoutVo;
import gov.gtas.vo.passenger.AttachmentVo;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.HitsDispositionCommentsVo;
import gov.gtas.vo.passenger.HitsDispositionVo;

@Service
public class CaseDispositionServiceImpl implements CaseDispositionService {

	private static final Logger logger = LoggerFactory.getLogger(CaseDispositionServiceImpl.class);

	private static Set casesToCommit = new HashSet<Case>();
	private static final String INITIAL_COMMENT = "Initial Comment";
	private static final String UPDATED_BY_INTERNAL = "Internal";
	private static final String CASE_CREATION_MANUAL_DESC = "Agent Created Case";
	private static final String WL_ITEM_PREFIX = "wl_item";

	@Resource
	private CaseDispositionRepository caseDispositionRepository;
	@Resource
	private FlightRepository flightRepository;
	@Resource
	private PassengerRepository passengerRepository;
	@Resource
	private AttachmentRepository attachmentRepository;
	@Resource
	private HitDetailRepository hitDetailRepository;
	@Resource
	private RuleCatRepository ruleCatRepository;
	@Resource
	private HitDispositionStatusRepository hitDispRepo;
	@Resource
	private CaseDispositionStatusRepository caseDispositionStatusRepository;
	@Resource
	private HitsDispositionRepository hitsDispositionRepository;
	@Resource
	private HitsDispositionCommentsRepository hitsDispositionCommentsRepository;
	@Resource
	private FlightPaxRepository flightPaxRepository;
	@Autowired
	public RuleCatService ruleCatService;
    @Autowired
    private PassengerResolverService passengerResolverService;
	@Resource
	private AppConfigurationRepository appConfigurationRepository;

	public CaseDispositionServiceImpl() {
	}

	@Override
	public Case create(Long flight_id, Long pax_id, List<Long> hit_ids) {
		Case aCase = new Case();
		HitsDisposition hitDisp = new HitsDisposition();
		HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
		Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
		Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
		Long highPriorityRuleCatId = 1L;
		aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
		aCase.setFlightId(flight_id);
		aCase.setPaxId(pax_id);
		aCase.setStatus(DispositionStatusCode.NEW.toString());
		for (Long _tempHitId : hit_ids) {
			hitDisp = new HitsDisposition();
			hitsDispCommentsSet = new HashSet<>();
			hitDisp.setHitId(_tempHitId);
			hitDisp.setStatus(DispositionStatusCode.NEW.toString());
			hitsDispositionComments = new HitsDispositionComments();
			hitsDispositionComments.setHitId(_tempHitId);
			hitsDispositionComments.setComments(INITIAL_COMMENT);
			hitsDispCommentsSet.add(hitsDispositionComments);

			hitDisp.addHitsDispositionComments(hitsDispositionComments);
			// hitDisp.setDispComments(hitsDispCommentsSet);
			// for(HitsDispositionComments _tempDispComments :
			// hitsDispCommentsSet){hitDisp.addHitsDispositionComments(_tempDispComments);}
			hitsDispSet.add(hitDisp);
		}
		// aCase.setHitsDispositions(hitsDispSet);
		for (HitsDisposition _tempHit : hitsDispSet)
			aCase.addHitsDisposition(_tempHit);

		caseDispositionRepository.save(aCase);
		return aCase;
	}

	@Override
	public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String hitDesc,
			List<Long> hit_ids) {
		Case aCase = new Case();
		HitsDisposition hitDisp = new HitsDisposition();
		HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
		Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
		Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
		Long highPriorityRuleCatId = 1L;
		aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
		aCase.setFlightId(flight_id);
		aCase.setPaxId(pax_id);
		aCase.setPaxName(paxName);
		aCase.setPaxType(paxType);
		aCase.setStatus(DispositionStatusCode.NEW.toString());
		for (Long _tempHitId : hit_ids) {
			hitDisp = new HitsDisposition();
			hitsDispCommentsSet = new HashSet<>();
			hitDisp.setHitId(_tempHitId);
			hitDisp.setDescription(hitDesc);
			hitDisp.setStatus(DispositionStatusCode.NEW.toString());
			hitsDispositionComments = new HitsDispositionComments();
			hitsDispositionComments.setHitId(_tempHitId);
			hitsDispositionComments.setComments(INITIAL_COMMENT);
			// hitsDispCommentsSet.add(hitsDispositionComments);
			// hitDisp.setDispComments(hitsDispCommentsSet);
			hitDisp.addHitsDispositionComments(hitsDispositionComments);
			hitsDispSet.add(hitDisp);
		}
		// aCase.setHitsDispositions(hitsDispSet);
		for (HitsDisposition _tempHit : hitsDispSet)
			aCase.addHitsDisposition(_tempHit);

		caseDispositionRepository.save(aCase);
		return aCase;
	}

	@Override
	@Transactional
	public Case create(Long flight_id, Long pax_id, String paxName, String paxType, String citizenshipCountry, Date dob,
			String document, String hitDesc, List<Long> hit_ids) {

		Case aCase = new Case();
		Case _tempCase = null;
		Long highPriorityRuleCatId = 1L;
		HitsDisposition hitDisp = new HitsDisposition();
		HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
		Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
		Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
		aCase.setUpdatedAt(new Date());
		aCase.setUpdatedBy(UPDATED_BY_INTERNAL); // @ToDo change this to pass-by-value in the next release
		aCase.setFlightId(flight_id);
		aCase.setPaxId(pax_id);
		aCase.setPaxName(paxName);
		aCase.setPaxType(paxType);
		aCase.setCitizenshipCountry(citizenshipCountry);
		aCase.setDocument(document);
		aCase.setDob(dob);
		aCase.setStatus(DispositionStatusCode.NEW.toString());
		aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
		populatePassengerDetailsInCase(aCase, flight_id, pax_id);
		_tempCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);
		if (_tempCase != null && (_tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.NEW))
				|| _tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.OPEN)))) {
			aCase = _tempCase;
		}

		// redundant at this time
		// contextCases(aCase);

		for (Long _tempHitId : hit_ids) {
			hitDisp = new HitsDisposition();
			if (hitDesc.startsWith(WL_ITEM_PREFIX)) {
				pullRuleCategory(hitDisp, getRuleCatId(9999L));
				hitDesc = hitDesc.substring(7);
			} else
				pullRuleCategory(hitDisp, getRuleCatId(_tempHitId));
			highPriorityRuleCatId = getHighPriorityRuleCatId(_tempHitId);
			hitsDispCommentsSet = new HashSet<>();
			hitDisp.setHitId(_tempHitId);
			hitDisp.setDescription(hitDesc);
			hitDisp.setStatus(DispositionStatusCode.NEW.toString());
			hitDisp.setUpdatedAt(new Date());
			hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
			hitsDispositionComments = new HitsDispositionComments();
			hitsDispositionComments.setHitId(_tempHitId);
			hitsDispositionComments.setComments(INITIAL_COMMENT);
			hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
			hitsDispositionComments.setUpdatedAt(new Date());
			// hitsDispCommentsSet.add(hitsDispositionComments);
			// hitDisp.setDispComments(hitsDispCommentsSet);
			hitDisp.addHitsDispositionComments(hitsDispositionComments);
			hitsDispSet.add(hitDisp);
		}
		if (aCase.getHighPriorityRuleCatId() != null && aCase.getHighPriorityRuleCatId().equals(1L))
			aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
		else if (aCase.getHighPriorityRuleCatId() != null && (aCase.getHighPriorityRuleCatId() > highPriorityRuleCatId)
				&& highPriorityRuleCatId != 1)
			aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
		// if (aCase.getHitsDispositions() != null)
		// aCase.getHitsDispositions().addAll(hitsDispSet);
		// else aCase.setHitsDispositions(hitsDispSet);
		for (HitsDisposition _tempHit : hitsDispSet) {
			aCase.addHitsDisposition(_tempHit);
		}

		caseDispositionRepository.save(aCase);
		return aCase;
	}

	@Override
	public Case createManualCase(Long flight_id, Long pax_id, Long rule_cat_id, String comments, String username) {

		Case aCase = new Case();
		Long _tempHitIdForManualCase = 9999L;
		Case _tempCase = null;
		_tempCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);
		if (_tempCase != null && (_tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.NEW))
				|| _tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.OPEN)))) {
			aCase = _tempCase;
		}
		Long highPriorityRuleCatId = 1L;
		ArrayList<Long> hit_ids = new ArrayList<>();
		Passenger pax = passengerRepository.getPassengerById(pax_id);
		HitsDisposition hitDisp = new HitsDisposition();
		HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
		Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
		Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
		aCase.setUpdatedAt(new Date());
		aCase.setUpdatedBy(username);
		aCase.setFlightId(flight_id);
		aCase.setPaxId(pax_id);
		aCase.setPaxName(pax.getFirstName() + " " + pax.getLastName());
		populatePassengerDetailsInCase(aCase, flight_id, pax_id);
		aCase.setPaxType(pax.getPassengerType());
		aCase.setCitizenshipCountry(pax.getCitizenshipCountry());
		aCase.setDocument(((Document) pax.getDocuments().parallelStream().findFirst().orElse(new Document("xxxxxxxxx")))
				.getDocumentNumber());
		aCase.setDob(pax.getDob());
		aCase.setStatus(DispositionStatusCode.NEW.toString());

		hit_ids.add(_tempHitIdForManualCase); // Manual Distinction

		for (Long _tempHitId : hit_ids) {
			hitDisp = new HitsDisposition();
			// pullRuleCategory(hitDisp, rule_cat_id);
			// hitDisp.getRuleCat().setHitsDispositions(null);
			highPriorityRuleCatId = 1L;
			hitsDispCommentsSet = new HashSet<>();
			hitDisp.setHitId(_tempHitId);
			// pullRuleCategory(hitDisp, rule_cat_id);
			hitDisp.setDescription(CASE_CREATION_MANUAL_DESC);
			hitDisp.setStatus(DispositionStatusCode.NEW.toString());
			hitDisp.setUpdatedAt(new Date());
			hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
			RuleCat _tempRuleCat = ruleCatRepository.findOne(rule_cat_id);
			if (_tempRuleCat != null)
				hitDisp.setRuleCat(ruleCatRepository.findOne(rule_cat_id));
			hitsDispositionComments = new HitsDispositionComments();
			hitsDispositionComments.setHitId(_tempHitId);
			hitsDispositionComments.setComments(comments);
			hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
			hitsDispositionComments.setUpdatedAt(new Date());
			hitsDispCommentsSet.add(hitsDispositionComments);
			hitDisp.setDispComments(hitsDispCommentsSet);
			hitsDispSet.add(hitDisp);
		}

		if (aCase.getHighPriorityRuleCatId() == null)
			aCase.setHighPriorityRuleCatId(rule_cat_id);
		if (aCase.getHighPriorityRuleCatId() != null && aCase.getHighPriorityRuleCatId().equals(1L))
			aCase.setHighPriorityRuleCatId(rule_cat_id);
		else if (aCase.getHighPriorityRuleCatId() != null && (aCase.getHighPriorityRuleCatId() > rule_cat_id)
				&& rule_cat_id != 1)
			aCase.setHighPriorityRuleCatId(rule_cat_id);
		if (aCase.getHitsDispositions() != null)
			aCase.getHitsDispositions().addAll(hitsDispSet);
		else
			aCase.setHitsDispositions(hitsDispSet);

		//fix to adjust to the recent flight mappings
		aCase.setFlight(null);

		caseDispositionRepository.save(aCase);

		// _tempCase = null;
		// _tempCase = caseDispositionRepository.getOne(aCase.getId());
		//
		// if(_tempCase!=null){
		// for(HitsDisposition _tempHitDisp : _tempCase.getHitsDispositions()) {
		// if((_tempHitDisp.getHitId() == 9999L))
		// pullRuleCategory(_tempHitDisp, rule_cat_id);
		// }
		// caseDispositionRepository.save(_tempCase);
		// }
		return aCase;
	}

	@Override
	public Case createManualCaseAttachment(Long flight_id, Long pax_id, String paxName, String paxType,
			String citizenshipCountry, Date dob, String document, String hitDesc, List<Long> hit_ids, String username,
			MultipartFile fileToAttach) {

		Case aCase = new Case();
		Case _tempCase = null;
		Long highPriorityRuleCatId = 1L;
		HitsDisposition hitDisp = new HitsDisposition();
		HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
		Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
		Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();
		aCase.setUpdatedAt(new Date());
		aCase.setUpdatedBy(username);
		aCase.setFlightId(flight_id);
		aCase.setPaxId(pax_id);
		aCase.setPaxName(paxName);
		populatePassengerDetailsInCase(aCase, flight_id, pax_id);
		aCase.setPaxType(paxType);
		aCase.setCitizenshipCountry(citizenshipCountry);
		aCase.setDocument(document);
		aCase.setDob(dob);
		aCase.setStatus(DispositionStatusCode.NEW.toString());
		_tempCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);
		if (_tempCase != null && (_tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.NEW))
				|| _tempCase.getStatus().equalsIgnoreCase(String.valueOf(CaseDispositionStatusEnum.OPEN)))) {
			aCase = _tempCase;
		}

		// redundant at this time
		// contextCases(aCase);

		for (Long _tempHitId : hit_ids) {
			hitDisp = new HitsDisposition();
			pullRuleCategory(hitDisp, getRuleCatId(_tempHitId));
			highPriorityRuleCatId = 1L;
			hitsDispCommentsSet = new HashSet<>();
			hitDisp.setHitId(_tempHitId);
			hitDisp.setDescription(hitDesc);
			hitDisp.setStatus(DispositionStatusCode.NEW.toString());
			hitDisp.setUpdatedAt(new Date());
			hitDisp.setUpdatedBy(UPDATED_BY_INTERNAL);
			// RuleCat _tempRuleCat = ruleCatRepository.findOne(rule_cat_id);
			// if(_tempRuleCat!=null)hitDisp.setRuleCat(ruleCatRepository.findOne(rule_cat_id));
			hitsDispositionComments = new HitsDispositionComments();
			hitsDispositionComments.setHitId(_tempHitId);
			hitsDispositionComments.setComments(INITIAL_COMMENT);
			hitsDispositionComments.setUpdatedBy(UPDATED_BY_INTERNAL);
			hitsDispositionComments.setUpdatedAt(new Date());
			hitsDispCommentsSet.add(hitsDispositionComments);
			hitDisp.setDispComments(hitsDispCommentsSet);
			hitsDispSet.add(hitDisp);
		}
		aCase.setHighPriorityRuleCatId(highPriorityRuleCatId);
		if (aCase.getHitsDispositions() != null)
			aCase.getHitsDispositions().addAll(hitsDispSet);
		else
			aCase.setHitsDispositions(hitsDispSet);
		caseDispositionRepository.save(aCase);
		return aCase;
	}

	/**
	 * Utility method to manage cases to persist
	 *
	 * @param aCase
	 */
	private void contextCases(Case aCase) {
		try {
			if (casesToCommit != null) {
				final Case _tempCaseToCompare = aCase;
				Case existingCase = (Case) casesToCommit.stream().filter(x -> _tempCaseToCompare.equals(x)).findAny()
						.orElse(null);
				if (existingCase != null)
					aCase = existingCase;
				else
					casesToCommit.add(aCase);
			}
		} catch (Exception ex) {
			logger.error("Error in context cases!", ex);
		}
	}

	/**
	 * Utility method to pull Rule Cat
	 *
	 * @param hitDisp
	 * @param id
	 */
	private void pullRuleCategory(HitsDisposition hitDisp, Long id) {
		try {
			if (id == null || (ruleCatRepository.findOne(id) == null))
				hitDisp.setRuleCat(ruleCatRepository.findOne(1L));
			else
				hitDisp.setRuleCat(ruleCatRepository.findOne(id));
		} catch (Exception ex) {
			logger.error("error in pull rule category.", ex);
		}
	}

	/**
	 * @param ruleId
	 * @return
	 */
	private Long getHighPriorityRuleCatId(Long ruleId) {
		try {
			return ruleCatService.fetchRuleCatPriorityIdFromRuleId(ruleId);
		} catch (Exception ex) {
			logger.error("error in high priority rule cat id", ex);
		}
		return 1L;
	}

	private Long getRuleCatId(Long ruleId) {
		try {
			return ruleCatService.fetchRuleCatIdFromRuleId(ruleId);
		} catch (Exception ex) {
			logger.error("error in get rule cat id", ex);
		}
		return 1L;
	}

	@Override
	public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id) {

		Case aCase = new Case();
		HitsDisposition hitDisp = new HitsDisposition();
		HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
		Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
		Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

		aCase.setFlightId(flight_id);
		aCase.setPaxId(pax_id);
		aCase.setStatus(DispositionStatusCode.NEW.toString());
		// aCase.setHitsDispositions(hitsDispSet);
		for (HitsDisposition _tempHit : hitsDispSet)
			aCase.addHitsDisposition(_tempHit);

		caseDispositionRepository.save(aCase);
		return aCase;
	}

	@Override
	public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id, String caseComments, String status,
			String validHit, MultipartFile fileToAttach, String username,String caseDisposition) {
		Case aCase = new Case();
		HitsDisposition hitDisp = new HitsDisposition();
		HitsDispositionComments hitsDispositionComments = new HitsDispositionComments();
		Set<HitsDisposition> hitsDispSet = new HashSet<HitsDisposition>();
		Set<HitsDispositionComments> hitsDispCommentsSet = new HashSet<HitsDispositionComments>();

		try {
			aCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(flight_id, pax_id);

			if (aCase != null && status != null) { // set case status
				if (status.startsWith("Case"))
					aCase.setStatus(status.substring(4));
					aCase.setDisposition(caseDisposition);
			}
			hitsDispCommentsSet = null;
			hitsDispSet = aCase.getHitsDispositions();
			for (HitsDisposition hit : hitsDispSet) {

				// if ((hit.getCaseId() == aCase.getId()) && (hit_id != null) && (hit.getHitId()
				// == hit_id)) {
				// (hit.getaCase().getId() == aCase.getId()) &&
				if ((hit_id != null) && (hit.getId() == hit_id)) {

					if (caseComments != null) { // set comments
						hitsDispositionComments = new HitsDispositionComments();
						hitsDispositionComments.setHitId(hit_id);
						hitsDispositionComments.setComments(caseComments);
						hitsDispositionComments.setUpdatedBy(username);
						hitsDispositionComments.setUpdatedAt(new Date());
						hitsDispCommentsSet = hit.getDispComments();
						// check whether attachment exists, if yes, populate
						if (fileToAttach != null && !fileToAttach.isEmpty())
							populateAttachmentsToCase(fileToAttach, hitsDispositionComments, pax_id);
						hitsDispCommentsSet.add(hitsDispositionComments);
						hit.setDispComments(hitsDispCommentsSet);
					}

					if (status != null && !status.startsWith("Case")) { // set status
						hit.setStatus(status);
					}

					if (!(validHit == null)) {
						hit.setValid(validHit);
					}

				} // end of hit updates

			}

			// aCase.setHitsDispositions(hitsDispSet);
			for (HitsDisposition _tempHit : hitsDispSet)
				aCase.addHitsDisposition(_tempHit);

			if ((status != null) || (caseComments != null) || (validHit != null))
				caseDispositionRepository.save(aCase);

		} catch (Exception ex) {
			logger.error("Error adding case comments: ", ex);
		}

		aCase.getHitsDispositions().stream().forEach(x -> x.setaCase(null));	
		
		return aCase;
	}

	/**
	 * Utility method to persist attachment to each case comment
	 * 
	 * @param file
	 * @param _tempHitsDispComments
	 * @throws Exception
	 */
	private void populateAttachmentsToCase(MultipartFile file, HitsDispositionComments _tempHitsDispComments,
			Long pax_id) throws Exception {

		Attachment attachment = new Attachment();
		// Build attachment to be added to pax
		Set<Attachment> _tempAttachSet = new HashSet<Attachment>();
		if (_tempHitsDispComments.getAttachmentSet() != null)
			_tempAttachSet = _tempHitsDispComments.getAttachmentSet();
		attachment.setContentType(file.getContentType());
		attachment.setFilename(file.getOriginalFilename());
		attachment.setName(file.getName());
		byte[] bytes = file.getBytes();
		Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
		attachment.setContent(blob);
		// Grab pax to add attachment to it
		Passenger pax = passengerRepository.findOne(pax_id);
		attachment.setPassenger(pax);
		attachmentRepository.save(attachment);
		_tempAttachSet.add(attachment);
		_tempHitsDispComments.setAttachmentSet(_tempAttachSet);

	}

	@Override
	public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, Long hit_id) {
		List<Case> _tempCaseList = new ArrayList<>();
		List<Long> _tempHitIds = new ArrayList<>();

		_tempHitIds.add(hit_id);
		_tempCaseList.add(create(flight_id, pax_id, _tempHitIds));

		return _tempCaseList;
	}

	@Override
	public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType,
			String hitDesc, Long hit_id) {
		List<Case> _tempCaseList = new ArrayList<>();
		List<Long> _tempHitIds = new ArrayList<>();

		_tempHitIds.add(hit_id);
		_tempCaseList.add(create(flight_id, pax_id, paxName, paxType, hitDesc, _tempHitIds));

		return _tempCaseList;
	}

	@Override
	public Passenger findPaxByID(Long id) {
		return passengerRepository.findOne(id);
	}

	@Override
	public Flight findFlightByID(Long id) {
		return flightRepository.findOne(id);
	}

	@Override
	public List<Case> registerCasesFromRuleService(Long flight_id, Long pax_id, String paxName, String paxType,
			String citizenshipCountry, Date dob, String document, String hitDesc, Long hit_id) {
		List<Case> _tempCaseList = new ArrayList<>();
		List<Long> _tempHitIds = new ArrayList<>();

		_tempHitIds.add(hit_id);
		_tempCaseList.add(
				create(flight_id, pax_id, paxName, paxType, citizenshipCountry, dob, document, hitDesc, _tempHitIds));

		return _tempCaseList;
	}

	@Override
	public CasePageDto findHitsDispositionByCriteria(CaseRequestDto dto) {

		Case aCase = caseDispositionRepository.getCaseByFlightIdAndPaxId(dto.getFlightId(), dto.getPaxId());

		List<CaseVo> vos = new ArrayList<>();
		CaseVo vo = new CaseVo();
		vo.setHitsDispositions(aCase.getHitsDispositions());
		vo.setHitsDispositionVos(returnHitsDisposition(aCase.getHitsDispositions()));
		CaseDispositionServiceImpl.copyIgnoringNullValues(aCase, vo);
		manageHitsDispositionCommentsAttachments(vo.getHitsDispositions());
		vos.add(vo);
		return new CasePageDto(vos, new Long(1L));
	}

	/**
	 * @param dto
	 * @return
	 */
	@Override
	public CasePageDto findAll(CaseRequestDto dto) 
        {
            List<CaseVo> vos = new ArrayList<>();

            CasePageDto casePageDto = null;

            Pair<Long, List<Case>> tuple2 = caseDispositionRepository.findByCriteria(dto);
            for (Case f : tuple2.getRight()) {
                    CaseVo vo = new CaseVo();
                    CaseDispositionServiceImpl.copyIgnoringNullValues(f, vo);
                    vo.setCurrentTime(new Date());
                    vo.setFlightDirection(f.getFlight().getDirection());
                    vo.setCountdownTime(f.getCountdown().getTime());
                    vo = calculateCountDownDisplayString(vo);
                    vos.add(vo);
            }

            casePageDto = new CasePageDto(vos, tuple2.getLeft()); 

            return casePageDto;
	}
        
        private CaseVo calculateCountDownDisplayString(CaseVo caseVo)
        {
            Long etdEtaDateTime = caseVo.getCountdownTime();
            Long currentTimeMillis = caseVo.getCurrentTime().getTime();
           
            Long countDownMillis = etdEtaDateTime - currentTimeMillis;
            Long countDownSeconds = countDownMillis/1000;
            
            Long daysLong = countDownSeconds/86400;
            Long secondsRemainder1 = countDownSeconds % 86400;
            Long hoursLong = secondsRemainder1/3600;
            Long secondsRemainder2 = secondsRemainder1 % 3600;
            Long minutesLong = secondsRemainder2/60;
            
            String daysString = (countDownSeconds < 0 && daysLong.longValue() == 0) ? "-" + daysLong.toString() : daysLong.toString();
            
            String countDownString = daysString + "d " + Math.abs(hoursLong) + "h " + Math.abs(minutesLong) + "m";
            caseVo.setCountDownTimeDisplay(countDownString);

            return caseVo;
        }
        
        @Override
        public Date getCurrentServerTime()
        {
            Date currentTime = new Date();
            return currentTime;
        }

	/**
	 * Utility method to fetch model object
	 *
	 * @param _tempHitsDispositionSet
	 * @return
	 */
	private Set<HitsDispositionVo> returnHitsDisposition(Set<HitsDisposition> _tempHitsDispositionSet) {

		Set<HitsDispositionVo> _tempReturnHitsDispSet = new HashSet<HitsDispositionVo>();
		Set<RuleCat> _tempRuleCatSet = new HashSet<RuleCat>();
		HitsDispositionVo _tempHitsDisp = new HitsDispositionVo();
		RuleCat _tempRuleCat = new RuleCat();
		Set<AttachmentVo> _tempAttachmentVoSet = new HashSet<AttachmentVo>();
		Set<HitsDispositionCommentsVo> _tempHitsDispCommentsVoSet = new HashSet<HitsDispositionCommentsVo>();
		HitsDispositionCommentsVo _tempDispCommentsVo = new HitsDispositionCommentsVo();

		try {
			for (HitsDisposition hitDisp : _tempHitsDispositionSet) {
				_tempHitsDisp = new HitsDispositionVo();
				_tempRuleCat = new RuleCat();
				_tempHitsDispCommentsVoSet = new HashSet<HitsDispositionCommentsVo>();
				_tempAttachmentVoSet = new HashSet<AttachmentVo>();

				CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp, _tempHitsDisp);
				_tempHitsDisp.setHit_disp_id(hitDisp.getId());
				if (hitDisp.getRuleCat() != null) {
					CaseDispositionServiceImpl.copyIgnoringNullValues(hitDisp.getRuleCat(), _tempRuleCat);
					// _tempRuleCat.setHitsDispositions(null);
				}
				_tempRuleCatSet.add(_tempRuleCat);
				_tempHitsDisp.setCategory(_tempRuleCat.getCategory());
				_tempHitsDisp.setRuleCatSet(_tempRuleCatSet);

				// begin to retrieve attachments
				if (hitDisp.getDispComments() != null) {
					Set<HitsDispositionComments> _tempDispCommentsSet = hitDisp.getDispComments();
					for (HitsDispositionComments _tempComments : _tempDispCommentsSet) {
						_tempDispCommentsVo = new HitsDispositionCommentsVo();
						_tempAttachmentVoSet = new HashSet<AttachmentVo>();
						CaseDispositionServiceImpl.copyIgnoringNullValues(_tempComments, _tempDispCommentsVo);
						_tempHitsDispCommentsVoSet.add(_tempDispCommentsVo);

						if (_tempComments.getAttachmentSet() != null) {

							for (Attachment a : _tempComments.getAttachmentSet()) {
								AttachmentVo attVo = new AttachmentVo();
								// Turn blob into byte[], as input stream is not serializable
								attVo.setContent(a.getContent().getBytes(1, (int) a.getContent().length()));
								attVo.setId(a.getId());
								attVo.setContentType(a.getContentType());
								attVo.setDescription(a.getDescription());
								attVo.setFilename(a.getFilename());
								// Drop blob from being held in memory after each set
								a.getContent().free();
								// Add to attVoList to be returned to front-end
								a.setPassenger(null);
								_tempAttachmentVoSet.add(attVo);
							}

						}
						_tempDispCommentsVo.setAttachmentSet(_tempAttachmentVoSet);
					}
					_tempHitsDisp.setDispCommentsVo(_tempHitsDispCommentsVoSet);

				} // end

				_tempReturnHitsDispSet.add(_tempHitsDisp);
			}
		} catch (Exception ex) {
			logger.error("error returning hits disposition.", ex);
		}
		// _tempReturnHitsDispSet =
		// _tempReturnHitsDispSet.stream().sorted(Comparator.comparing(HitsDispositionVo::getHit_disp_id)).collect(Collectors.toSet());
		List<HitsDispositionVo> _tempArrList = _tempReturnHitsDispSet.stream()
				.sorted(Comparator.comparing(HitsDispositionVo::getHit_disp_id)).collect(Collectors.toList());
		return new HashSet<>(_tempReturnHitsDispSet.stream()
				.sorted(Comparator.comparing(HitsDispositionVo::getHit_disp_id)).collect(Collectors.toList()));
		// return _tempReturnHitsDispSet;
	}

	/**
	 * Utility method to fetch model object
	 *
	 * @param _tempHitsDispositionSet
	 * @return
	 */
	private Set<HitsDispositionVo> manageHitsDispositionCommentsAttachments(
			Set<HitsDisposition> _tempHitsDispositionSet) {

		Set<HitsDispositionVo> _tempReturnHitsDispSet = new HashSet<HitsDispositionVo>();
		Set<Attachment> _tempAttachmentSet = new HashSet<Attachment>();
		HitsDispositionVo _tempHitsDisp = new HitsDispositionVo();
		RuleCat _tempRuleCat = new RuleCat();
		try {
			for (HitsDisposition hitDisp : _tempHitsDispositionSet) {
				_tempHitsDisp = new HitsDispositionVo();
				if (hitDisp.getDispComments() != null) {
					Set<HitsDispositionComments> _tempDispCommentsSet = hitDisp.getDispComments();
					for (HitsDispositionComments _tempComments : _tempDispCommentsSet) {
						if (_tempComments.getAttachmentSet() != null) {

							for (Attachment _tempAttach : _tempComments.getAttachmentSet()) {
								_tempAttach.setPassenger(null);
							}
						}
					}
				}

			}
		} catch (Exception ex) {
			logger.error("Error in manage hits disposition comments.", ex);
		}
		return _tempReturnHitsDispSet;
	}

	/**
	 * Utility method to pull passenger details for the cases view
	 *
	 * @param aCaseVo
	 * @param flightId
	 * @param paxId
	 */
	private void populatePassengerDetails(CaseVo aCaseVo, Long flightId, Long paxId) {
		Passenger _tempPax = findPaxByID(paxId);
		Flight _tempFlight = findFlightByID(flightId);
		if (_tempPax != null) {
			aCaseVo.setFirstName(_tempPax.getFirstName());
			aCaseVo.setLastName(_tempPax.getLastName());
		}
		if (_tempFlight != null) {
			aCaseVo.setFlightNumber(_tempFlight.getFlightNumber());
		}
	}

	/**
	 * Utility method to pull passenger details for the cases view
	 *
	 * @param aCase
	 * @param flightId
	 * @param paxId
	 */
	private void populatePassengerDetailsInCase(Case aCase, Long flightId, Long paxId) {
		Passenger _tempPax = findPaxByID(paxId);
		Flight _tempFlight = findFlightByID(flightId);
		if (_tempPax != null) {
			aCase.setFirstName(_tempPax.getFirstName());
			aCase.setLastName(_tempPax.getLastName());
		}
		if (_tempFlight != null) {
			aCase.setFlightNumber(_tempFlight.getFlightNumber());
                        aCase.setFlightETADate(_tempFlight.getEta());
			aCase.setFlightETDDate(_tempFlight.getEtd());
		}
	}

	/**
	 * Static utility method to ignore nulls while copying
	 *
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/**
	 * Wrapper method over BeanUtils.copyProperties
	 *
	 * @param src
	 * @param target
	 */
	public static void copyIgnoringNullValues(Object src, Object target) {
		try {
			BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
		} catch (Exception ex) {
			logger.error("error copying object", ex);
		}
	}

	@Override
	public List<HitDispositionStatus> getHitDispositionStatuses() {
		Iterable<HitDispositionStatus> i = hitDispRepo.findAll();
		if (i != null) {
			return IteratorUtils.toList(i.iterator());
		}
		return new ArrayList<>();
	}

	@Override
	public List<OneDayLookoutVo> getOneDayLookoutByDate(Date date) {

		List<OneDayLookoutVo> oneDayLookoutVoList = null;
		// set start date
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
		Date startDate = cal.getTime();
		// set end date
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
		Date endDate = cal.getTime();

		List<Case> oneDayLookoutResult = caseDispositionRepository.findOneDayLookoutByDate(startDate, endDate);

		if (oneDayLookoutResult == null || oneDayLookoutResult.isEmpty()) {
			return new ArrayList<OneDayLookoutVo>();
		}

		else {

			oneDayLookoutVoList = getOneDaylookoutVo(oneDayLookoutResult);
			FlightPax flightPax = null;
			for (OneDayLookoutVo oneDayLookoutVo : oneDayLookoutVoList) {
				if (oneDayLookoutVo.getPaxId() != null)
					flightPax = flightPaxRepository.findOne(oneDayLookoutVo.getPaxId());

				if (flightPax != null) {
					if (flightPax.getPassenger().getId() != null)
						oneDayLookoutVo.setPassengerId(flightPax.getPassenger().getId());

				}

			}
		}

		return oneDayLookoutVoList;
	}

	private List<OneDayLookoutVo> getOneDaylookoutVo(List<Case> oneDayLookoutList) {

		List<OneDayLookoutVo> oneDayLookoutVoList = new ArrayList<OneDayLookoutVo>();

		OneDayLookoutVo oneDayLookoutVo = null;
		String etaEtdTime = null;
		Date etaEtdDate = null;
		Calendar calendar = null;
		for (Case oneDayLookoutCase : oneDayLookoutList) {
			etaEtdTime = null;
			calendar = null;
			oneDayLookoutVo = new OneDayLookoutVo();
			oneDayLookoutVo.setDocument(oneDayLookoutCase.getDocument());
			oneDayLookoutVo.setFirstName(oneDayLookoutCase.getFirstName());
			oneDayLookoutVo.setLastName(oneDayLookoutCase.getLastName());
			oneDayLookoutVo.setName(oneDayLookoutCase.getLastName() + ", " + oneDayLookoutCase.getFirstName());

			// set flight information
			if (oneDayLookoutCase.getFlight() != null) {
				oneDayLookoutVo.setFlightNumber(oneDayLookoutCase.getFlight().getFlightNumber());
				oneDayLookoutVo.setFullFlightNumber(oneDayLookoutCase.getFlight().getFullFlightNumber());
				oneDayLookoutVo.setPaxId(oneDayLookoutCase.getPaxId());
				oneDayLookoutVo.setFlightId(oneDayLookoutCase.getFlightId());
				String origDestFlightsStr = oneDayLookoutCase.getFlight().getOrigin() + "/" + oneDayLookoutCase.getFlight().getDestination();
                                oneDayLookoutVo.setOrigDestAirportsStr(origDestFlightsStr);

				if (oneDayLookoutCase.getFlight().getDirection() != null) {

					// set eta/etd time and direction
					if (oneDayLookoutCase.getFlight().getDirection()
							.equalsIgnoreCase(OneDayLookoutContants.FLIGHT_DIRECTION_INCOMING)) {
						oneDayLookoutVo.setDirection(OneDayLookoutContants.FLIGHT_DIRECTION_INCOMING_DESC);
						etaEtdDate = oneDayLookoutCase.getFlight().getEta();
						if (etaEtdDate != null) {
							calendar = Calendar.getInstance();
							calendar.setTime(etaEtdDate);
							etaEtdTime = String.format("%02d", Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))
									+ String.format("%02d", Integer.valueOf(calendar.get(Calendar.MINUTE)));
							oneDayLookoutVo.setEtaEtdTime(etaEtdTime);
						}

					} else if (oneDayLookoutCase.getFlight().getDirection()
							.equalsIgnoreCase(OneDayLookoutContants.FLIGHT_DIRECTION_OUTGOING)) {
						oneDayLookoutVo.setDirection(OneDayLookoutContants.FLIGHT_DIRECTION_OUTGOING_DESC);
						etaEtdDate = oneDayLookoutCase.getFlight().getEtd();
						calendar = Calendar.getInstance();
						calendar.setTime(etaEtdDate);
						etaEtdTime = String.format("%02d", Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))
								+ String.format("%02d", Integer.valueOf(calendar.get(Calendar.MINUTE)));
						oneDayLookoutVo.setEtaEtdTime(etaEtdTime);
					}

				}
			}
			oneDayLookoutVoList.add(oneDayLookoutVo);
		}

		return oneDayLookoutVoList;
	}

	
	public Boolean updateDayLookoutFlag(Long caseId, Boolean flag) {
		
		boolean result = false;
		
		try
		{
			caseDispositionRepository.updateOneDayLookoutFlag(caseId, flag);
			result = true;
		}
		catch (Exception e)
		{
			logger.error("An Error has occurred when updating one day lookout flag for CASE ID: " + caseId+ " with flag: "+ flag );
			logger.error(e.getMessage(), e);
		}
			return result;
	}

	@Override
	public List<Case> getCaseByPaxId(List<Long> paxIds) {
		//
		return this.caseDispositionRepository.getCaseByPaxId(paxIds);
	}

	@Override
	public List<Case> getCaseHistoryByPaxId(Long paxId) {
		
		List<Long> pax_group = this.passengerResolverService.resolve(paxId);
    	
    	return this.getCaseByPaxId(pax_group);
	}

        // returns version with TRUE flag, apisOnlyFlag;apisVersion, e.g. TRUE;16B or FALSE
	@Override
        public String getAPISOnlyFlagAndVersion()
        {
            String apisReturnStr = "";
            AppConfiguration appConfiguration = appConfigurationRepository.findByOption(AppConfigurationRepository.APIS_ONLY_FLAG);
            String apisOnlyFlag = (appConfiguration != null) ? appConfiguration.getValue() : "FALSE";
            if (apisOnlyFlag.equals("TRUE"))
            {
                appConfiguration = appConfigurationRepository.findByOption(AppConfigurationRepository.APIS_VERSION);
                String apisVersion = (appConfiguration != null) ? appConfiguration.getValue() : "";
                apisReturnStr = apisOnlyFlag + ";" + apisVersion;
            }
            else
            {
               apisReturnStr =  apisOnlyFlag;
            }
            return apisReturnStr;
	}

	@Override
	public List<CaseDispositionStatus> getCaseDispositionStatuses() {
		Iterable<CaseDispositionStatus> i = caseDispositionStatusRepository.findAll();
		if (i != null) {
			return IteratorUtils.toList(i.iterator());
		}
		return new ArrayList<>();
	}
        
}