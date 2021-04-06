/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.constant.WatchlistConstants;
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
import gov.gtas.svc.util.HitDetailsWithMessageStatus;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;

import org.apache.commons.collections4.map.HashedMap;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static gov.gtas.repository.AppConfigurationRepository.RECOMPILE_RULES;
import static org.apache.http.util.TextUtils.isBlank;

/**
 * Rule Runner Scheduler class. Using Spring's Scheduled annotation for
 * scheduling tasks. The class reads configuration values from an external file.
 */
@Component
@ConditionalOnProperty(prefix = "rules", name = "enabled")
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
	private Map<String, KIEAndLastUpdate> cache = new ConcurrentHashMap<>();
	private int KB_LIST_SIZE = 9;
	/* The targeting service. */
/**
	 5
162+03.
* Instantiates a new rule runner scheduler. the targeting service
	 */
	@Autowired
	public RuleRunnerScheduler(ApplicationContext ctx, MessageStatusRepository messageStatusRepository,
			JobSchedulerConfig jobSchedulerConfig, PendingHitDetailRepository pendingHitDetailRepository,
			AppConfigurationRepository appConfigurationRepository, WatchlistService watchlistService,
			UdrService udrService, KnowledgeBaseRepository knowledgeBaseRepository) {
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
		if (!isBlank(recompileRulesAndWatchlist.getOption())
				&& Boolean.parseBoolean(recompileRulesAndWatchlist.getValue())) {
			logger.info("REBALANCING WL KBS!");
			watchlistService.rebalanceAndCreateWatchlist();
			logger.info("REBALANCING RULE KBS!");
			udrService.reblanceRules();
			logger.info("RECOMPILING FINISHED!");
			recompileRulesAndWatchlist.setValue("false");
			appConfigurationRepository.save(recompileRulesAndWatchlist);
		}

		int flightLimit = this.jobSchedulerConfig.getMaxFlightsPerRuleRun();
		Set<Number> flightIdsForPendingHits = pendingHitDetailRepository.getFlightsWithPendingHitsByLimit(flightLimit);
		if (!flightIdsForPendingHits.isEmpty()) {
			int flightsPerThread = this.jobSchedulerConfig.getMaxFlightsProcessedPerThread();
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
		List<MessageStatus> source = messageStatusRepository
				.getMessagesFromStatus(MessageStatusEnum.LOADED.getName(), messageLimit);
		int maxPassengers = this.jobSchedulerConfig.getMaxPassengersPerRuleRun();
		Set<MessageStatus> errors = new HashSet<>();
		if (!source.isEmpty()) {
			Map<Long, List<MessageStatus>> messageFlightMap = SchedulerUtils.geFlightMessageMap(source);
			int runningTotal = 0;
			List<MessageStatus> ruleThread = new ArrayList<>();

			List<DroolFactGatheringThread> list = new ArrayList<>();
			// First gather facts for drools
			int number = 0;
			for (List<MessageStatus> messageStatuses : messageFlightMap.values()) {
				for (MessageStatus ms : messageStatuses) {
					ruleThread.add(ms);
					Message message = ms.getMessage();
					if (message.getPassengerCount() != null) {
						runningTotal += message.getPassengerCount();
					}
				}
				if (runningTotal >= maxPassengers) {
					DroolFactGatheringThread worker = ctx.getBean(DroolFactGatheringThread.class);
					// worker.setRules(rules);
					worker.setMessageStatuses(ruleThread);
					list.add(worker);
					worker.setNumber(number);
					number++;
					ruleThread = new ArrayList<>();
					runningTotal = 0;
				}
				if (list.size() >= maxNumOfThreads - 1) {
					break;
				}
			}
			if (runningTotal != 0) {
				DroolFactGatheringThread worker = ctx.getBean(DroolFactGatheringThread.class);
				// worker.setRules(rules);
				worker.setMessageStatuses(ruleThread);
				worker.setNumber(number);
				list.add(worker);
			}
			source = null; // Alert that source can be GC'd.

			// Invoke fact gathering into rule execution context
			// After gathering facts execute them against the drools kbs
			List<Future<RuleExecutionContext>> recList = exec.invokeAll(list);
			List<RuleExecutionContext> readyList = new ArrayList<>();
			for (Future<RuleExecutionContext> fre : recList) {
				try {
					RuleExecutionContext rec = fre.get();
					readyList.add(rec);
				} catch (InterruptedException | ExecutionException e) {
					logger.error("Error getting rule facts!" + e);
				}
			}

			Map<Integer, List<RuleResultsWithMessageStatus>> ruleResultMap = new HashMap<>();
			Iterable<KnowledgeBase> kbs = knowledgeBaseRepository.findAll();
			List<List<KnowledgeBase>> kbsForEngine = new ArrayList<>();
			List<KnowledgeBase> kbList = new ArrayList<>();
			for (KnowledgeBase kb : kbs) {
				if (kbList.size() < KB_LIST_SIZE) {
					kbList.add(kb);
				} else {
					kbsForEngine.add(kbList);
					kbList = new ArrayList<>();
					kbList.add(kb);
				}
			}
			if (!kbList.isEmpty()) {
				kbsForEngine.add(kbList);
			}

			for (List<KnowledgeBase> kbToRunRulesOn : kbsForEngine) {
				logger.info("In kb list");
				for (KnowledgeBase ruleKb : kbToRunRulesOn) {
					List<RuleExecutionThread> droolExecutionThreads = new ArrayList<>();
					Map<String, KIEAndLastUpdate> rules = new HashedMap<>();
					if (cache.containsKey(ruleKb.getKbName())) {
						KIEAndLastUpdate kie = cache.get(ruleKb.getKbName());
						if (kie.getUpdated().before(ruleKb.getCreationDt())) {
							KIEAndLastUpdate kieAndLastUpdate = makeKieAndLastUpdate(ruleKb);
							if (kieAndLastUpdate == null) {
								continue;
							}
							cache.put(ruleKb.getKbName(), kieAndLastUpdate);
						} else {
							rules.put(ruleKb.getKbName(), cache.get(ruleKb.getKbName()));
						}
					} else {
						KIEAndLastUpdate kieAndLastUpdate = makeKieAndLastUpdate(ruleKb);
						if (kieAndLastUpdate == null) {
							continue;
						}
						rules.put(ruleKb.getKbName(), kieAndLastUpdate);
						if (cache.values().size() < KB_LIST_SIZE) {
							cache.put(ruleKb.getKbName(), kieAndLastUpdate);
						}
					}
					

					for (RuleExecutionContext rec : readyList) {
						List<MessageStatus> msList = rec.getSource();
						collectErrorMessages(errors, msList);
						if (!rec.getSource().isEmpty()) {
							RuleExecutionThread worker = ctx.getBean(RuleExecutionThread.class);
							worker.setRuleExecutionContext(rec);
							worker.setRules(rules);
							droolExecutionThreads.add(worker);
						}
					}

					List<Future<RuleResultsWithMessageStatus>> rresultFutures = exec.invokeAll(droolExecutionThreads);

					for (Future<RuleResultsWithMessageStatus> frmsFuture : rresultFutures) {
						try {
							logger.debug("processing frmsFuture");

							RuleResultsWithMessageStatus rec = frmsFuture.get();
							logger.debug("got rule results with message status");

							if (ruleResultMap.containsKey(rec.getNumber())) {
								ruleResultMap.get(rec.getNumber()).add(rec);
							} else {
								List<RuleResultsWithMessageStatus> rrList = new ArrayList<>();
								rrList.add(rec);
								ruleResultMap.put(rec.getNumber(), rrList);
							}
						} catch (InterruptedException | ExecutionException e) {
							logger.error("Error getting rule facts!" + e);
						}
					}
				}
			}
			// TODO: consolidate ruleresultswithmessagestattuses OR make sure they stay
			// distinct
			logger.debug("in hd creator ");
			List<HitDetailCreatorThread> hdCreatorThreads = new ArrayList<>();
			List<HitDetailsWithMessageStatus> hdwms = new ArrayList<>();

			// Gather up rule results and convert any hit details
			for (int num : ruleResultMap.keySet()) {
				//logger.info("in map gathering ");

				List<RuleResultsWithMessageStatus> rlList = ruleResultMap.get(num);
				for (RuleResultsWithMessageStatus rrwms : rlList) {
					if (!rrwms.getMessageStatusList().isEmpty()) {
						collectErrorMessages(errors, rrwms.getMessageStatusList());
						HitDetailCreatorThread worker = ctx.getBean(HitDetailCreatorThread.class);
						worker.setRuleResultsWithMessageStatus(rrwms);
						hdCreatorThreads.add(worker);
					}
				}
				List<Future<HitDetailsWithMessageStatus>> hdwmsListFutures = exec.invokeAll(hdCreatorThreads);
				List<HitDetailsWithMessageStatus> hd2 = new ArrayList<>();
				for (Future<HitDetailsWithMessageStatus> hdwmsList : hdwmsListFutures) {
					try {
						HitDetailsWithMessageStatus rec = hdwmsList.get();
						hd2.add(rec);
					} catch (InterruptedException | ExecutionException e) {
						logger.error("Error getting rule facts!" + e);
					}
				}

				HitDetailsWithMessageStatus hd1 = null;
				for (HitDetailsWithMessageStatus hdwmsItem : hd2) {
					if (hd1 == null) {
						hd1 = hdwmsItem;
					} else {
						hd1.getHitDetails().addAll(hdwmsItem.getHitDetails());
						// the same messages are always apart of the same rule context so it can be
						// assumed the hd is the same.
						for (MessageStatus ms : hdwmsItem.getMessageStatuses()) {
							collectErrorMessages(errors, hdwmsItem.getMessageStatuses());
						}
					}
					for (MessageStatus ms : hd1.getMessageStatuses()) {
						if (errors.contains(ms)) {
							ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
						}
					}
				}
				hdwms.add(hd1);
			}
			logger.debug("hd persistent thread ");

			List<HitPersistenceThread> hitPersistenceThreads = new ArrayList<>();
			for (HitDetailsWithMessageStatus hd : hdwms) {
				if (!hd.getMessageStatuses().isEmpty()) {
					HitPersistenceThread worker = ctx.getBean(HitPersistenceThread.class);
					worker.setHitDetails(hd.getHitDetails());
					worker.setMessageStatuses(hd.getMessageStatuses());
					hitPersistenceThreads.add(worker);
				}
			}
			exec.invokeAll(hitPersistenceThreads);
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

	private void collectErrorMessages(Set<MessageStatus> errors, List<MessageStatus> msList) {
		for (MessageStatus ms : msList) {
			if (MessageStatusEnum.PARTIAL_ANALYZE == ms.getMessageStatusEnum()) {
				errors.add(ms);
			}
			if (MessageStatusEnum.FAILED_ANALYZING == ms.getMessageStatusEnum()) {
				if (errors.contains(ms)) {
					errors.remove(ms);
				}
				errors.add(ms);
			}
		}
	}

	private void updateWatchlistKb(String kbName) throws IOException {
		KnowledgeBase wlKb = watchlistService.createAKnowledgeBase(kbName);
		if (wlKb != null) {
			addOrUpdateNameAndKie(wlKb, cache);
		}
	}

	private void updateRuleKb(String kbName) throws IOException {
		KnowledgeBase udrKnowledgeBase = udrService.recompileRules(kbName, "RULE_SCHEDULER");
		if (udrKnowledgeBase != null) {
			addOrUpdateNameAndKie(udrKnowledgeBase, cache);
		}
	}

	private void addOrUpdateNameAndKie(KnowledgeBase kb, Map<String, KIEAndLastUpdate> map) throws IOException {
		KIEAndLastUpdate kieAndLastUpdate = makeKieAndLastUpdate(kb);
		map.put(kb.getKbName(), kieAndLastUpdate);
	}

	private KIEAndLastUpdate makeKieAndLastUpdate(KnowledgeBase kb) throws IOException {

		logger.info("Creating a KB. This can take a long time...");
		boolean isWatchlist = kb.getKbName().startsWith(WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
		KIEAndLastUpdate kieAndLastUpdate = new KIEAndLastUpdate();

		KnowledgeBase newKb;
		if (isWatchlist) {
			newKb = watchlistService.createAKnowledgeBase(kb.getKbName());
		} else {
			newKb = udrService.recompileRules(kb.getKbName(), "RULE_SCHEDULER");
		}
		if (newKb != null) {
		KieBase kieBase = RuleUtils.createKieBaseFromDrlString(new String(newKb.getRulesBlob()));
		Date updatedDate = kb.getCreationDt();
		kieAndLastUpdate.setKieBase(kieBase);
		kieAndLastUpdate.setKbName(kb.getKbName());
		kieAndLastUpdate.setUpdated(updatedDate);
		
		logger.info("KB created!");
		} else {
			logger.info("KB has been deleted!");
			cache.remove(newKb.getKbName());
			kieAndLastUpdate = null;
		}

		return kieAndLastUpdate;
	}
}
