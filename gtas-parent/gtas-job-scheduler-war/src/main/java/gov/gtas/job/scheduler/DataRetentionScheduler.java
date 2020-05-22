package gov.gtas.job.scheduler;

import gov.gtas.job.config.JobSchedulerConfig;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.repository.MessageStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ConditionalOnProperty(prefix = "retention", name = "enabled")
@Component
public class DataRetentionScheduler {

    /**
     * The Constant logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RuleRunnerScheduler.class);
    private final ApplicationContext ctx;
    private ExecutorService exec;
    private static final int DEFAULT_THREADS_ON_DATA_RETENTION = 5;
    private MessageStatusRepository messageStatusRepository;
    private int maxNumOfThreads = DEFAULT_THREADS_ON_DATA_RETENTION;
    private JobSchedulerConfig jobSchedulerConfig;

    public DataRetentionScheduler(ApplicationContext ctx,
                                  MessageStatusRepository messageStatusRepository,
                                  JobSchedulerConfig jobSchedulerConfig) {
        this.ctx = ctx;
        this.jobSchedulerConfig = jobSchedulerConfig;
        try {
            maxNumOfThreads = this.jobSchedulerConfig.getThreadsOnRules();
        } catch (Exception e) {
            logger.error(String.format(
                    "Failed to load application configuration: DATA_RETENTION_THREADS from application properties... Number of threads set to use %1$s",
                    DEFAULT_THREADS_ON_DATA_RETENTION));
        }
        this.exec = Executors.newFixedThreadPool(maxNumOfThreads);
        this.messageStatusRepository = messageStatusRepository;
    }

    @Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
    public void dataRetention() throws InterruptedException {
        int messageLimit = this.jobSchedulerConfig.messageOutProcessLimit();
        int pnrHourLimit = this.jobSchedulerConfig.getRetentionHoursMaskPNR();
        int apisHourLimit = this.jobSchedulerConfig.getRetentionHoursMaskAPIS();
        int pnrHourLimitDelete = this.jobSchedulerConfig.getRetentionHoursDeletePNR();
        int apisHourLimitDelete = this.jobSchedulerConfig.getRetentionHoursDeleteAPIS();

        List<String> pnrMessageStatusForMask = this.jobSchedulerConfig.getMessageStatusMaskRetentionPNR();
        List<String> apisMessageStatusForMask = this.jobSchedulerConfig.getMessageStatusMaskRetentionAPIS();
        List<String> pnrMessageStatusForDelete = this.jobSchedulerConfig.getMessageStatusDeleteRetentionPNR();
        List<String> apisMessageStatusForDelete = this.jobSchedulerConfig.getMessageStatusDeletionRetentionAPIS();

        boolean pnrJob = this.jobSchedulerConfig.isPnrRetentionDataJob();
        boolean apisJob = this.jobSchedulerConfig.isAPISRetentionDataJob();
        int maxPassengers = this.jobSchedulerConfig.messagePassengerOutProcessThreadLimit();

        Date convertedPnrDateMask = getDate(pnrHourLimit);
        Date convertedAPISDateMask = getDate(apisHourLimit);

        Date convertedAPISDateDelete = getDate(apisHourLimitDelete);
        Date convertedPnrDateDelete = getDate(pnrHourLimitDelete);


        if (pnrJob) {
            List<MessageStatus> messagesForPnrOutProcess = messageStatusRepository.getMessagesToOutProcess(messageLimit, convertedPnrDateMask, pnrMessageStatusForMask);
            if (!messagesForPnrOutProcess.isEmpty()) {
                List<PnrDataMaskThread> list = getRetentionThreads(messagesForPnrOutProcess, convertedAPISDateMask, convertedPnrDateMask,  maxPassengers, PnrDataMaskThread.class);
                messagesForPnrOutProcess = null; // Alert to be GC'd.
                exec.invokeAll(list);
            }
            List<MessageStatus> messagesToRunDeleteOn = messageStatusRepository.getMessagesToOutProcess(messageLimit, convertedPnrDateMask, pnrMessageStatusForDelete);
            if (!messagesToRunDeleteOn.isEmpty()) {
                List<PnrDataDeletionThread> list = getRetentionThreads(messagesToRunDeleteOn, convertedAPISDateDelete, convertedPnrDateDelete,  maxPassengers, PnrDataDeletionThread.class);
                //noinspection UnusedAssignment
                messagesToRunDeleteOn = null; // Alert to be GC'd.
                exec.invokeAll(list);
            }
        }

        if (apisJob) {
            List<MessageStatus> messagesForAPISMask = messageStatusRepository.getMessagesToOutProcess(messageLimit, convertedAPISDateMask, apisMessageStatusForMask);
            if (!messagesForAPISMask.isEmpty()) {
                List<ApisDataMaskThread> list = getRetentionThreads(messagesForAPISMask, convertedAPISDateMask, convertedPnrDateMask, maxPassengers, ApisDataMaskThread.class);
                //noinspection UnusedAssignment
                messagesForAPISMask = null; // Alert to be GC'd.
                exec.invokeAll(list);
            }
            List<MessageStatus> messagesForAPISDelete = messageStatusRepository.getMessagesToOutProcess(messageLimit, convertedAPISDateDelete, apisMessageStatusForDelete);
            if (!messagesForAPISDelete.isEmpty()) {
                List<ApisDataDeletionThread> list = getRetentionThreads(messagesForAPISDelete, convertedAPISDateDelete, convertedPnrDateDelete, maxPassengers, ApisDataDeletionThread.class);
                //noinspection UnusedAssignment
                messagesForAPISDelete = null; // Alert to be GC'd.
                exec.invokeAll(list);
            }
        }
    }

    private Date getDate(int hourLimit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pnrLdtCutOff = now.minusHours(hourLimit);
        return new Date(pnrLdtCutOff.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    private <T extends DataSchedulerThread> List<T> getRetentionThreads(List<MessageStatus> messagesToOutProcess, Date apisCutOffDate, Date pnrCutOffDate, int maxPassengers, Class<T> threadType) {
        Map<Long, List<MessageStatus>> messageFlightMap = SchedulerUtils.geFlightMessageMap(messagesToOutProcess);
        int runningTotal = 0;
        List<MessageStatus> ruleThread = new ArrayList<>();
        List<T> list = new ArrayList<>();
        for (List<MessageStatus> messageStatuses : messageFlightMap.values()) {
            for (MessageStatus ms : messageStatuses) {
                ruleThread.add(ms);
                Message message = ms.getMessage();
                if (message.getPassengerCount() != null) {
                    runningTotal += message.getPassengerCount();
                }
            }
            if (runningTotal >= maxPassengers) {
                T worker = ctx.getBean(threadType);
                worker.setApisCutOffDate(apisCutOffDate);
                worker.setPnrCutOffDate(pnrCutOffDate);
                worker.setMessageStatuses(ruleThread);
                worker.setDefaultShareConstraint(new DefaultShareConstraint());
                list.add(worker);
                ruleThread = new ArrayList<>();
                runningTotal = 0;

            }
            if (list.size() >= maxNumOfThreads - 1) {
                break;
            }
        }
        if (runningTotal != 0) {
            T worker = ctx.getBean(threadType);
            worker.setMessageStatuses(ruleThread);
            worker.setApisCutOffDate(apisCutOffDate);
            worker.setPnrCutOffDate(pnrCutOffDate);
            worker.setDefaultShareConstraint(new DefaultShareConstraint());
            list.add(worker);
        }
        return list;
    }

}
