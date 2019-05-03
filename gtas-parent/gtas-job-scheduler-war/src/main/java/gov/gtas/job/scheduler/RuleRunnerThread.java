package gov.gtas.job.scheduler;

import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.Case;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.TargetingServiceResults;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@Component
@Scope("prototype")
public class RuleRunnerThread implements Callable<Boolean> {


    private Logger logger = LoggerFactory.getLogger(RuleRunnerThread.class);

    private final ApplicationContext applicationContext;
    /**
     * The error persistence service.
     */
    private ErrorPersistenceService errorPersistenceService;

    private final AppConfigurationService appConfigurationService;

    private List<MessageStatus> messageStatuses = new ArrayList<>();


    public RuleRunnerThread(ErrorPersistenceService errorPersistenceService,
                            AppConfigurationService appConfigurationService, ApplicationContext applicationContext) {
        this.errorPersistenceService = errorPersistenceService;
        this.appConfigurationService = appConfigurationService;
        this.applicationContext = applicationContext;
    }


    public Boolean call() {
        logger.debug("Starting rule running scheduled task");
        boolean success = true;
        long start = System.nanoTime();
        RuleResultsWithMessageStatus ruleResults = null;
        TargetingService targetingService = applicationContext.getBean(TargetingService.class);
        try {
            ruleResults = targetingService.analyzeLoadedMessages(messageStatuses);
            List<TargetingServiceResults> targetingServiceResultsList = targetingService.createHitsAndCases(ruleResults.getRuleResults());
            logger.debug("About to batch");
            List<TargetingServiceResults> batchedTargetingServiceResults = batchResults(targetingServiceResultsList);
            logger.debug("done batching");
            int count = 1;
            if (ruleResults.getMessageStatusList() != null) {
                Date analyzedAt = new Date();
                ruleResults.getMessageStatusList().forEach(m -> {
                    m.setMessageStatusEnum(MessageStatusEnum.ANALYZED);
                    m.setAnalyzedTimestamp(analyzedAt);
                });
                for (TargetingServiceResults targetingServiceResults : batchedTargetingServiceResults) {
                    try {
                        logger.info("Saving rules/summary targeting results " + count + " of " + batchedTargetingServiceResults.size() + "...");
                        targetingService.saveEverything(targetingServiceResults);
                    } catch (Exception ignored) {
                        ruleResults.getMessageStatusList().forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.PARTIAL_ANALYZE));
                        logger.error("Failed to save rules summary count " + count + " with following stacktrace: ", ignored);
                    }
                    count++;
                }
                targetingService.saveMessageStatuses(ruleResults.getMessageStatusList());
                logger.info("Rules and Watchlist ran in " + (System.nanoTime() - start) / 1000000 + "m/s.");
            }
            logger.debug("entering matching service portion of jobScheduling");
            long fuzzyStart = System.nanoTime();
            MatchingService matchingService = applicationContext.getBean(MatchingService.class);
            int passengersProcessed = matchingService.findMatchesBasedOnTimeThreshold(messageStatuses);
            logger.debug("exiting matching service portion of jobScheduling");
            if (passengersProcessed > 0) {
                logger.info("Fuzzy Matching Run in  " + (System.nanoTime() - fuzzyStart) / 1000000 + "m/s.");
            }
            logger.debug("Total rule running scheduled task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
        } catch (Exception exception) {
            String errorMessage = exception.getCause() != null && exception.getCause().getMessage() != null ? exception.getCause().getMessage() : "Error in rule runner";
            logger.error(errorMessage);
            if (ruleResults != null) {
                ruleResults.getMessageStatusList().forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING));
                targetingService.saveMessageStatuses(ruleResults.getMessageStatusList());
            }
            ErrorDetailInfo errInfo = ErrorHandlerFactory
                    .createErrorDetails(RuleServiceConstants.RULE_ENGINE_RUNNER_ERROR_CODE, exception);
            errorPersistenceService.create(errInfo);
            success = false;
        }
        logger.debug("exiting jobScheduling()");
        return success;
    }


    private List<TargetingServiceResults> batchResults(List<TargetingServiceResults> targetingServiceResultsList) {

        int BATCH_SIZE = Integer.parseInt(appConfigurationService.findByOption(AppConfigurationRepository.MAX_FLIGHTS_SAVED_PER_BATCH).getValue());
        List<TargetingServiceResults> batchedResults = new ArrayList<>();
        TargetingServiceResults conglomerateResults = new TargetingServiceResults();
        int counter = 0;
        while (!targetingServiceResultsList.isEmpty()) {
            TargetingServiceResults targetingServiceResults = targetingServiceResultsList.get(0);
            Set<Case> casesSet = conglomerateResults.getCaseSet();
            List<HitsSummary> hitsSummaries = conglomerateResults.getHitsSummaryList();
            if (casesSet == null) {
                conglomerateResults.setCaseSet(targetingServiceResults.getCaseSet());
            } else {
                conglomerateResults.getCaseSet().addAll(targetingServiceResults.getCaseSet());
            }
            if (hitsSummaries == null) {
                conglomerateResults.setHitsSummaryList(targetingServiceResults.getHitsSummaryList());
            } else {
                conglomerateResults.getHitsSummaryList().addAll(targetingServiceResults.getHitsSummaryList());
            }
            counter++;
            if (targetingServiceResultsList.size() == 1) {
                batchedResults.add(conglomerateResults);
            } else {
                if (counter >= BATCH_SIZE) {
                    batchedResults.add(conglomerateResults);
                    conglomerateResults = new TargetingServiceResults();
                    counter = 1;
                }
            }
            targetingServiceResultsList.remove(0);
        }
        return batchedResults;
    }

    public void setMessageStatuses(List<MessageStatus> messageStatuses) {
        this.messageStatuses = messageStatuses;
    }
}
