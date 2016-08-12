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
import gov.gtas.error.ErrorHandler;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.model.User;
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
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.TargetingResultUtils;
import gov.gtas.svc.util.TargetingServiceUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
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
	private UserService userService;
	@Autowired
	private UserServiceUtil userServiceUtil;

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

	@Override
	public RuleServiceResult applyRules(RuleServiceRequest request,
			String drlRules) {
		RuleServiceResult res = ruleService.invokeAdhocRulesFromString(
				drlRules, request);
		res = TargetingResultUtils.ruleResultPostProcesssing(res);
		return res;
	}

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
			Set<Flight> flights = new HashSet<Flight>();
			Set<Passenger> passengers = new HashSet<Passenger>();
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

	@Override
	@Transactional
	public RuleExecutionContext analyzeLoadedMessages(
			final boolean updateProcesssedMessageStat) {
		logger.info("Entering analyzeLoadedMessages()");
		Iterator<Message> source = messageRepository.findByStatus(
				MessageStatus.LOADED).iterator();
		List<Message> target = new ArrayList<Message>();
		source.forEachRemaining(target::add);

		RuleExecutionContext ctx = executeRules(target);

		logger.info("updating messages status from loaded to analyzed.");
		if (updateProcesssedMessageStat) {
			for (Message message : target) {
				message.setStatus(MessageStatus.ANALYZED);
			}
		}
		logger.info("Exiting analyzeLoadedMessages()");
		return ctx;
	}

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
									(RuleConstants.UDR_KNOWLEDGE_BASE_NAME
											+ "/" + WatchlistConstants.WL_KNOWLEDGE_BASE_NAME));
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

	@Override
	@Transactional
	public List<ApisMessage> retrieveApisMessage(MessageStatus messageStatus) {
		return apisMsgRepository.findByStatus(messageStatus);
	}

	@Override
	@Transactional
	public List<Pnr> retrievePnr(MessageStatus messageStatus) {
		return pnrMsgRepository.findByStatus(messageStatus);
	}

	@Override
	@Transactional
	public void updateApisMessage(ApisMessage message,
			MessageStatus messageStatus) {
		ApisMessage apisMessage = apisMsgRepository.findOne(message.getId());
		if (apisMessage != null) {
			apisMessage.setStatus(messageStatus);
		}
	}

	@Override
	@Transactional
	public void updatePnr(Pnr message, MessageStatus messageStatus) {
		Pnr pnr = pnrMsgRepository.findOne(message.getId());
		if (pnr != null) {
			pnr.setStatus(messageStatus);
		}
	}

	@Transactional
	public Set<Long> runningRuleEngine() {
		logger.info("Entering runningRuleEngine().");
		RuleExecutionContext ruleRunningResult = analyzeLoadedMessages(true);
		RuleExecutionStatistics ruleExeStatus = ruleRunningResult
				.getRuleExecutionStatistics();
		if (logger.isInfoEnabled()) {
			logger.info("TargetingServiceImpl.runningRuleEngine() - Total Rules fired. --> "
					+ ruleExeStatus.getTotalRulesFired());
		}

		List<HitsSummary> hitsSummary = storeHitsInfo(ruleRunningResult);
		Set<Long> uniqueFlights = new HashSet<>();
		for (HitsSummary s : hitsSummary) {
			passengerService.createDisposition(s);
			uniqueFlights.add(s.getFlight().getId());
		}
		writeAuditLogForTargetingRun(ruleRunningResult);
		logger.info("Exiting runningRuleEngine().");
		return uniqueFlights;
	}

	private void writeAuditLogForTargetingRun(
			RuleExecutionContext targetingResult) {
		try {
			Set<Long> passengerHits = new HashSet<Long>();
			int ruleHits = 0;
			int wlHits = 0;
			for (TargetSummaryVo hit : targetingResult.getTargetingResult()) {
				ruleHits += hit.getRuleHitCount();
				wlHits += hit.getWatchlistHitCount();
				passengerHits.add(hit.getPassengerId());
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

	@Transactional
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

	private List<HitsSummary> storeHitsInfo(
			RuleExecutionContext ruleRunningResult) {
		logger.info("Entering storeHitsInfo().");

		List<HitsSummary> hitsSummaryList = new ArrayList<HitsSummary>();
		Collection<TargetSummaryVo> results = ruleRunningResult
				.getTargetingResult();

		logger.info("Total new hits summary records --> " + results.size());
		Iterator<TargetSummaryVo> iter = results.iterator();
		while (iter.hasNext()) {
			TargetSummaryVo ruleDetail = iter.next();
			HitsSummary hitsSummary = constructHitsInfo(ruleDetail);
			hitsSummaryList.add(hitsSummary);
		}
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
		HitsSummary hitsSummary = new HitsSummary();
		Passenger foundPassenger = passengerRepository.findOne(hitSummmaryVo
				.getPassengerId());
		if (foundPassenger != null) {
			logger.info("Found passenger.");
			hitsSummary.setPassenger(foundPassenger);
			writeAuditLogForTargetingPassenger(foundPassenger);
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
		List<HitDetail> detailList = new ArrayList<HitDetail>();
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
			StringBuilder sb = new StringBuilder();
			if (passenger.getFirstName() != null) {
				sb.append(passenger.getFirstName());
			} else if (passenger.getMiddleName() != null) {
				sb.append(" " + passenger.getMiddleName());
			} else if (passenger.getLastName() != null) {
				sb.append(" " + passenger.getLastName());
			}
			actionData.addProperty("Passenger Name", sb.toString());
			actionData.addProperty("PassengerType",
					passenger.getPassengerType());

			// 
			String message = "API/PNR MESSAGE Ingest and Parsing  "
					+ passenger.getCreatedAt();
			String targetStr = target != null ? target.toString()
					: StringUtils.EMPTY;
			String actionDataStr = actionData != null ? actionData.toString()
					: StringUtils.EMPTY;
			auditLogRepository.save(new AuditRecord(
					AuditActionType.MESSAGE_INGEST_PARSING, targetStr,
					Status.SUCCESS, message, actionDataStr,
					fetchUser(GTAS_APPLICATION_USERID), passenger.getCreatedAt()));
			//
			String message2 = "Passenger Rule Hit and Case Open run on "
					+ new Date();			
			auditLogRepository.save(new AuditRecord(
					AuditActionType.RULE_HIT_CASE_OPEN, targetStr,
					Status.SUCCESS, message2, actionDataStr,
					fetchUser(GTAS_APPLICATION_USERID), new Date()));
		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}
	}

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

	/**
	 * Fetches the user object and throws an unchecked exception if the user
	 * cannot be found.
	 * 
	 * @param userId
	 *            the ID of the user to fetch.
	 * @return the user fetched from the DB.
	 */
	private User fetchUser(final String userId) {
		UserData userData = userService.findById(userId);
		User user = null;
		if (userData != null) {
			user = userServiceUtil.mapUserEntityFromUserData(userData);
		}
		if (user == null || user.getUserId() == null) {
			ErrorHandler errorHandler = ErrorHandlerFactory.getErrorHandler();
			throw errorHandler.createException(
					CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
		}
		return user;
	}
}
