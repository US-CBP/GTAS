/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.constant.RuleConstants;
import gov.gtas.job.config.JobSchedulerConfig;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.KnowledgeBaseRepository;
import gov.gtas.repository.MessageStatusRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.rule.KIEAndLastUpdate;
import gov.gtas.rule.RuleUtils;
import gov.gtas.svc.UdrService;
import gov.gtas.svc.WatchlistService;
import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static gov.gtas.repository.AppConfigurationRepository.RECOMPILE_RULES;
import static org.apache.http.util.TextUtils.isBlank;

/**
 * Rule Runner Scheduler class. Using Spring's Scheduled annotation for
 * scheduling tasks. The class reads configuration values from an external file.
 */
@Component@ConditionalOnProperty(prefix = "rules", name = "enabled")
public class RuleRunnerScheduler {

	/**
	 * The Constant logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RuleRunnerScheduler.class);
	private final ApplicationContext ctx;
	private ExecutorService exec;
	private static final int DEFAULT_THREADS_ON_RULES = 5;
	private MessageStatusRepository messageStatusRepository;
	private int maxNumOfThreads = DEFAULT_THREADS_ON_RULES;
	private boolean graphDbOn;
	private JobSchedulerConfig jobSchedulerConfig;
	private PendingHitDetailRepository pendingHitDetailRepository;
	private AppConfigurationRepository appConfigurationRepository;
	private WatchlistService watchlistService;
	private UdrService udrService;
	private KnowledgeBaseRepository knowledgeBaseRepository;
	private Map<String, KIEAndLastUpdate> rules = new ConcurrentHashMap<>();

	/* The targeting service. */

	/**
	 * Instantiates a new rule runner scheduler. the targeting service
	 */
	@Autowired
	public RuleRunnerScheduler(ApplicationContext ctx, MessageStatusRepository messageStatusRepository,
							   JobSchedulerConfig jobSchedulerConfig,
							   PendingHitDetailRepository pendingHitDetailRepository,
							   AppConfigurationRepository appConfigurationRepository,
							   WatchlistService watchlistService,
							   UdrService udrService,
							   KnowledgeBaseRepository knowledgeBaseRepository) {
		this.watchlistService = watchlistService;
		this.udrService = udrService;
		this.messageStatusRepository = messageStatusRepository;
		this.jobSchedulerConfig = jobSchedulerConfig;
		this.pendingHitDetailRepository = pendingHitDetailRepository;
		this.appConfigurationRepository = appConfigurationRepository;
		this.knowledgeBaseRepository = knowledgeBaseRepository;

		try {
			graphDbOn = this.jobSchedulerConfig.getNeo4JRuleEngineEnabled();
		} catch (Exception e) {
			logger.error("Failed to get graph db toggle. Graph rules will be OFF.");
		}
		try {
			maxNumOfThreads = this.jobSchedulerConfig.getThreadsOnRules();
		} catch (Exception e) {
			logger.error(String.format(
					"Failed to load application configuration: THREADS_ON_RULES from application properties... Number of threads set to use %1$s",
					DEFAULT_THREADS_ON_RULES));
		}
		this.exec = Executors.newFixedThreadPool(maxNumOfThreads);
		this.ctx = ctx;
	}

	/**
	 * rule engine
	 **/
	@Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
	public void ruleEngine() throws InterruptedException, IOException {

		AppConfiguration recompileRulesAndWatchlist = appConfigurationRepository.findByOption(RECOMPILE_RULES);
		if (!isBlank(recompileRulesAndWatchlist.getOption()) && Boolean.parseBoolean(recompileRulesAndWatchlist.getValue())) {
			logger.info("RECOMPILING KBS!");
			watchlistService.activateAllWatchlists();
			udrService.recompileRules(RuleConstants.UDR_KNOWLEDGE_BASE_NAME, "RULE_SCHEDULER");
			recompileRulesAndWatchlist.setValue("false");
			appConfigurationRepository.save(recompileRulesAndWatchlist);
		}

		Iterable<KnowledgeBase> kbs = knowledgeBaseRepository.findAll();
		for (KnowledgeBase kb : kbs ) {
			if (rules.containsKey(kb.getKbName())) {
				KIEAndLastUpdate kau = rules.get(kb.getKbName());
				if (kau.getUpdated().before(kb.getCreationDt())) {
					logger.info("updating rule runner kie for " + kb.getKbName());
					addOrUpdateNameAndKie(kb);
					logger.info("Done updating rule runner kie for " + kb.getKbName());
				}
			} else {
				logger.info("making new rule kie for " + kb.getKbName());
				addOrUpdateNameAndKie(kb);
				logger.info("Done creating rule kie for " + kb.getKbName());
			}
		}

		int flightLimit = this.jobSchedulerConfig.getMaxFlightsPerRuleRun();
		Set<Number> flightIdsForPendingHits = pendingHitDetailRepository.getFlightsWithPendingHitsByLimit(flightLimit);
		if (!flightIdsForPendingHits.isEmpty()) {
			int flightsPerThread =  this.jobSchedulerConfig.getMaxFlightsProcessedPerThread();
			List<AsyncHitPersistenceThread> list = new ArrayList<>();
			int runningTotal = 0;
			Set<Long> flightIds = new HashSet<>();
			for (Number flightId : flightIdsForPendingHits) {
				flightIds.add(flightId.longValue());
				runningTotal++;
				if (runningTotal >= flightsPerThread) {
					AsyncHitPersistenceThread worker = ctx.getBean(AsyncHitPersistenceThread.class);
					worker.setFlightIds(flightIds);
					list.add(worker);
					runningTotal = 0;
				}
				if (list.size() >= maxNumOfThreads - 1) {
					break;
				}
			}
			if (runningTotal != 0) {
				AsyncHitPersistenceThread worker = ctx.getBean(AsyncHitPersistenceThread.class);
				worker.setFlightIds(flightIds);
				list.add(worker);
			}
			exec.invokeAll(list);
		}

		int messageLimit = this.jobSchedulerConfig.getMaxMessagesPerRuleRun();
		List<MessageStatus> source = messageStatusRepository.getMessagesFromStatus(MessageStatusEnum.LOADED.getName(),
				messageLimit);
		int maxPassengers = this.jobSchedulerConfig.getMaxPassengersPerRuleRun();
		if (!source.isEmpty()) {
			Map<Long, List<MessageStatus>> messageFlightMap = SchedulerUtils.geFlightMessageMap(source);
			int runningTotal = 0;
			List<MessageStatus> ruleThread = new ArrayList<>();
			List<RuleRunnerThread> list = new ArrayList<>();
			for (List<MessageStatus> messageStatuses : messageFlightMap.values()) {
				for (MessageStatus ms : messageStatuses) {
					ruleThread.add(ms);
					Message message = ms.getMessage();
					if (message.getPassengerCount() != null) {
						runningTotal += message.getPassengerCount();
					}
				}
				if (runningTotal >= maxPassengers) {
					RuleRunnerThread worker = ctx.getBean(RuleRunnerThread.class);
					worker.setRules(rules);
					worker.setMessageStatuses(ruleThread);
					list.add(worker);
					ruleThread = new ArrayList<>();
					runningTotal = 0;

				}
				if (list.size() >= maxNumOfThreads - 1) {
					break;
				}
			}
			if (runningTotal != 0) {
				RuleRunnerThread worker = ctx.getBean(RuleRunnerThread.class);
				worker.setRules(rules);
				worker.setMessageStatuses(ruleThread);
				list.add(worker);
			}
			source = null; // Alert that source can be GC'd.
			exec.invokeAll(list);
		}

		if (graphDbOn) {
			source = messageStatusRepository.getMessagesFromStatus(MessageStatusEnum.NEO_LOADED.getName(),
					messageLimit);
			if (!source.isEmpty()) {
				Map<Long, List<MessageStatus>> messageFlightMap = SchedulerUtils.geFlightMessageMap(source);
				int runningTotal = 0;
				List<MessageStatus> ruleThread = new ArrayList<>();
				List<GraphRulesThread> list = new ArrayList<>();
				for (List<MessageStatus> messageStatuses : messageFlightMap.values()) {
					for (MessageStatus ms : messageStatuses) {
						ruleThread.add(ms);
						Message message = ms.getMessage();
						if (message.getPassengerCount() != null) {
							runningTotal += message.getPassengerCount();
						}
					}
					if (runningTotal >= maxPassengers) {
						GraphRulesThread worker = ctx.getBean(GraphRulesThread.class);
						worker.setMessageStatuses(ruleThread);
						list.add(worker);
						ruleThread = new ArrayList<>();
						runningTotal = 0;
					}
					if (list.size() >= maxNumOfThreads - 1) {
						break;
					}
				}
				if (runningTotal != 0) {
					GraphRulesThread worker = ctx.getBean(GraphRulesThread.class);
					worker.setMessageStatuses(ruleThread);
					list.add(worker);
				}
				source = null; // Alert that source can be GC'd.
				exec.invokeAll(list);
			}
		}
	}

	private void addOrUpdateNameAndKie(KnowledgeBase kb) throws IOException {
		KieBase kieBase = RuleUtils.createKieBaseFromDrlString(new String(kb.getRulesBlob()));
		Date updatedDate = kb.getCreationDt();
		KIEAndLastUpdate KIEAndLastUpdate = new KIEAndLastUpdate();
		KIEAndLastUpdate.setKieBase(kieBase);
		KIEAndLastUpdate.setUpdated(updatedDate);
		rules.put(kb.getKbName(), KIEAndLastUpdate);
	}
}
