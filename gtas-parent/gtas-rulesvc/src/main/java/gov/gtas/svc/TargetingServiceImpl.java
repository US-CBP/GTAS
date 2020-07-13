/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import gov.gtas.bo.CompositeRuleServiceResult;
import gov.gtas.bo.RuleExecutionStatistics;
import gov.gtas.constant.RuleConstants;
import gov.gtas.model.RuleHitDetail;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.error.CommonServiceException;
import gov.gtas.model.*;
import gov.gtas.repository.*;
import gov.gtas.repository.udr.RuleMetaRepository;
import gov.gtas.rule.KIEAndLastUpdate;
import gov.gtas.rule.RuleService;
import gov.gtas.services.*;
import gov.gtas.svc.request.builder.RuleEngineRequestBuilder;
import gov.gtas.svc.util.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the Targeting Service API.
 */
@Component
@Scope("prototype")
@Transactional
public class TargetingServiceImpl implements TargetingService {

	private static final Logger logger = LoggerFactory.getLogger(TargetingServiceImpl.class);

	/* The rule engine to be used. */
	private final RuleService ruleService;

	private final AppConfigurationService appConfigurationService;

	private final HitDetailRepository hitDetailRepository;

	private final HitsSummaryRepository hitsSummaryRepository;

	private final RuleMetaRepository ruleMetaRepository;

	@Value("${hibernate.jdbc.batch_size}")
	private String batchSize;

	private final PassengerService passengerService;

	private final MessageStatusRepository messageStatusRepository;

	private final FlightHitsRuleRepository flightHitsRuleRepository;

	private final FlightHitsWatchlistRepository flightHitsWatchlistRepository;

	private final ApplicationContext applicationContext;

	/**
	 * Constructor obtained from the spring context by auto-wiring.
	 * 
	 * @param rulesvc
	 *            the auto-wired rule engine instance. \
	 * @param hitsSummaryRepository
	 * @param flightHitsRuleRepository
	 * @param flightHitsWatchlistRepository
	 */
	@Autowired
	public TargetingServiceImpl(final RuleService rulesvc, AppConfigurationService appConfigurationService,
			HitDetailRepository hitDetailRepository, HitsSummaryRepository hitsSummaryRepository,
			PassengerService passengerService, MessageStatusRepository messageStatusRepository,
			RuleMetaRepository ruleMetaRepository, FlightHitsRuleRepository flightHitsRuleRepository,
			FlightHitsWatchlistRepository flightHitsWatchlistRepository, ApplicationContext applicationContext) {
		ruleService = rulesvc;
		this.appConfigurationService = appConfigurationService;
		this.hitDetailRepository = hitDetailRepository;
		this.hitsSummaryRepository = hitsSummaryRepository;
		this.passengerService = passengerService;
		this.messageStatusRepository = messageStatusRepository;
		this.ruleMetaRepository = ruleMetaRepository;
		this.flightHitsRuleRepository = flightHitsRuleRepository;
		this.flightHitsWatchlistRepository = flightHitsWatchlistRepository;
		this.applicationContext = applicationContext;
	}

	private TargetingResultServices getTargetingResultOptions() {
		TargetingResultServices targetingResultServices = new TargetingResultServices();
		targetingResultServices.setAppConfigurationService(appConfigurationService);
		targetingResultServices.setPassengerService(passengerService);
		targetingResultServices.setRuleMetaRepository(ruleMetaRepository);
		return targetingResultServices;
	}

	@Override
	@Transactional
	public RuleResultsWithMessageStatus analyzeLoadedMessages(List<MessageStatus> source, Map<String, KIEAndLastUpdate> rules) {
		logger.info("Entering analyzeLoadedMessages()");

		RuleResultsWithMessageStatus ruleResultsWithMessageStatus = new RuleResultsWithMessageStatus();
		if (source.isEmpty()) {
			return ruleResultsWithMessageStatus;
		}
		List<Message> target = new ArrayList<>();
		List<MessageStatus> procssedMessages = new ArrayList<>();
		for (MessageStatus ms : source) {
			Message message = ms.getMessage();
			target.add(message);
			procssedMessages.add(ms);
		}
		ruleResultsWithMessageStatus.setMessageStatusList(procssedMessages);

		RuleResults ruleResults = null;
		try {
			logger.info("About to execute rules");
			ruleResults = executeRules(target, rules);
			logger.info("updating messages status from loaded to analyzed.");
			for (MessageStatus ms : procssedMessages) {
				ms.setMessageStatusEnum(MessageStatusEnum.RUNNING_RULES);
			}
			messageStatusRepository.saveAll(procssedMessages);
			ruleResultsWithMessageStatus.setRuleResults(ruleResults);
			ruleResultsWithMessageStatus.setMessageStatusList(procssedMessages);
			logger.info("Exiting analyzeLoadedMessages()");
			return ruleResultsWithMessageStatus;
		} catch (CommonServiceException cse) {
			if (cse.getErrorCode().equals(RuleServiceConstants.KB_NOT_FOUND_ERROR_CODE)
					|| cse.getErrorCode().equals(RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE)) {
				logger.info("************************");
				logger.info(cse.getMessage());
				logger.info("************************");
				for (MessageStatus ms : procssedMessages) {
					ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
					messageStatusRepository.saveAll(procssedMessages);
				}
			} else {
				for (MessageStatus ms : procssedMessages) {
					ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
					messageStatusRepository.saveAll(procssedMessages);
				}
				throw cse;
			}
		}
		ruleResultsWithMessageStatus.setRuleResults(ruleResults);
		return ruleResultsWithMessageStatus;
	}

	public RuleExecutionContext createPnrApisRequestContext(final List<Message> loadedMessages) {
		logger.info("Entering createPnrApisRequestContext().");
		List<Pnr> pnrList = new ArrayList<>();
		List<ApisMessage> apisMessages = new ArrayList<>();
		if (loadedMessages != null) {
			for (Message message : loadedMessages) {
				if (message instanceof ApisMessage) {
					apisMessages.add((ApisMessage) message);
				} else if (message instanceof Pnr) {
					pnrList.add((Pnr) message);
				}
			}
		}
		RuleEngineRequestBuilder bldr = applicationContext.getBean(RuleEngineRequestBuilder.class);

		if (!pnrList.isEmpty()) {
			bldr.addPnr(pnrList);
		}
		if (!apisMessages.isEmpty()) {
			bldr.addApisMessage(apisMessages);
		}

		RuleExecutionContext context = new RuleExecutionContext();

		context.setRuleServiceRequest(bldr.build());
		logger.info("Exiting createPnrApisRequestContext().");
		return context;
	}

	private RuleResults executeRules(List<Message> target, Map<String, KIEAndLastUpdate> rules) {
		logger.debug("Entering executeRules().");
		RuleExecutionContext ctx = createPnrApisRequestContext(target);
		logger.debug("Running Rule set.");
		// default knowledge Base is the UDR KB
		RuleServiceResult udrResult = ruleService.invokeRuleEngine(ctx.getRuleServiceRequest(), RuleConstants.UDR_KNOWLEDGE_BASE_NAME, rules);
		logger.debug("Ran Rule set.");
		if (udrResult != null) {
			RuleExecutionStatistics res = udrResult.getExecutionStatistics();
			int totalRulesFired = res.getTotalRulesFired();
			List<String> ruleFireSequence = res.getRuleFiringSequence();
			logger.debug("Total UDR rules fired: " + totalRulesFired);
			logger.debug("\n****************UDR Rule firing sequence***************************\n");
			for (String str : ruleFireSequence) {
				logger.debug("UDR Rule fired: " + str);
			}
			logger.debug("\n\n**********************************************************************");
		}
		RuleServiceResult wlResult = ruleService.invokeRuleEngine(ctx.getRuleServiceRequest(),
				WatchlistConstants.WL_KNOWLEDGE_BASE_NAME, rules);
		RuleResults ruleResults = new RuleResults();
		ruleResults.setUdrResult(udrResult);
		ruleResults.setWatchListResult(wlResult);
		logger.debug("Exiting executeRules().");
		return ruleResults;
	}

	private List<RuleHitDetail> getRuleDetails(RuleResults ruleResults) {
		TargetingResultServices targetingResultServices = getTargetingResultOptions();
		if (ruleResults.getUdrResult() != null) {
			RuleServiceResult udrResult = ruleResults.getUdrResult();
			udrResult = TargetingResultUtils.ruleResultPostProcesssing(udrResult, targetingResultServices);
			ruleResults.setUdrResult(udrResult);
		}
		if (ruleResults.getWatchListResult() != null) {
			RuleServiceResult watchlistResult = ruleResults.getWatchListResult();
			watchlistResult = TargetingResultUtils.ruleResultPostProcesssing(watchlistResult, targetingResultServices);
			ruleResults.setWatchListResult(watchlistResult);
		}

		RuleServiceResult ruleServiceResult = new CompositeRuleServiceResult(ruleResults.getWatchListResult(),
				ruleResults.getUdrResult());
		return ruleServiceResult.getResultList();
	}

	/**
	 * Filtering whitelist.
	 *
	 * the w vos the passenger true, if successful if (udrResult == null && wlResult
	 * == null) { // currently only two knowledgebases: udr and Watchlist
	 * KnowledgeBase udrKb = rulePersistenceService
	 * .findUdrKnowledgeBase(RuleConstants.UDR_KNOWLEDGE_BASE_NAME); if (udrKb ==
	 * null) { KnowledgeBase wlKb = rulePersistenceService
	 * .findUdrKnowledgeBase(WatchlistConstants.WL_KNOWLEDGE_BASE_NAME); if (wlKb ==
	 * null) { throw ErrorHandlerFactory .getErrorHandler() .createException(
	 * RuleServiceConstants.KB_NOT_FOUND_ERROR_CODE,
	 * RuleConstants.UDR_KNOWLEDGE_BASE_NAME + "/" +
	 * WatchlistConstants.WL_KNOWLEDGE_BASE_NAME); } else { // No enabled but
	 * disabled wl rule exists throw ErrorHandlerFactory .getErrorHandler()
	 * .createException( RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE,
	 * RuleServiceConstants.NO_ENABLED_RULE_ERROR_MESSAGE); } } else { // No enabled
	 * but disabled udr rule exists throw
	 * ErrorHandlerFactory.getErrorHandler().createException(
	 * RuleServiceConstants.NO_ENABLED_RULE_ERROR_CODE,
	 * RuleServiceConstants.NO_ENABLED_RULE_ERROR_MESSAGE); } } Bench.end("fourth",
	 * "executeRules invokeRuleEngine second end"); // eliminate duplicates
	 * TargetingResultServices targetingResultServices =
	 * getTargetingResultOptions(); if (udrResult != null) {
	 * <p>
	 * logger.debug("Eliminate duplicates from UDR rule running.");
	 * Bench.start("fifth", "executeRules TargetingResultUtils
	 * ruleResultPostProcesssing udrResult start"); udrResult = TargetingResultUtils
	 * .ruleResultPostProcesssing(udrResult, targetingResultServices);
	 * Bench.end("fifth", "executeRules TargetingResultUtils
	 * ruleResultPostProcesssing udrResult end"); //make a call to Case Mgmt.
	 * Bench.start("sixth", "executeRules TargetingResultCaseMgmtUtils
	 * ruleResultPostProcesssing udrResult start");
	 * TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(udrResult,
	 * caseDispositionService, passengerService); Bench.end("sixth", "executeRules
	 * TargetingResultCaseMgmtUtils ruleResultPostProcesssing udrResult end");
	 * <p>
	 * } if (wlResult != null) { logger.debug("Eliminate duplicates from watchlist
	 * rule running."); Bench.start("seventh", "executeRules TargetingResultUtils
	 * ruleResultPostProcesssing wlResult start"); wlResult =
	 * TargetingResultUtils.ruleResultPostProcesssing(wlResult,
	 * targetingResultServices); Bench.end("seventh", "executeRules
	 * TargetingResultUtils ruleResultPostProcesssing wlResult end"); //make a call
	 * to Case Mgmt. Bench.start("eighth", "executeRules TargetingResultUtils
	 * TargetingResultCaseMgmtUtils wlResult start");
	 * TargetingResultCaseMgmtUtils.ruleResultPostProcesssing(wlResult,
	 * caseDispositionService, passengerService); Bench.end("eighth", "executeRules
	 * TargetingResultUtils TargetingResultCaseMgmtUtils wlResult end");
	 * <p>
	 * }
	 * <p>
	 * TargetingResultUtils.updateRuleExecutionContext(ctx, new
	 * CompositeRuleServiceResult(udrResult, wlResult));
	 */
	/*
	 * private boolean filteringWhitelist(List<WhitelistVo> wVos, Passenger
	 * passenger) { if (wVos.isEmpty()) return false; Set<Document> docs =
	 * passenger.getDocuments(); List<WhitelistVo> pwlVos = new ArrayList<>();
	 * docs.forEach(doc -> { WhitelistVo pwl = new WhitelistVo();
	 * pwl.setFirstName(passenger.getPassengerDetails().getFirstName());
	 * pwl.setMiddleName(passenger.getPassengerDetails().getMiddleName());
	 * pwl.setLastName(passenger.getPassengerDetails().getLastName());
	 * pwl.setGender(passenger.getPassengerDetails().getGender());
	 * pwl.setDob(passenger.getPassengerDetails().getDob());
	 * pwl.setNationality(passenger.getPassengerDetails().getNationality());
	 * pwl.setResidencyCountry(passenger.getPassengerDetails().getResidencyCountry()
	 * ); pwl.setDocumentType(doc.getDocumentType());
	 * pwl.setDocumentNumber(doc.getDocumentNumber());
	 * pwl.setExpirationDate(doc.getExpirationDate());
	 * pwl.setIssuanceDate(doc.getIssuanceDate());
	 * pwl.setIssuanceCountry(doc.getIssuanceCountry()); pwlVos.add(pwl); }); for
	 * (WhitelistVo newwl : pwlVos) { for (WhitelistVo wlv : wVos) { if
	 * (newwl.customEquals(wlv)) return true; } } return false;
	 * 
	 * }
	 */
	@Transactional
	@Override
	public void saveMessageStatuses(List<MessageStatus> messageStatuses) {
		messageStatusRepository.saveAll(messageStatuses);
	}

	@Override
	public Set<HitDetail> generateHitDetails(RuleResults ruleRunningResult) {
		logger.debug("in create hits and cases");
		Set<HitDetail> hitDetails = new HashSet<>();
		if (ruleRunningResult != null && ruleRunningResult.hasResults()) {
			List<RuleHitDetail> ruleHitDetailList = getRuleDetails(ruleRunningResult);
			for (RuleHitDetail ruleHitDetail : ruleHitDetailList) {
				HitDetail hitDetail = HitDetail.from(ruleHitDetail);
				hitDetails.add(hitDetail);
			}
		}
		return hitDetails;
	}
}