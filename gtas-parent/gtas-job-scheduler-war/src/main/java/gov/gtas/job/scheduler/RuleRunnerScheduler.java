/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.MessageStatusRepository;
import gov.gtas.services.AppConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Rule Runner Scheduler class. Using Spring's Scheduled annotation for
 * scheduling tasks. The class reads configuration values from an external file.
 */
@Component
@Conditional(RuleRunnerCondition.class)
public class RuleRunnerScheduler {

	/**
	 * The Constant logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RuleRunnerScheduler.class);
	private final ApplicationContext ctx;
	private ExecutorService exec;
	private static final int DEFAULT_THREADS_ON_RULES = 5;
	private MessageStatusRepository messageStatusRepository;
	private final AppConfigurationService appConfigurationService;
	private int maxNumOfThreads = DEFAULT_THREADS_ON_RULES;
	private boolean graphDbOn;

	/* The targeting service. */

	/**
	 * Instantiates a new rule runner scheduler. the targeting service
	 */
	@Autowired
	public RuleRunnerScheduler(ApplicationContext ctx, MessageStatusRepository messageStatusRepository,
			AppConfigurationService appConfigurationService) {
		this.messageStatusRepository = messageStatusRepository;
		this.appConfigurationService = appConfigurationService;

		try {
			graphDbOn = Boolean.parseBoolean(
					appConfigurationService.findByOption(AppConfigurationRepository.GRAPH_DB_TOGGLE).getValue());
		} catch (Exception e) {
			logger.error("Failed to get graph db toggle. Graph rules will be OFF.");
		}
		try {
			maxNumOfThreads = Integer.parseInt(
					appConfigurationService.findByOption(AppConfigurationRepository.THREADS_ON_RULES).getValue());
		} catch (Exception e) {
			logger.error(String.format(
					"Failed to load application configuration: '%1$s' from the database... Number of threads set to use %2$s",
					AppConfigurationRepository.THREADS_ON_RULES, DEFAULT_THREADS_ON_RULES));
		}
		this.exec = Executors.newFixedThreadPool(maxNumOfThreads);
		this.ctx = ctx;
	}

	/**
	 * Job scheduling.
	 */
	// This is commented out as a scheduled task in order to remove concurrency
	// issues in the DB involving loader and rule runner. This may not be the final
	// solution to the problem
	// but it suffices for now. The rule running portion of the scheduler is now
	// tacked into the loader portion at the bottom to insure sequential operation.
	@Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
	public void jobScheduling() throws InterruptedException {

		int messageLimit = Integer.parseInt(
				appConfigurationService.findByOption(AppConfigurationRepository.MAX_MESSAGES_PER_RULE_RUN).getValue());
		List<MessageStatus> source = messageStatusRepository.getMessagesFromStatus(MessageStatusEnum.LOADED.getName(),
				messageLimit);
		int maxPassengers = Integer.parseInt(appConfigurationService
				.findByOption(AppConfigurationRepository.MAX_PASSENGERS_PER_RULE_RUN).getValue());
		if (!source.isEmpty()) {
			Map<Long, List<MessageStatus>> messageFlightMap = geFlightMessageMap(source);
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
				worker.setMessageStatuses(ruleThread);
				list.add(worker);
			}
			exec.invokeAll(list);
		}

		if (graphDbOn) {
			source = messageStatusRepository.getMessagesFromStatus(MessageStatusEnum.NEO_LOADED.getName(),
					messageLimit);
			if (!source.isEmpty()) {
				Map<Long, List<MessageStatus>> messageFlightMap = geFlightMessageMap(source);
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
				exec.invokeAll(list);
			}
		}
	}

	private Map<Long, List<MessageStatus>> geFlightMessageMap(List<MessageStatus> source) {
		Map<Long, List<MessageStatus>> messageFlightMap = new HashMap<>();
		for (MessageStatus messageStatus : source) {
			Long flightId = messageStatus.getFlightId();
			if (messageFlightMap.containsKey(flightId)) {
				messageFlightMap.get(flightId).add(messageStatus);
			} else {
				List<MessageStatus> messageStatuses = new ArrayList<>();
				messageStatuses.add(messageStatus);
				messageFlightMap.put(flightId, messageStatuses);
			}
		}
		return messageFlightMap;
	}

}
