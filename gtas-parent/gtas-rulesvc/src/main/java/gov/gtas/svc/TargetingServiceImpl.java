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
import gov.gtas.constant.RuleConstants;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;
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
import gov.gtas.model.User;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.AuditRecordRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.repository.HitsSummaryRepository;
import gov.gtas.repository.MessageRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PnrRepository;
import gov.gtas.repository.UserRepository;
import gov.gtas.repository.udr.RuleMetaRepository;
import gov.gtas.repository.udr.UdrRuleRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.rule.RuleService;
import gov.gtas.services.*;
import gov.gtas.services.security.UserService;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.TargetingResultCaseMgmtUtils;
import gov.gtas.svc.util.TargetingResultUtils;
import gov.gtas.svc.util.TargetingServiceUtils;
import gov.gtas.util.Bench;
import gov.gtas.vo.WhitelistVo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
		List<RuleHitDetail> ret = null;
		List<ApisMessage> msgs = this.retrieveApisMessage(MessageStatus.LOADED);
		if (msgs != null) {
			RuleExecutionContext ctx = TargetingServiceUtils
					.createApisRequestContext(msgs);
			ret = runRules(ctx);
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
			ret = runRules(ctx);
		}
		return ret;
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
                    Bench.start("second", "analyzeLoadedMessages executeRules start");
			ctx = executeRules(target);
                        Bench.end("second", "analyzeLoadedMessages executeRules end");
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
		logger.info("Entering executeRules().");

		RuleExecutionContext ctx = TargetingServiceUtils
				.createPnrApisRequestContext(target);
		logger.debug("Running Rule set.");
                Bench.start("third", "executeRules invokeRuleEngine start");
		// default knowledge Base is the UDR KB
		RuleServiceResult udrResult = ruleService.invokeRuleEngine(ctx
				.getRuleServiceRequest());
                
                if (udrResult != null)
                {
                    RuleExecutionStatistics res = udrResult.getExecutionStatistics();
                    int totalRulesFired = res.getTotalRulesFired();
                    List<String> ruleFireSequence = res.getRuleFiringSequence();
                    logger.info("Total UDR rules fired: " + totalRulesFired);
                    logger.debug("\n****************UDR Rule firing sequence***************************\n");
                    for (String str : ruleFireSequence)
                    {
                       logger.debug("UDR Rule fired: " + str); 
                    }

                    logger.debug("\n\n**********************************************************************"); 
                }
                Bench.end("third", "executeRules invokeRuleEngine udrResult end");

                Bench.start("fourth", "executeRules invokeRuleEngine second start");
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
                            Bench.start("zxcv1", "start storeHitsInfo");
	 			List<HitsSummary> hitsSummaryList = storeHitsInfo(ruleRunningResult);
                            Bench.end("zxcv1", "End storeHitsInfo");
				for (HitsSummary s : hitsSummaryList) {
		  			passengerService.createDisposition(s);
		 			uniqueFlights.add(s.getFlight().getId());
				}
	  		writeAuditLogForTargetingRun(ruleRunningResult);
	  	}
	 	logger.info("Exiting runningRuleEngine().");
                
	 	//updateFlightHitCounts() was moved here in order to insure manual rule running updated flight hit counts
	 	updateFlightHitCounts(uniqueFlights);
                Bench.print();
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
                        Map<Long, Passenger> passengerMap = createPassengerMap(targetingResult.getTargetingResult());
                        
			for (TargetSummaryVo hit : targetingResult.getTargetingResult()) {

                                Passenger foundPassenger = passengerMap.get(hit.getPassengerId());
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
        
        private Map<Long, Passenger> createPassengerMap(Collection<TargetSummaryVo> tsvoCollection)
        {
            List<Long> passengerIdList = new ArrayList<>();
            List<Passenger> passengerResultList = null;
            Map<Long, Passenger> passengerMap = new HashMap<>();
            
            for (TargetSummaryVo tsvo : tsvoCollection)
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
		for (Long flightId : flights){
			flightRepository.updateRuleHitCountForFlight(flightId);
			flightRepository.updateListHitCountForFlight(flightId);
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
                
                Map<Long, Flight> flightMap = makeFlightMap(results);
                Map<Long, Passenger> passengerMap = makePassengerMap(results);
                User gtasUser = userRepository.findOne(GTAS_APPLICATION_USERID);
                List<WhitelistVo> wVos = whitelistService.getAllWhitelists();

                Bench.start("storeHits1", "Starting constructHitsInfo ");
		Iterator<TargetSummaryVo> iter = results.iterator();
                
		while (iter.hasNext()) {
			TargetSummaryVo ruleDetail = iter.next();
			HitsSummary hitsSummary = constructHitsInfo(ruleDetail, flightMap, passengerMap, gtasUser, wVos);
			if (hitsSummary != null) {
				hitsSummaryList.add(hitsSummary);
			}
		}
                Bench.end("storeHits1","End after constructHitsInfo");
                Bench.start("dedupHits", "start adjustForNewRuleHits");
                List<HitsSummary> adjustedHitsSummaryList = adjustForNewRuleHits(hitsSummaryList);
                Bench.end("dedupHits", "End adjustForNewRuleHits");
                
		logger.info("Total new hits summary records --> "
				+ adjustedHitsSummaryList.size());
		hitsSummaryRepository.save(adjustedHitsSummaryList);

		//logger.info("Exiting storeHitsInfo().");
		return adjustedHitsSummaryList;

	}
        
        // This code replaces the code previously in preProcessing() which was way too time consuming.
        // But this does not do a full clearing of all back hits from all messages and flights.
        private List<HitsSummary> adjustForNewRuleHits(List<HitsSummary> hitsSummaryList)
        {
           List<HitDetail> newHitDetailsToSave = new ArrayList<>();
           List<HitsSummary> hitSummaryListToRemove = new ArrayList<>();
           List<HitsSummary> dedupedHitsSummaryList = new ArrayList<>();
           List<Long> passengerIdList = new ArrayList<>();
           List<HitsSummary> existingHits = new ArrayList<>();
           Map<Long, HitsSummary> existingHitsMap  = new HashMap<>();
           
           //logger.info("Length of hitsSummaryList at start of adjustForNewRuleHits: " + hitsSummaryList.size());
           for (HitsSummary hSummary : hitsSummaryList)
           {
              Long passengerId = hSummary.getPassenger().getId();
              passengerIdList.add(passengerId);
           } 
           
           if (!passengerIdList.isEmpty())
           {
               existingHits = hitsSummaryRepository.findHitsByPassengerIdList(passengerIdList);

               for (HitsSummary existingHit : existingHits)
               {
                  existingHitsMap.put(existingHit.getPassenger().getId(), existingHit); 
               }
           }

           for (HitsSummary hSummary : hitsSummaryList)
           {
               HitsSummary existingHitsSummary = null;

               // There should only be one hit returned here. HitDetails with the ruleIds will be returned by blasted eager fetching.
              // List<HitsSummary> existingHits =  hitsSummaryRepository.retrieveHitsByFlightAndPassengerId(
              //                                   hSummary.getFlight().getId(),hSummary.getPassenger().getId());
               
               //logger.info("Length of existingHits at middle of adjustForNewRuleHits: " + existingHits.size());

               /* some previous hits in HitSummary table, need to check if any new rules in HitDetail have been added. 
                  We are not deleting any old rule hits, the decision was made to leave them in place.
                  if existingHits is null, then no previous hits are present and we leave the hit list alone.
               */
               if (!existingHitsMap.isEmpty())
               {
                  //existingHitsSummary =  existingHits.get(0);
                  existingHitsSummary = existingHitsMap.get(hSummary.getPassenger().getId());
                  List<Long> existingRuleIdList = existingHitsSummary.getHitdetails().stream().map(hs -> hs.getRuleId()).collect(Collectors.toList());
                  
                  for (HitDetail hitDetail : hSummary.getHitdetails())
                  {
                      if (!existingRuleIdList.contains(hitDetail.getRuleId()))
                      {
                         hitDetail.setParent(existingHitsSummary);  // set new HitDetail with new rule to existing HitSummary.
                         newHitDetailsToSave.add(hitDetail);
                      }
                  }
                  // Do not duplicate existing Hit Summary. Add new rules later if needed.
                  hitSummaryListToRemove.add(hSummary);
                }
           }

           List< ImmutablePair<Long,Long> > flightPassengerIdPairList = new ArrayList<>();
           for (HitsSummary hs : hitSummaryListToRemove)
           {
               Long flightId = hs.getFlight().getId();
               Long passId = hs.getPassenger().getId();
               ImmutablePair<Long,Long> longPair = new ImmutablePair<>(flightId,passId);
               flightPassengerIdPairList.add(longPair);
           }
           
           if (!hitSummaryListToRemove.isEmpty())
           {
               for (HitsSummary hsum : hitsSummaryList)
               {
                   ImmutablePair<Long,Long> longPair = new ImmutablePair<>(hsum.getFlight().getId(),hsum.getPassenger().getId());
                   if (!flightPassengerIdPairList.contains(longPair))
                   {
                     dedupedHitsSummaryList.add(hsum);
                   }
               }
           }
           else
           {
               dedupedHitsSummaryList = hitsSummaryList;
           }
           
           // save new rule hits in HitDetail table.
           if (!newHitDetailsToSave.isEmpty())
           {
               hitDetailRepository.save(newHitDetailsToSave);
           }

           return dedupedHitsSummaryList;
        }

	/**
	 * @param hitSummmaryVo
	 * @return HitsSummary
	 */
	private HitsSummary constructHitsInfo(TargetSummaryVo hitSummmaryVo, Map<Long, Flight> flightMap, Map<Long, Passenger> passengerMap, User gtasUser,  List<WhitelistVo> wVos) {
		//logger.info("Entering constructHitsInfo().");
		HitsSummary hitsSummary = new HitsSummary();
                Passenger foundPassenger = passengerMap.get(hitSummmaryVo.getPassengerId());
                
		if (foundPassenger != null) {
			logger.debug("Found passenger.");
			if (!wVos.isEmpty()) {
				if (!filteringWhitelist(wVos, foundPassenger)) {
					hitsSummary.setPassenger(foundPassenger);
					writeAuditLogForTargetingPassenger(foundPassenger, hitSummmaryVo.getHitType().toString(), gtasUser);
				} else {
					return null;
				}
			} else {
				hitsSummary.setPassenger(foundPassenger);
				writeAuditLogForTargetingPassenger(foundPassenger, hitSummmaryVo.getHitType().toString(), gtasUser);
			}
		} else {
			logger.debug("No passenger found. --> ");
		}
                
                Flight foundFlight = flightMap.get(hitSummmaryVo.getFlightId());
                
		if (foundFlight != null) {
			logger.debug("Found flight.");
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
