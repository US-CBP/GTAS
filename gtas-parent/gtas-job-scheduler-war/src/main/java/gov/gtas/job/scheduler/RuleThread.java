package gov.gtas.job.scheduler;

import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import freemarker.template.TemplateException;
import gov.gtas.aws.HitNotificationConfig;
import gov.gtas.job.scheduler.service.AdditionalProcessingService;
import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.model.Passenger;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.NotificatonService;
import gov.gtas.services.RuleHitPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Scope("prototype")
@Component
public abstract class RuleThread  implements Callable<Boolean> {


    @Autowired
    private AppConfigurationService appConfigurationService;

    @Value("${email.hit.notification.enabled}")
    private Boolean emailHitNotificationEnabled;

    @Value("${additional.processing.enabled.rules}")
    private Boolean additionalProcessHit;

    @Autowired
    private NotificatonService notificationSerivce;


    private static final Logger logger = LoggerFactory.getLogger(RuleThread.class);


    protected void processHits(List<MessageStatus> messageStatusList, RuleHitPersistenceService persistenceService, List<Set<HitDetail>> batchedTargetingServiceResults,
                               AdditionalProcessingService additionalProcessingService) {
        int count = 1;
        Set<HitDetail> firstTimeHits = new HashSet<>();
        for (Set<HitDetail> hitDetailSet : batchedTargetingServiceResults) {
            try {
                logger.debug("Saving rule hit details results batch " + count + " of "
                        + batchedTargetingServiceResults.size() + "...");
                Iterable<HitDetail> hitDetailIterable = persistenceService.persistToDatabase(hitDetailSet);
                if (hitDetailIterable != null) {
                    hitDetailIterable.forEach(firstTimeHits::add);
                }
            } catch (Exception ignored) {
                messageStatusList
                        .forEach(m -> m.setMessageStatusEnum(MessageStatusEnum.PARTIAL_ANALYZE));
                logger.error("Failed to save rules summary count " + count + " with following stacktrace: ",
                        ignored);
            }
            count++;
        }

        if (!firstTimeHits.isEmpty()) {
            // Send hit notifications using AWS SNS topic
            Set<Passenger> passengersWithFirstTimeHits = firstTimeHits.stream().map(HitDetail::getPassenger)
                    .collect(Collectors.toSet());
            sendNotifications(passengersWithFirstTimeHits);
            if (additionalProcessHit) {
                Set<Long> mIds = messageStatusList.stream().map(MessageStatus::getMessageId).collect(Collectors.toSet());
                additionalProcessingService.passengersAdditionalHits(firstTimeHits, mIds);
            }
        }
    }
    private void sendNotifications(Set<Passenger> passengersWithNewHits) {
        if (emailHitNotificationEnabled) {
            try {
                notificationSerivce.sendAutomatedHitEmailNotifications(passengersWithNewHits);
            } catch (IOException | TemplateException ignored) {
                //TODO: Add error handling. Do not propagate error up as partial matching still needs to happen.
                logger.error("There was an error within the email notification sender! ", ignored);
            }
        }

        boolean hitNotificationEnabled;
        try {
            hitNotificationEnabled = Boolean.parseBoolean(appConfigurationService
                    .findByOption(AppConfigurationRepository.ENABLE_INTERPOL_HIT_NOTIFICATION).getValue());

            if (hitNotificationEnabled) {
                long notificationStart = System.nanoTime();
                HitNotificationConfig hitNotificationConfig = generateSnsHitNotificationConfig(passengersWithNewHits);
                notificationSerivce.sendHitNotifications(hitNotificationConfig);
                logger.info("Hit Notification sent, it took {} m/s", (System.nanoTime() - notificationStart) / 1000000);
            }
        } catch (Exception e) {
            logger.warn(
                    "WATCHLIST HIT NOTIFICATION IS NOT CONFIGURED. SET NOTIFICATION IN DATABASE APP_CONFIGURATION TABLE.");
        }
    }
    private HitNotificationConfig generateSnsHitNotificationConfig(Set<Passenger> passengersWithNewHits) {
        String topicArn = appConfigurationService
                .findByOption(AppConfigurationRepository.INTERPOL_SNS_NOTIFICATION_ARN).getValue();
        String topicSubject = appConfigurationService
                .findByOption(AppConfigurationRepository.INTERPOL_SNS_NOTIFICATION_SUBJECT).getValue();
        // SET TO WATCH LIST CATEGORY ID FOR INTERPOL RED NOTICES
        Long interpolRedNoticesId = Long.parseLong(appConfigurationService
                .findByOption(AppConfigurationRepository.INTERPOL_WATCHLIST_ID).getValue());
        HitNotificationConfig hitNotificationConfig = new HitNotificationConfig(
                AmazonSNSClientBuilder.standard().build(), passengersWithNewHits, topicArn, topicSubject,
                interpolRedNoticesId);

        return hitNotificationConfig;
    }
}
