/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.constant.GtasSecurityConstants.GTAS_APPLICATION_USERID;
import gov.gtas.bo.CompositeRuleServiceResult;
import gov.gtas.bo.RuleExecutionStatistics;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.bo.TargetDetailVo;
import gov.gtas.bo.TargetSummaryVo;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.RuleConstants;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.error.CommonServiceException;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.repository.HitsSummaryRepository;
import gov.gtas.repository.MessageRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PnrRepository;
import gov.gtas.repository.udr.UdrRuleRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.rule.RuleService;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.HitsSummaryService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.WhitelistService;
import gov.gtas.services.security.UserService;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.TargetingResultUtils;
import gov.gtas.svc.util.TargetingServiceUtils;
import gov.gtas.vo.WhitelistVo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of the Targeting Service API.
 */
@Service
public class TargetingServiceImpl implements TargetingService {

	private static final Logger logger = LoggerFactory
			.getLogger(TargetingServiceImpl.class);

	private final String HITS_REASONS_SEPARATOR = "$$$";

	/* The rule engine to be used. */
	private final RuleService ruleService;

	@Autowired
	private MessageRepository<Message> messageRepository;

	@Autowired
	private ApisMessageRepository apisMsgRepository;

	@Autowired
	private PnrRepository pnrMsgRepository;

	@Autowired
	private HitsSummaryRepository hitsSummaryRepository;

	@Autowired
	private HitsSummaryService hitsSummaryService;

	@Autowired
	private FlightRepository flightRepository;

	@Autowired
	private PassengerRepository passengerRepository;

	@Autowired
	private AuditLogPersistenceService auditLogPersistenceService;

	@Value("${hibernate.jdbc.batch_size}")
	private String batchSize;

	@Autowired
	private HitDetailRepository hitDetailRepository;

	@Autowired
	private RulePersistenceService rulePersistenceService;

	@Autowired
	private WatchlistItemRepository watchlistItemRepository;

	@Autowired
	private UdrRuleRepository udrRuleRepository;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private AuditRecordRepository auditLogRepository;

	@Autowired
	private WhitelistService whitelistService;

	@Autowired
	private UserService userService;

	/**
	 * Constructor obtained from the spring context by auto-wiring.
	 * 
	 * @param rulesvc
	 *            the auto-wired rule engine instance.
	 */
	@Autowired
	public TargetingServiceImpl(final RuleService rulesvc) {
		ruleService = rulesvc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.svc.TargetingService#analyzeApisMessage(gov.gtas.model.ApisMessage
	 * )
	 */
	@Override
	@Transactional
	public RuleServiceResult analyzeApisMessage(ApisMessage message) {
		logger.info("Entering analyzeApisMessage().");

		if (null == message) {
			logger.error("message is null.");
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"ApisMessage", "TargetingServiceImpl.analyzeApisMessage()");
		}

		RuleServiceRequest req = TargetingServiceUtils.createApisRequest(
				message).getRuleServiceRequest();
		RuleServiceResult res = ruleService.invokeRuleEngine(req);
		res = TargetingResultUtils.ruleResultPostProcesssing(res);
		logger.info("Exiting analyzeApisMessage().");
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.svc.TargetingService#applyRules(gov.gtas.bo.RuleServiceRequest,
	 * java.lang.String)
	 */
	@Override
	public RuleServiceResult applyRules(RuleServiceRequest request,
			String drlRules) {
		RuleServiceResult res = ruleService.invokeAdhocRulesFromString(
				drlRules, request);
		res = TargetingResultUtils.ruleResultPostProcesssing(res);
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#analyzeApisMessage(long)
	 */
	@Override
	@Transactional
	public RuleServiceResult analyzeApisMessage(long messageId) {
		ApisMessage msg = apisMsgRepository.findOne(messageId);
		if (msg == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					RuleServiceConstants.MESSAGE_NOT_FOUND_ERROR_CODE,
					messageId);
		}
		RuleServiceResult res = this.analyzeApisMessage(msg);
		res = TargetingResultUtils.ruleResultPostProcesssing(res);
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#analyzeLoadedApisMessage()
	 */
	@Override
	@Transactional
	public List<RuleHitDetail> analyzeLoadedApisMessage() {
		List<RuleHitDetail> ret = null;
		List<ApisMessage> msgs = this.retrieveApisMessage(MessageStatus.LOADED);
		if (msgs != null) {
			RuleExecutionContext ctx = TargetingServiceUtils
					.createApisRequestContext(msgs);
			RuleServiceResult res = ruleService.invokeRuleEngine(ctx
					.getRuleServiceRequest());
			res = TargetingResultUtils.ruleResultPostProcesssing(res);
			ret = res.getResultList();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#analyzeLoadedPnr()
	 */
	@Override
	@Transactional
	public List<RuleHitDetail> analyzeLoadedPnr() {
		List<RuleHitDetail> ret = null;
		List<Pnr> msgs = this.retrievePnr(MessageStatus.LOADED);
		if (msgs != null) {
			RuleExecutionContext ctx = TargetingServiceUtils
					.createPnrRequestContext(msgs);
			RuleServiceResult res = ruleService.invokeRuleEngine(ctx
					.getRuleServiceRequest());
			res = TargetingResultUtils.ruleResultPostProcesssing(res);
			ret = res.getResultList();
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#preProcessing()
	 */
	@Override
	@Transactional
	public void preProcessing() {
		logger.info("Entering preProcessing()");
		// check if there are rules (undeleted & enabled)
		List<UdrRule> ruleList = udrRuleRepository.findByDeletedAndEnabled(
				YesNoEnum.N, YesNoEnum.Y);

		Iterable<WatchlistItem> all = watchlistItemRepository.findAll();
		List<WatchlistItem> target = new ArrayList<>();
		all.forEach(target::add);

		if (!CollectionUtils.isEmpty(ruleList)
				|| !CollectionUtils.isEmpty(target)) {
			logger.info("Db operations...");
			Iterator<Message> source = messageRepository.findByStatus(
					MessageStatus.LOADED).iterator();

			List<Message> loadedMessages = new ArrayList<>();
			source.forEachRemaining(loadedMessages::add);
			Set<Flight> flights = new HashSet<>();
			Set<Passenger> passengers = new HashSet<>();
			if (!loadedMessages.isEmpty()) {
				logger.info("Loaded messages size -->" + loadedMessages.size());
				for (Message message : loadedMessages) {
					if (message instanceof ApisMessage) {
						ApisMessage apisMsg = (ApisMessage) message;
						flights = apisMsg.getFlights();
						passengers = apisMsg.getPassengers();
					} else if (message instanceof Pnr) {
						Pnr pnrMsg = (Pnr) message;
						flights = pnrMsg.getFlights();
						passengers = pnrMsg.getPassengers();
					}
					deleteRelatedRecords(ruleList, target, flights, passengers);
				}
			}
		}
		logger.info("Exiting preProcessing()");
	}

	/**
	 * Delete related records.
	 *
	 * @param ruleList
	 *            the rule list
	 * @param target
	 *            the target
	 * @param flights
	 *            the flights
	 * @param passengers
	 *            the passengers
	 */
	@Transactional
	private void deleteRelatedRecords(List<UdrRule> ruleList,
			List<WatchlistItem> target, Set<Flight> flights,
			Set<Passenger> passengers) {
		for (Flight f : flights) {
			for (Passenger p : passengers) {
				// /
				if (CollectionUtils.isEmpty(ruleList)
						&& !CollectionUtils.isEmpty(target)) {
					// deleted anything with Watchlist
					List<HitsSummary> hitsD = hitsSummaryService
							.findByFlightIdAndPassengerIdAndWL(f.getId(),
									p.getId());
					for (HitsSummary hs : hitsD) {
						hitDetailRepository.deleteDBData(hs.getId());
						hitsSummaryRepository.deleteDBData(hs.getId());
					}
					// deleted anything with combined rules
					List<HitsSummary> hitsWithCombined = hitsSummaryService
							.findByFlightIdAndPassengerIdAndCombinedWithUdrRule(
									f.getId(), p.getId());
					for (HitsSummary hs : hitsWithCombined) {
						hitDetailRepository.deleteDBData(hs.getId());
						hitsSummaryRepository.deleteDBData(hs.getId());
					}

				} else if (!CollectionUtils.isEmpty(ruleList)
						&& CollectionUtils.isEmpty(target)) {
					List<HitsSummary> hitsR = hitsSummaryService
							.findByFlightIdAndPassengerIdAndUdrRule(f.getId(),
									p.getId());
					List<String> enable = hitsSummaryRepository
							.enableFlagByUndeletedAndEnabledRule(f.getId(),
									p.getId());
					for (HitsSummary hs : hitsR) {
						if (enable.contains(YesNoEnum.Y)) {
							hitDetailRepository.deleteDBData(hs.getId());
							hitsSummaryRepository.deleteDBData(hs.getId());
						}
					}
				} else if (!CollectionUtils.isEmpty(ruleList)
						&& !CollectionUtils.isEmpty(target)) {
					//
					List<HitsSummary> hitsD = hitsSummaryService
							.findByFlightIdAndPassengerIdAndWL(f.getId(),
									p.getId());
					for (HitsSummary hs : hitsD) {
						hitDetailRepository.deleteDBData(hs.getId());
						hitsSummaryRepository.deleteDBData(hs.getId());
					}
					//
					List<HitsSummary> hitsR = hitsSummaryService
							.findByFlightIdAndPassengerIdAndUdrRule(f.getId(),
									p.getId());
					List<String> enable = hitsSummaryRepository
							.enableFlagByUndeletedAndEnabledRule(f.getId(),
									p.getId());
					for (HitsSummary hs : hitsR) {
						if (enable.contains(YesNoEnum.Y)) {
							hitDetailRepository.deleteDBData(hs.getId());
							hitsSummaryRepository.deleteDBData(hs.getId());
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#analyzeLoadedMessages(boolean)
	 */
	@Override
	@Transactional
	public RuleExecutionContext analyzeLoadedMessages(
			final boolean updateProcesssedMessageStat) {
		logger.info("Entering analyzeLoadedMessages()");
		Iterator<Message> source = messageRepository.findByStatus(
				MessageStatus.LOADED).iterator();
		List<Message> target = new ArrayList<>();
		source.forEachRemaining(target::add);
		RuleExecutionContext ctx = null;
		try {
			ctx = executeRules(target);
			logger.info("updating messages status from loaded to analyzed.");
			if (updateProcesssedMessageStat) {
				for (Message message : target) {
					message.setStatus(MessageStatus.ANALYZED);
				}
			}
			logger.info("Exiting analyzeLoadedMessages()");
			return ctx;
		} catch (CommonServiceException cse) {
			if (cse.getErrorCode().equals(
					RuleServiceConstants.KB_NOT_FOUND_ERROR_CODE)
					|| cse.getErrorCode().equals(
							RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE)) {
				logger.info("************************");
				logger.info(cse.getMessage());
				logger.info("************************");
			} else {
				throw cse;
			}
		}
		return ctx;
	}

	/**
	 * Execute rules.
	 *
	 * @param target
	 *            the target
	 * @return the rule execution context
	 */
	private RuleExecutionContext executeRules(List<Message> target) {
		logger.debug("Entering executeRules().");

		RuleExecutionContext ctx = TargetingServiceUtils
				.createPnrApisRequestContext(target);
		logger.debug("Running Rule set.");
		// default knowledge Base is the UDR KB
		RuleServiceResult udrResult = ruleService.invokeRuleEngine(ctx
				.getRuleServiceRequest());

		RuleServiceResult wlResult = ruleService.invokeRuleEngine(
				ctx.getRuleServiceRequest(),
				WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);

		if (udrResult == null && wlResult == null) {
			// currently only two knowledgebases: udr and Watchlist
			KnowledgeBase udrKb = rulePersistenceService
					.findUdrKnowledgeBase(RuleConstants.UDR_KNOWLEDGE_BASE_NAME);
			if (udrKb == null) {
				KnowledgeBase wlKb = rulePersistenceService
						.findUdrKnowledgeBase(WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
				if (wlKb == null) {
					throw ErrorHandlerFactory
							.getErrorHandler()
							.createException(
									RuleServiceConstants.KB_NOT_FOUND_ERROR_CODE,
									RuleConstants.UDR_KNOWLEDGE_BASE_NAME
											+ "/"
											+ WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
				} else { // No enabled but disabled wl rule exists
					throw ErrorHandlerFactory
							.getErrorHandler()
							.createException(
									RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE,
									RuleServiceConstants.NO_ENABLED_RULE_ERROR_MESSAGE);
				}
			} else { // No enabled but disabled udr rule exists
				throw ErrorHandlerFactory.getErrorHandler().createException(
						RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE,
						RuleServiceConstants.NO_ENABLED_RULE_ERROR_MESSAGE);
			}
		}

		// eliminate duplicates
		if (udrResult != null) {
			logger.debug("Eliminate duplicates from UDR rule running.");
			udrResult = TargetingResultUtils
					.ruleResultPostProcesssing(udrResult);
		}
		if (wlResult != null) {
			logger.debug("Eliminate duplicates from watchlist rule running.");
			wlResult = TargetingResultUtils.ruleResultPostProcesssing(wlResult);
		}

		TargetingResultUtils.updateRuleExecutionContext(ctx,
				new CompositeRuleServiceResult(udrResult, wlResult));

		logger.debug("Exiting executeRules().");
		return ctx;
	}

	/**
	 * Filtering whitelist.
	 *
	 * @param wVos
	 *            the w vos
	 * @param passenger
	 *            the passenger
	 * @return true, if successful
	 */
	private boolean filteringWhitelist(List<WhitelistVo> wVos,
			Passenger passenger) {
		if (wVos.isEmpty()) return false;
		Set<Document> docs = passenger.getDocuments();
		List<WhitelistVo> pwlVos = new ArrayList<>();
		docs.forEach(doc -> {
			WhitelistVo pwl = new WhitelistVo();
			pwl.setFirstName(passenger.getFirstName());
			pwl.setMiddleName(passenger.getMiddleName());
			pwl.setLastName(passenger.getLastName());
			pwl.setGender(passenger.getGender());
			pwl.setDob(passenger.getDob());
			pwl.setCitizenshipCountry(passenger.getCitizenshipCountry());
			pwl.setResidencyCountry(passenger.getResidencyCountry());
			pwl.setDocumentType(doc.getDocumentType());
			pwl.setDocumentNumber(doc.getDocumentNumber());
			pwl.setExpirationDate(doc.getExpirationDate());
			pwl.setIssuanceDate(doc.getIssuanceDate());
			pwl.setIssuanceCountry(doc.getIssuanceCountry());
			pwlVos.add(pwl);
		});
		for (WhitelistVo newwl : pwlVos) {
			for (WhitelistVo wlv : wVos) {
				if (newwl.equals(wlv))
					return true;
			}
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#retrieveApisMessage(gov.gtas.model.
	 * MessageStatus)
	 */
	@Override
	@Transactional
	public List<ApisMessage> retrieveApisMessage(MessageStatus messageStatus) {
		return apisMsgRepository.findByStatus(messageStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.svc.TargetingService#retrievePnr(gov.gtas.model.MessageStatus)
	 */
	@Override
	@Transactional
	public List<Pnr> retrievePnr(MessageStatus messageStatus) {
		return pnrMsgRepository.findByStatus(messageStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.svc.TargetingService#updateApisMessage(gov.gtas.model.ApisMessage
	 * , gov.gtas.model.MessageStatus)
	 */
	@Override
	@Transactional
	public void updateApisMessage(ApisMessage message,
			MessageStatus messageStatus) {
		ApisMessage apisMessage = apisMsgRepository.findOne(message.getId());
		if (apisMessage != null) {
			apisMessage.setStatus(messageStatus);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#updatePnr(gov.gtas.model.Pnr,
	 * gov.gtas.model.MessageStatus)
	 */
	@Override
	@Transactional
	public void updatePnr(Pnr message, MessageStatus messageStatus) {
		Pnr pnr = pnrMsgRepository.findOne(message.getId());
		if (pnr != null) {
			pnr.setStatus(messageStatus);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#runningRuleEngine()
	 */
	@Transactional
	@Override
	public Set<Long> runningRuleEngine() {
		logger.info("Entering runningRuleEngine().");
		Set<Long> uniqueFlights = new HashSet<>();
		RuleExecutionContext ruleRunningResult = analyzeLoadedMessages(true);
		if (ruleRunningResult != null) {
			RuleExecutionStatistics ruleExeStatus = ruleRunningResult
					.getRuleExecutionStatistics();

			List<HitsSummary> hitsSummaryList = storeHitsInfo(ruleRunningResult);
			for (HitsSummary s : hitsSummaryList) {
				passengerService.createDisposition(s);
				uniqueFlights.add(s.getFlight().getId());
			}
			writeAuditLogForTargetingRun(ruleRunningResult);
		}
		logger.info("Exiting runningRuleEngine().");
		return uniqueFlights;
	}

	/**
	 * Write audit log for targeting run.
	 *
	 * @param targetingResult
	 *            the targeting result
	 */
	private void writeAuditLogForTargetingRun(
			RuleExecutionContext targetingResult) {
		try {
			Set<Long> passengerHits = new HashSet<>();
			int ruleHits = 0;
			int wlHits = 0;
			List<WhitelistVo> wVos = whitelistService.getAllWhitelists();
			for (TargetSummaryVo hit : targetingResult.getTargetingResult()) {
				Passenger foundPassenger = passengerRepository.findOne(hit
						.getPassengerId());
				if (!wVos.isEmpty()) {
					if (!filteringWhitelist(wVos, foundPassenger)) {
						ruleHits += hit.getRuleHitCount();
						wlHits += hit.getWatchlistHitCount();
						passengerHits.add(hit.getPassengerId());
					}
				} else {
					ruleHits += hit.getRuleHitCount();
					wlHits += hit.getWatchlistHitCount();
					passengerHits.add(hit.getPassengerId());
				}	
			}
			AuditActionTarget target = new AuditActionTarget(
					AuditActionType.TARGETING_RUN, "GTAS Rule Engine", null);
			AuditActionData actionData = new AuditActionData();
			actionData.addProperty("totalRuleHits", String.valueOf(ruleHits));
			actionData.addProperty("watchlistHits", String.valueOf(wlHits));
			actionData.addProperty("totalPassengerHits",
					String.valueOf(passengerHits.size()));

			String message = "Targeting run on " + new Date();
			auditLogPersistenceService.create(AuditActionType.TARGETING_RUN,
					target.toString(), actionData.toString(), message,
					GTAS_APPLICATION_USERID);
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.svc.TargetingService#updateFlightHitCounts(java.util.Set)
	 */
	@Transactional
	@Override
	public void updateFlightHitCounts(Set<Long> flights) {
		logger.info("Entering updateFlightHitCounts().");
		if (CollectionUtils.isEmpty(flights)) {
			logger.info("no flight");
			return;
		}
		logger.info("update rule hit count on flights.");
		for (Long flightId : flights) {
			flightRepository.updateRuleHitCountForFlight(flightId);
			flightRepository.updateListHitCountForFlight(flightId);
		}
	}

	/**
	 * Store hits info.
	 *
	 * @param ruleRunningResult
	 *            the rule running result
	 * @return the list
	 */
	private List<HitsSummary> storeHitsInfo(
			RuleExecutionContext ruleRunningResult) {
		logger.info("Entering storeHitsInfo().");

		List<HitsSummary> hitsSummaryList = new ArrayList<>();
		Collection<TargetSummaryVo> results = ruleRunningResult
				.getTargetingResult();

		Iterator<TargetSummaryVo> iter = results.iterator();
		while (iter.hasNext()) {
			TargetSummaryVo ruleDetail = iter.next();
			HitsSummary hitsSummary = constructHitsInfo(ruleDetail);
			if (hitsSummary != null) {
				hitsSummaryList.add(hitsSummary);
			}
		}
		logger.info("Total new hits summary records --> " + hitsSummaryList.size());
		hitsSummaryRepository.save(hitsSummaryList);

		logger.info("Exiting storeHitsInfo().");
		return hitsSummaryList;
	}

	/**
	 * @param hitSummmaryVo
	 * @return HitsSummary
	 */
	private HitsSummary constructHitsInfo(TargetSummaryVo hitSummmaryVo) {
		logger.info("Entering constructHitsInfo().");
		List<WhitelistVo> wVos = whitelistService.getAllWhitelists();
		HitsSummary hitsSummary = new HitsSummary();
		Passenger foundPassenger = passengerRepository.findOne(hitSummmaryVo
				.getPassengerId());
		if (foundPassenger != null) {
			logger.info("Found passenger.");
			if (!wVos.isEmpty()) {
				if (!filteringWhitelist(wVos, foundPassenger)) {
					hitsSummary.setPassenger(foundPassenger);
					writeAuditLogForTargetingPassenger(foundPassenger);
				} else {
					return null;
				}
			} else {
				hitsSummary.setPassenger(foundPassenger);
				writeAuditLogForTargetingPassenger(foundPassenger);
			}
		} else {
			logger.debug("No passenger found. --> ");
		}

		Flight foundFlight = flightRepository.findOne(hitSummmaryVo
				.getFlightId());
		if (foundFlight != null) {
			logger.info("Found flight.");
			hitsSummary.setFlight(foundFlight);
		} else {
			logger.debug("No flight found. --> ");
		}
		hitsSummary.setCreatedDate(new Date());
		hitsSummary.setHitType(hitSummmaryVo.getHitType().toString());

		hitsSummary.setRuleHitCount(hitSummmaryVo.getRuleHitCount());
		hitsSummary.setWatchListHitCount(hitSummmaryVo.getWatchlistHitCount());
		List<HitDetail> detailList = new ArrayList<>();
		for (TargetDetailVo hdv : hitSummmaryVo.getHitDetails()) {
			detailList.add(createHitDetail(hitsSummary, hdv));
		}
		hitsSummary.setHitdetails(detailList);
		logger.info("Exiting constructHitsInfo().");
		return hitsSummary;
	}

	/**
	 * Write audit log for passenger rule hit.
	 *
	 * @param passenger
	 *            the passenger
	 */
	private void writeAuditLogForTargetingPassenger(Passenger passenger) {
		try {
			AuditActionTarget target = new AuditActionTarget(passenger);
			AuditActionData actionData = new AuditActionData();

			actionData.addProperty("CitizenshipCountry",
					passenger.getCitizenshipCountry());
			actionData.addProperty("PassengerType",
					passenger.getPassengerType());

			//
			String message = "API/PNR MESSAGE Ingest and Parsing  "
					+ passenger.getCreatedAt();
			auditLogRepository.save(new AuditRecord(
					AuditActionType.MESSAGE_INGEST_PARSING, target.toString(),
					Status.SUCCESS, message, actionData.toString(), userService
							.fetchUser(GTAS_APPLICATION_USERID), passenger
							.getCreatedAt()));
			//
			String message2 = "Passenger Rule Hit and Case Open run on "
					+ new Date();
			auditLogRepository
					.save(new AuditRecord(AuditActionType.RULE_HIT_CASE_OPEN,
							target.toString(), Status.SUCCESS, message2,
							actionData.toString(), userService
									.fetchUser(GTAS_APPLICATION_USERID),
							new Date()));
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}
	}

	/**
	 * Creates the hit detail.
	 *
	 * @param hitsSummary
	 *            the hits summary
	 * @param hitDetailVo
	 *            the hit detail vo
	 * @return the hit detail
	 */
	private HitDetail createHitDetail(HitsSummary hitsSummary,
			TargetDetailVo hitDetailVo) {
		logger.info("Entering createHitDetail().");
		HitDetail hitDetail = new HitDetail();
		if (hitDetailVo.getUdrRuleId() != null) {
			logger.info("Set UDR Rule Id.");
			hitDetail.setRuleId(hitDetailVo.getUdrRuleId());
		} else {
			logger.info("Set Rule Id.");
			hitDetail.setRuleId(hitDetailVo.getRuleId());
		}

		String[] hitReasons = hitDetailVo.getHitReasons();

		StringBuilder sb = new StringBuilder();
		for (String hitReason : hitReasons) {
			sb.append(hitReason);
			sb.append(HITS_REASONS_SEPARATOR);
		}

		hitDetail.setRuleConditions(sb.toString());
		hitDetail.setCreatedDate(new Date());
		hitDetail.setTitle(hitDetailVo.getTitle());
		hitDetail.setDescription(hitDetailVo.getDescription());
		hitDetail.setHitType(hitDetailVo.getHitType().toString());

		hitDetail.setParent(hitsSummary);
		logger.info("Exiting createHitDetail().");
		return hitDetail;
	}
}
