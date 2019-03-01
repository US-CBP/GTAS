/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
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
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;
import gov.gtas.error.CommonServiceException;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.*;
import gov.gtas.repository.*;
import gov.gtas.repository.udr.RuleMetaRepository;
import gov.gtas.repository.udr.UdrRuleRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.rule.RuleService;
import gov.gtas.services.*;
import gov.gtas.services.security.UserService;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.util.*;
import gov.gtas.util.Bench;
import gov.gtas.vo.WhitelistVo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	private AppConfigurationService appConfigurationService;

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
	private RuleMetaRepository ruleMetaRepository;

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

	@Autowired
	private CaseDispositionService caseDispositionService;

	@Autowired
	private UserRepository userRepository;

    @Autowired
	private FlightHitsRuleRepository flightHitsRuleRepository;

    @Autowired
	private FlightHitsWatchlistRepository flightHitsWatchlistRepository;

	@Autowired
	private MessageStatusRepository messageStatusRepository;

	@Autowired
	CaseDispositionRepository caseDispositionRepository;

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
		TargetingResultServices targetingResultServices = getTargetingResultOptions();
		res = TargetingResultUtils.ruleResultPostProcesssing(res, targetingResultServices);
		//make a call to Case Mgmt.
		TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(res, caseDispositionService, passengerService);

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
		TargetingResultServices targetingResultServices = getTargetingResultOptions();
		res = TargetingResultUtils.ruleResultPostProcesssing(res, targetingResultServices);
		//make a call to Case Mgmt.
		TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(res, caseDispositionService, passengerService);

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
		TargetingResultServices targetingResultServices = getTargetingResultOptions();

		res = TargetingResultUtils.ruleResultPostProcesssing(res, targetingResultServices);
		//make a call to Case Mgmt.
		TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(res, caseDispositionService, passengerService);

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
		return new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.gtas.svc.TargetingService#analyzeLoadedPnr()
	 */
	@Override
	@Transactional
	public List<RuleHitDetail> analyzeLoadedPnr() {
		return new ArrayList<>();
	}

	private List<RuleHitDetail> runRules(RuleExecutionContext ctx) {
		List<RuleHitDetail> ret;
		RuleServiceResult res = ruleService.invokeRuleEngine(ctx
				.getRuleServiceRequest());
		TargetingResultServices targetingResultServices = getTargetingResultOptions();
		res = TargetingResultUtils.ruleResultPostProcesssing(res, targetingResultServices);
		//make a call to Case Mgmt.
		TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(res, caseDispositionService, passengerService);
		ret = res.getResultList();
		return ret;
	}

	private TargetingResultServices getTargetingResultOptions() {
		TargetingResultServices targetingResultServices = new TargetingResultServices();
		targetingResultServices.setAppConfigurationService(appConfigurationService);
		targetingResultServices.setPassengerService(passengerService);
		targetingResultServices.setRuleMetaRepository(ruleMetaRepository);
		return targetingResultServices;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see gov.gtas.svc.TargetingService#analyzeLoadedMessages(boolean)
	 */
	@Override
	public RuleResults analyzeLoadedMessages(
			final boolean updateProcesssedMessageStat) {
		logger.debug("Entering analyzeLoadedMessages()");

		List<MessageStatus> source =
				messageStatusRepository
				.getMessagesFromStatus(
				MessageStatusEnum.LOADED);

		List<Message> target = source
				.stream()
				.map(MessageStatus::getMessage)
				.collect(Collectors.toList());

		RuleResults ruleResults = null;
		try {
			ruleResults = executeRules(target);
                        Bench.end("second", "analyzeLoadedMessages executeRules end");
			logger.debug("updating messages status from loaded to analyzed.");
			if (updateProcesssedMessageStat) {
				for (MessageStatus ms : source) {
					ms.setMessageStatusEnum(MessageStatusEnum.ANALYZED);
				}
			}
			logger.debug("saving message status");
			messageStatusRepository.save(source);
			logger.debug("done saving message status");
			logger.debug("Exiting analyzeLoadedMessages()");
			return ruleResults;
		} catch (CommonServiceException cse) {
			if (cse.getErrorCode().equals(
					RuleServiceConstants.KB_NOT_FOUND_ERROR_CODE)
					|| cse.getErrorCode().equals(
							RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE)) {
				logger.info("************************");
				logger.info(cse.getMessage());
				logger.info("************************");
				if (updateProcesssedMessageStat) {
					for (MessageStatus ms : source) {
						ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
					}
					messageStatusRepository.save(source);
				}
			} else {
				if (updateProcesssedMessageStat) {
					for (MessageStatus ms : source) {
						ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
					}
					messageStatusRepository.save(source);
				}
				throw cse;
			}
		}
		return ruleResults;
	}

	/**
	 * Execute rules.
	 *
	 * @param target
	 *            the target
	 * @return the rule execution context
	 */
	private RuleResults executeRules(List<Message> target) {
		logger.debug("Entering executeRules().");

		RuleExecutionContext ctx = TargetingServiceUtils
				.createPnrApisRequestContext(target);
		logger.debug("Running Rule set.");
		// default knowledge Base is the UDR KB
		RuleServiceResult udrResult = ruleService.invokeRuleEngine(ctx
				.getRuleServiceRequest());
                if (udrResult != null)
                {
                    RuleExecutionStatistics res = udrResult.getExecutionStatistics();
                    int totalRulesFired = res.getTotalRulesFired();
                    List<String> ruleFireSequence = res.getRuleFiringSequence();
                    logger.debug("Total UDR rules fired: " + totalRulesFired);
                    logger.debug("\n****************UDR Rule firing sequence***************************\n");
                    for (String str : ruleFireSequence)
                    {
                       logger.debug("UDR Rule fired: " + str);
                    }
                    logger.debug("\n\n**********************************************************************");
                }

		RuleServiceResult wlResult = ruleService.invokeRuleEngine(
				ctx.getRuleServiceRequest(),
				WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
		RuleResults ruleResults = new RuleResults();
		ruleResults.setUdrResult(udrResult);
		ruleResults.setWatchListResult(wlResult);

		logger.debug("Exiting executeRules().");
		return ruleResults;
	}

	private Set<Case> processResultAndMakeCases(RuleResults ruleResults) {
		TargetingResultServices targetingResultServices = getTargetingResultOptions();
		Set<Case> casesSet = new HashSet<>();
		if (ruleResults.getUdrResult() != null) {
			RuleServiceResult udrResult = ruleResults.getUdrResult();
			logger.debug("Rule Hits....");
			udrResult = TargetingResultUtils
					.ruleResultPostProcesssing(udrResult, targetingResultServices);
			Set<Case> resultCases = (TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(udrResult, caseDispositionService, passengerService));
			mergeSets(casesSet, resultCases);
			ruleResults.setUdrResult(udrResult);
		}
		if (ruleResults.getWatchListResult() != null) {
			RuleServiceResult watchlistResult = ruleResults.getWatchListResult();
			logger.debug("Watchlist...");
			watchlistResult = TargetingResultUtils.ruleResultPostProcesssing(watchlistResult, targetingResultServices);
			//make a call to Case Mgmt.
			Set<Case> resultCases = TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(watchlistResult, caseDispositionService, passengerService);
			mergeSets(casesSet, resultCases);
			ruleResults.setWatchListResult(watchlistResult);
		}

		RuleServiceResult ruleServiceResult = new CompositeRuleServiceResult(ruleResults.getWatchListResult(), ruleResults.getUdrResult());
		TargetingResultUtils.updateRuleExecutionContext(ruleServiceResult, ruleResults);
		return casesSet;
	}

	private void mergeSets(Set<Case> casesSet, Set<Case> resultCases) {
		for (Case caze : resultCases) {
			if (!casesSet.add(caze)) {
				Case oldCase = casesSet.stream().filter(c -> c.equals(caze)).findFirst().orElse(null);
				if (oldCase != null) {
					caze.getHitsDispositions().addAll(oldCase.getHitsDispositions());
					casesSet.remove(caze);
					casesSet.add(caze);
				}
			}
		}
	}
	/**
	 * Filtering whitelist.
	 *
	 * @param wVos
	 *            the w vos
	 * @param passenger
	 *            the passenger
	 * @return true, if successful
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
	Bench.end("fourth", "executeRules invokeRuleEngine second end");
	// eliminate duplicates
	TargetingResultServices targetingResultServices = getTargetingResultOptions();
	if (udrResult != null) {

	logger.debug("Eliminate duplicates from UDR rule running.");
	Bench.start("fifth", "executeRules TargetingResultUtils ruleResultPostProcesssing udrResult start");
	udrResult = TargetingResultUtils
	.ruleResultPostProcesssing(udrResult, targetingResultServices);
	Bench.end("fifth", "executeRules TargetingResultUtils ruleResultPostProcesssing udrResult end");
	//make a call to Case Mgmt.
	Bench.start("sixth", "executeRules TargetingResultCaseMgmtUtils ruleResultPostProcesssing udrResult start");
	TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(udrResult, caseDispositionService, passengerService);
	Bench.end("sixth", "executeRules TargetingResultCaseMgmtUtils ruleResultPostProcesssing udrResult end");

	}
	if (wlResult != null) {
	logger.debug("Eliminate duplicates from watchlist rule running.");
	Bench.start("seventh", "executeRules TargetingResultUtils ruleResultPostProcesssing wlResult start");
	wlResult = TargetingResultUtils.ruleResultPostProcesssing(wlResult, targetingResultServices);
	Bench.end("seventh", "executeRules TargetingResultUtils ruleResultPostProcesssing wlResult end");
	//make a call to Case Mgmt.
	Bench.start("eighth", "executeRules TargetingResultUtils TargetingResultCaseMgmtUtils wlResult start");
	TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(wlResult, caseDispositionService, passengerService);
	Bench.end("eighth", "executeRules TargetingResultUtils TargetingResultCaseMgmtUtils wlResult end");

	}

	TargetingResultUtils.updateRuleExecutionContext(ctx,
	new CompositeRuleServiceResult(udrResult, wlResult));

	 */
	private boolean filteringWhitelist(List<WhitelistVo> wVos,
			Passenger passenger) {
		if (wVos.isEmpty())
			return false;
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
				if (newwl.customEquals(wlv))
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
		return new ArrayList<>();
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
		return new ArrayList<>();
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
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.gtas.svc.TargetingService#runningRuleEngine()
	 */
	@Transactional
	@Override
	public RuleResults runningRuleEngine() {
		logger.debug("Entering runningRuleEngine().");
	  	return analyzeLoadedMessages(true);
	}

	public TargetingServiceResults createHitsAndCases(RuleResults ruleRunningResult) {
		TargetingServiceResults targetingServiceResults = null;
		if (ruleRunningResult != null && ruleRunningResult.hasResults()) {
			targetingServiceResults = new TargetingServiceResults();
			Set<Case> casesSet = processResultAndMakeCases(ruleRunningResult);
			List<HitsSummary> hitsSummaryList = storeHitsInfo(ruleRunningResult.getTargetingResult());
			targetingServiceResults.setCaseSet(casesSet);
			targetingServiceResults.setHitsSummaryList(hitsSummaryList);
		}
		return targetingServiceResults;
	}

	@Transactional
	public void saveEverything(TargetingServiceResults targetingServiceResults) {
		try {
			if (targetingServiceResults != null) {
				Set<Long> uniqueFlights = new HashSet<>();
				caseDispositionRepository.save(targetingServiceResults.getCaseSet());
				hitsSummaryRepository.save(targetingServiceResults.getHitsSummaryList());
				passengerService.createDisposition(targetingServiceResults.getHitsSummaryList());
				for (HitsSummary s : targetingServiceResults.getHitsSummaryList()) {
					uniqueFlights.add(s.getFlightId());
				}
				writeAuditLogForTargetingRun(targetingServiceResults);
				updateFlightHitCounts(uniqueFlights);
			}
		} catch (Exception e) {
			logger.warn("UNABLE TO CREATE NEW CASES. FAILURE! ", e);
		}
	}


	/**
	 * Write audit log for targeting run.
	 *
	 * @param targetingResult
	 *            the targeting result
	 */
	private void writeAuditLogForTargetingRun(
			TargetingServiceResults targetingResult) {
		try {
			Set<Long> passengerHits = new HashSet<>();
			int ruleHits = 0;
			int wlHits = 0;
			for (HitsSummary hit : targetingResult.getHitsSummaryList()) {
				ruleHits += hit.getRuleHitCount();
				wlHits += hit.getWatchListHitCount();
				passengerHits.add(hit.getPaxId());
			}
			//IF IMPLEMENTING WHITELIST CHECK SOURCE CONTROL(i.e. git) HISTORY.
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
	@Override
	public void updateFlightHitCounts(Set<Long> flights) {
		logger.debug("Entering updateFlightHitCounts().");
		if (CollectionUtils.isEmpty(flights)) {
			logger.debug("no flight");
			return;
		}
		logger.debug("update rule hit count on flights.");
		for (Long flightId : flights){
			Integer ruleHits = hitsSummaryRepository.ruleHitCount(flightId);
			FlightHitsRule ruleFlightHits = new FlightHitsRule(flightId, ruleHits);
			flightHitsRuleRepository.save(ruleFlightHits);

			Integer watchlistHit = hitsSummaryRepository.watchlistHitCount(flightId);
			FlightHitsWatchlist watchlistHitCount = new FlightHitsWatchlist(flightId, watchlistHit);
			flightHitsWatchlistRepository.save(watchlistHitCount);
		}
	}

	private Map<Long, Passenger> makePassengerMap(Collection<TargetSummaryVo> targetSummaryVoList)
        {
            List<Long> passengerIdList = new ArrayList<>();
            List<Passenger> passengerResultList = null;
            Map<Long, Passenger> passengerMap = new HashMap<>();

            for (TargetSummaryVo tsvo : targetSummaryVoList)
            {
               Long passengerId = tsvo.getPassengerId();
               passengerIdList.add(passengerId);

            }

            if (!passengerIdList.isEmpty())
            {
                passengerResultList = passengerService.getPaxByPaxIdList(passengerIdList);

                for (Passenger passenger :  passengerResultList)
                {
                   passengerMap.put(passenger.getId(), passenger);

                }
            }
            return passengerMap;

        }

	private Map<Long, Flight> makeFlightMap(Collection<TargetSummaryVo> targetSummaryVoList)
        {
            List<Long> flightIdList = new ArrayList<>();
            List<Flight> flightResultList = null;
            Map<Long, Flight> flightMap = new HashMap<>();

            for (TargetSummaryVo tsvo : targetSummaryVoList)
            {
               Long flightId = tsvo.getFlightId();
               flightIdList.add(flightId);
            }

            if (!flightIdList.isEmpty())
            {
                flightResultList = passengerService.getFlightsByIdList(flightIdList);

                for (Flight flight : flightResultList)
                {
                    flightMap.put(flight.getId(), flight);
                }
            }

            return flightMap;

        }

	/**
	 * Store hits info.
	 *
	 *            the rule running result
	 * @return the list
	 */
	private List<HitsSummary> storeHitsInfo(
			Collection<TargetSummaryVo> results) {

		List<HitsSummary> hitsSummaryList = new ArrayList<>();

		Map<Long, Flight> flightMap = makeFlightMap(results);
		Map<Long, Passenger> passengerMap = makePassengerMap(results);
		User gtasUser = userRepository.findOne(GTAS_APPLICATION_USERID);

		for (TargetSummaryVo ruleDetail : results) {
			HitsSummary hitsSummary = constructHitsInfo(ruleDetail, flightMap, passengerMap, gtasUser);
			if (hitsSummary != null) {
				hitsSummaryList.add(hitsSummary);
			}
		}
		return adjustForNewRuleHits(hitsSummaryList);

	}

	private List<HitsSummary> adjustForNewRuleHits(List<HitsSummary> hitsSummaryList) {
		List<Long> passengerIdList = new ArrayList<>();
		for (HitsSummary hSummary : hitsSummaryList) {
			Long passengerId = hSummary.getPaxId();
			passengerIdList.add(passengerId);
		}

		Set<HitsSummary> existingHits = new HashSet<>();
		Map<Long, HitsSummary> existingHitsMap = new HashMap<>();
		if (!passengerIdList.isEmpty()) {
			existingHits = hitsSummaryRepository.findHitsByPassengerIdList(passengerIdList);
			for (HitsSummary existingHit : existingHits) {
				existingHitsMap.put(existingHit.getPaxId(), existingHit);
			}
		}

		int updated = 0;
		int newDetails = 0;
		for (HitsSummary hSummary : hitsSummaryList) {
			int watchlistCount = 0;
			int ruleHitCount = 0;
			if (existingHits.contains(hSummary)) {
				HitsSummary existingHitsSummary = existingHitsMap.get(hSummary.getPaxId());
				for (HitDetail hitDetail : hSummary.getHitdetails()) {
					//Recalculate rules and watchlist hits by counting each hit detail.
					if (isRuleHitDetail(hitDetail)) {
						ruleHitCount++;
					} else {
						watchlistCount++;
					}
					if (!existingHitsSummary.getHitdetails().contains(hitDetail)) {
						hitDetail.setParent(existingHitsSummary);  // set new HitDetail with new rule to existing HitSummary.
						existingHitsSummary.getHitdetails().add(hitDetail);
						updated++;
					}
				}
				//Existing hit will override the hit so update it instead of the hit summary passed in to the method.
				existingHitsSummary.setRuleHitCount(ruleHitCount);
				existingHitsSummary.setWatchListHitCount(watchlistCount);
			} else {
				//Calculate rules and watchlist hits by counting each hit detail.
				for (HitDetail hitDetail : hSummary.getHitdetails()) {
					newDetails++;
					if (isRuleHitDetail(hitDetail)) {
						ruleHitCount++;
					} else {
						watchlistCount++;
					}
				}
				hSummary.setWatchListHitCount(watchlistCount);
				hSummary.setRuleHitCount(ruleHitCount);
			}
		}

		//Replace existing hit summaries
		for (HitsSummary updatedExistingHits : existingHits) {
			if (hitsSummaryList.contains(updatedExistingHits)) {
				hitsSummaryList.remove(updatedExistingHits);
				hitsSummaryList.add(updatedExistingHits);
			}
		}
		int totalNewRecords = hitsSummaryList.size() - existingHits.size();
		if (updated > 0 || newDetails > 0) {
			logger.info("Added " + updated + " hit details to existing hit summaries. " +
					"Created " + totalNewRecords + " new hit summaries with " + newDetails + " new details.");
		}
		return hitsSummaryList;
	}

	private boolean isRuleHitDetail(HitDetail hitDetail) {
		return hitDetail.getHitType() != null && hitDetail.getHitType().equalsIgnoreCase("R");
	}

	/**
	 * @param hitSummmaryVo hits summary value object
	 * @return HitsSummary
	 */
	private HitsSummary constructHitsInfo(TargetSummaryVo hitSummmaryVo, Map<Long, Flight> flightMap, Map<Long, Passenger> passengerMap, User gtasUser) {
		//logger.info("Entering constructHitsInfo().");
		HitsSummary hitsSummary = new HitsSummary();
                Passenger foundPassenger = passengerMap.get(hitSummmaryVo.getPassengerId());

		if (foundPassenger != null) {
			logger.debug("Found passenger.");
				hitsSummary.setPaxId(foundPassenger.getId());
				writeAuditLogForTargetingPassenger(foundPassenger, hitSummmaryVo.getHitType().toString(), gtasUser);

		} else {
			logger.debug("No passenger found. --> ");
		}

		Flight foundFlight = flightMap.get(hitSummmaryVo.getFlightId());

		if (foundFlight != null) {
			logger.debug("Found flight.");
			hitsSummary.setFlightId(foundFlight.getId());
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
		//logger.info("Exiting constructHitsInfo().");
		return hitsSummary;
	}

	/**
	 * Write audit log for passenger rule hit.
	 *
	 * @param passenger
	 *            the passenger
	 */
	private void writeAuditLogForTargetingPassenger(Passenger passenger,
			String hitType, User gtasUser) {
		try {
			AuditActionTarget target = new AuditActionTarget(passenger);
			AuditActionData actionData = new AuditActionData();

			actionData.addProperty("CitizenshipCountry",
					passenger.getCitizenshipCountry());
			actionData.addProperty("PassengerType",
					passenger.getPassengerType());

			String message = "API/PNR MESSAGE Ingest and Parsing  "
					+ passenger.getCreatedAt();
			auditLogRepository.save(new AuditRecord(
					AuditActionType.MESSAGE_INGEST_PARSING, target.toString(),
					Status.SUCCESS, message, actionData.toString(), gtasUser, passenger.getCreatedAt()));

			String message2 = new StringBuffer("Passenger Rule Hit with ")
					.append(hitType).append(" HitType and Case Open run on ")
					.append(Calendar.getInstance().getTime()).toString();

			auditLogRepository
					.save(new AuditRecord(AuditActionType.RULE_HIT_CASE_OPEN,
							target.toString(), Status.SUCCESS, message2,
							actionData.toString(), gtasUser, new Date()));
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
		//logger.info("Entering createHitDetail().");
		HitDetail hitDetail = new HitDetail();
		if (hitDetailVo.getUdrRuleId() != null) {
			logger.debug("Set UDR Rule Id.");
			hitDetail.setRuleId(hitDetailVo.getUdrRuleId());
		} else {
			logger.debug("Set Rule Id.");
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
		//logger.info("Exiting createHitDetail().");
		return hitDetail;
	}

	private Map<Long, Set<Flight>> getPaxIdFlightMap(RuleServiceResult res){
		Map<Long,Set<Flight>> map = new HashMap<Long, Set<Flight>>();
		Set<Flight> flights;

		for(RuleHitDetail rhd : res.getResultList()){
			flights = passengerService.getAllFlights(rhd.getPassengerId());
			if(flights != null && flights.size() > 0){
				map.put(rhd.getPassengerId(), flights);
			}
		}
		return map;
	}
}
