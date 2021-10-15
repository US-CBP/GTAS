/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.bo.TargetSummaryVo;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.job.config.JobSchedulerConfig;
import gov.gtas.model.HitDetail;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.model.PassengerWLTimestamp;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.KnowledgeBaseRepository;
import gov.gtas.repository.MessageStatusRepository;
import gov.gtas.repository.PassengerWatchlistRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.rule.KIEAndLastUpdate;
import gov.gtas.rule.RuleUtils;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.services.matcher.PassengerWatchlistAndHitDetails;
import gov.gtas.svc.TargetingService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.hazelcast.hibernate.serialization.Hibernate5CacheEntrySerializerHook;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

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
	private ExecutorService asyncHitPersistenceExecutor;
	private ExecutorService graphRulesExec;
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
	private JmsTemplate jmsTemplateFile;
	private List<String> ruleQueues = new ArrayList<>();
	private Map<String, String> ruleMap = new HashMap<>();
	private TargetingService targetingService;
	private PassengerWatchlistRepository passengerWatchlistRepository;
	private static final ReentrantLock watchlistSavingLock = new ReentrantLock();

	/* The targeting service. */
	/**
	 * 
	 * Instantiates a new rule runner scheduler. the targeting service
	 */
	@Autowired
	public RuleRunnerScheduler(ApplicationContext ctx, MessageStatusRepository messageStatusRepository,
			JobSchedulerConfig jobSchedulerConfig, PendingHitDetailRepository pendingHitDetailRepository,
			AppConfigurationRepository appConfigurationRepository, WatchlistService watchlistService,
			UdrService udrService, KnowledgeBaseRepository knowledgeBaseRepository, JmsTemplate jmsTemplateFile,
			@Value("#{'${rule.engine.list}'.split(',')}") List<String> ruleList, TargetingService targetingService,
			PassengerWatchlistRepository passengerWatchlistRepository) {
		this.watchlistService = watchlistService;
		this.udrService = udrService;
		this.messageStatusRepository = messageStatusRepository;
		this.jobSchedulerConfig = jobSchedulerConfig;
		this.pendingHitDetailRepository = pendingHitDetailRepository;
		this.appConfigurationRepository = appConfigurationRepository;
		this.knowledgeBaseRepository = knowledgeBaseRepository;
		this.jmsTemplateFile = jmsTemplateFile;
		this.ruleQueues = ruleList;
		this.targetingService = targetingService;
		this.passengerWatchlistRepository = passengerWatchlistRepository;
		for (int i = 0; i < ruleList.size(); i++) {
			if (i == ruleList.size() - 1) {
				ruleMap.put(ruleList.get(i), "GTAS_FINAL_PROCESS");
			} else {
				ruleMap.put(ruleList.get(i), ruleList.get(i + 1));
			}
		}

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
		this.asyncHitPersistenceExecutor = Executors.newFixedThreadPool(maxNumOfThreads);
		this.graphRulesExec = Executors.newFixedThreadPool(maxNumOfThreads);

		this.ctx = ctx;
	}
	//Check once a minute for new rules
	@Scheduled(fixedDelayString = "60000", initialDelayString = "60000")
	public void ruleEngineRebalance() throws InterruptedException {
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
	}
	//Check message status health every 30 minutes
	@Scheduled(fixedDelayString = "1800000", initialDelayString = "6000")	
	public void ruleStatusHealthCheck() throws InterruptedException {
		logger.info("Performing rule status health check.");
		List<MessageStatus> msList = messageStatusRepository.getOrphanedRuleRunningMessages(oneHourAgo());
		if (!msList.isEmpty()) {
			logger.error("RUNNING RULES FOR OVER 1 HOUR! LIKELY ERROR IN RULE RUNNER OR NO "
					+ "RULE RUNNERS RUNNING. Setting messages to ERROR STATE!");
			for (MessageStatus errMessage : msList) {
				errMessage.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
			}
			messageStatusRepository.saveAll(msList);
		}
		logger.info("Rule status health check completed.");
	}

    private Date oneHourAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pnrLdtCutOff = now.minusHours(1);
        return new Date(pnrLdtCutOff.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
    
    
	@Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
	public void asyncHitPersister() throws InterruptedException {
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
					flightIds = new HashSet<>();
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
			asyncHitPersistenceExecutor.invokeAll(list);
		}
	}

	/**
	 * rule engine
	 * @throws InterruptedException 
	 **/
	@Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
	public void ruleEngineMediator() throws IOException, InterruptedException {
		int messageLimit = this.jobSchedulerConfig.getMaxMessagesPerRuleRun();
		Long runningMessages = messageStatusRepository.getCountRunningRules();
		Long maxRunningRules = this.jobSchedulerConfig.getMaxRunningRules();
		if (runningMessages < maxRunningRules) {
			List<MessageStatus> source = messageStatusRepository
					.getMessagesFromStatus(MessageStatusEnum.LOADED.getName(), messageLimit);
			for (MessageStatus ms : source) {
				ms.setMessageStatusEnum(MessageStatusEnum.RUNNING_RULES);
			}
			Iterable<MessageStatus> savedSource = messageStatusRepository.saveAll(source);
			List<MessageStatus> msToMapper = prepareMessageStatusToSend(savedSource);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(msToMapper);
			if (!msToMapper.isEmpty()) {
				sendFileContent(ruleQueues.get(0), json);
			}
		} else {
			logger.info("RULES CURRENTLY RUNNING: " + runningMessages + " MAX CAPACITY: " + maxRunningRules + ". PAUSING FOR 1 MINUTE TO ALLOW CATCH UP"  );
			Thread.sleep(60000);
		}
	}

	private List<MessageStatus> prepareMessageStatusToSend(Iterable<MessageStatus> savedSource) {
		List<MessageStatus> msToMapper = new ArrayList<>();
		for (MessageStatus ms : savedSource) {
			MessageStatus msSource = new MessageStatus();
			msSource.setFlightId(ms.getFlightId());
			msSource.setMessageId(ms.getMessageId());
			msToMapper.add(msSource);
		}
		return msToMapper;
	}
	
	@Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
	private void graphRuleEngine() throws InterruptedException {
		int messageLimit = this.jobSchedulerConfig.getMaxMessagesPerRuleRun();
		int maxPassengers = this.jobSchedulerConfig.getMaxPassengersPerRuleRun();
		
		if (graphDbOn) {
			List<MessageStatus> source = messageStatusRepository.getMessagesFromStatus(MessageStatusEnum.NEO_LOADED.getName(),
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
				graphRulesExec.invokeAll(list);
			}
		}
	}

	private void sendFileContent(final String queue, final String stringFile) {
		jmsTemplateFile.send(queue, session -> 
			session.createObjectMessage(stringFile)
		);
	}

	@JmsListener(destination = "GTAS_RULE_ENGINE", concurrency = "10")
	public void ruleMediator(org.springframework.messaging.Message<?> message, javax.jms.Session session,
			javax.jms.Message msg) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		RuleResultsWithMessageStatus rrwms = mapper.readValue((String) message.getPayload(),
				RuleResultsWithMessageStatus.class);
		String previousQueue = rrwms.getQueueName();
		String nextQueue = ruleMap.get(previousQueue);
		Set<HitDetail> hits = rrwms.getHitDetails();
		Set<PendingHitDetails> pendingHits = PendingHitDetails.convertHits(hits);
		if (!pendingHits.isEmpty()) {
			pendingHitDetailRepository.saveAll(new ArrayList<>(pendingHits));
		}
		List<MessageStatus> msList = rrwms.getMessageStatusList();
		String json = mapper.writeValueAsString(msList);
		if (nextQueue.equals("GTAS_FINAL_PROCESS")) {
			if (!msList.isEmpty()) {
				Date analyzed = new Date();
				for (MessageStatus finalMessage : msList) {
					finalMessage.setAnalyzedTimestamp(analyzed);
				}
				messageStatusRepository.saveAll(msList);
			}
		} else {
			sendFileContent(nextQueue, json);
		}
	}

	@JmsListener(destination = "GTAS_FUZZY_MATCHER", concurrency = "10")
	public void fuzzyMatching(org.springframework.messaging.Message<?> message, javax.jms.Session session,
			javax.jms.Message msg) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<MessageStatus> messageStatuses = mapper.readValue((String) message.getPayload(),
				new TypeReference<List<MessageStatus>>() {
				});
		String previousQueue = "GTAS_FUZZY_MATCHER";
		MatchingService ms = ctx.getBean(MatchingService.class);
		RuleResultsWithMessageStatus rrwms = new RuleResultsWithMessageStatus();
		rrwms.setMessageStatusList(messageStatuses);
		rrwms.setQueueName(previousQueue);
		try {
			PassengerWatchlistAndHitDetails passengerWatchlistAndHitDetails = ms
					.findMatchesBasedOnTimeThreshold(messageStatuses);
			if (!passengerWatchlistAndHitDetails.getPartialWatchlistHits().isEmpty()) {
				rrwms.setHitDetails(passengerWatchlistAndHitDetails.getPartialWatchlistHits());
			}
			saveWatchlistTimestamps(passengerWatchlistAndHitDetails);
		} catch (Exception e) {
			for (MessageStatus mstat : messageStatuses) {
				mstat.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
			}
		}
		sendFileContent("GTAS_RULE_ENGINE", mapper.writeValueAsString(rrwms));
	}

	private void saveWatchlistTimestamps(PassengerWatchlistAndHitDetails passengerWatchlistAndHitDetails) {
		try {
			watchlistSavingLock.lock();
			Set<PassengerWLTimestamp> savingPassengerSet = passengerWatchlistAndHitDetails.getSavingPassengerSet();
			if (!savingPassengerSet.isEmpty()) {
				passengerWatchlistRepository.saveAll(savingPassengerSet);
			}
		} catch (Exception e) {
			logger.error("Can't save watchlist timestamp!");
		} finally {
			watchlistSavingLock.unlock();
		}
	}

}
