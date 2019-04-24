package gov.gtas.job.scheduler;

import gov.gtas.model.*;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.MessageStatusRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.svc.GraphRulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GraphRulesScheduler {

    private Logger logger = LoggerFactory.getLogger(GraphRulesScheduler.class);

    private final GraphRulesService graphRulesService;

    private final AppConfigurationService appConfigurationService;

    private final MessageStatusRepository messageStatusRepository;

    private final PassengerRepository passengerRepository;


    public GraphRulesScheduler(
            GraphRulesService graphRulesService,
            AppConfigurationService appConfigurationService,
            MessageStatusRepository messageStatusRepository,
            PassengerRepository passengerRepository) {
        this.graphRulesService = graphRulesService;
        this.appConfigurationService = appConfigurationService;
        this.messageStatusRepository = messageStatusRepository;
        this.passengerRepository = passengerRepository;
    }


    @Scheduled(fixedDelayString = "${ruleRunner.fixedDelay.in.milliseconds}", initialDelayString = "${ruleRunner.initialDelay.in.milliseconds}")
    public void jobScheduling() {
        int messageLimit = Integer.parseInt(appConfigurationService.findByOption(AppConfigurationRepository.MAX_MESSAGES_PER_RULE_RUN).getValue());
        List<MessageStatus> source =
                messageStatusRepository
                        .getMessagesFromStatus(
                                MessageStatusEnum.LOADED_IN_NEO_4_J.getName(), messageLimit);
        if (source.isEmpty()) {
            return;
        }

        List<MessageStatus> procssedMessages = new ArrayList<>();
        int maxPassengers = Integer.parseInt(appConfigurationService.findByOption(AppConfigurationRepository.MAX_PASSENGERS_PER_RULE_RUN).getValue());
        int runningTotal = 0;
        for (MessageStatus ms : source) {
            Message message = ms.getMessage();
            procssedMessages.add(ms);
            if (message.getPassengerCount() != null) {
                runningTotal += message.getPassengerCount();
            }
            if (runningTotal >= maxPassengers) {
                break;
            }
        }

        try {
            Set<Long> messageId = procssedMessages.stream()
                    .map(MessageStatus::getMessageId)
                    .collect(Collectors.toSet());

            Set<Passenger> passengers = passengerRepository.getPassengerByMessageId(messageId);
            Set<GraphHitDetail> graphHitDetailSet = graphRulesService.graphResults(passengers);
            graphRulesService.saveResults(graphHitDetailSet);
            procssedMessages.forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.ANALYZED_BY_NEO_4_J));
        } catch (Exception e) {
            logger.warn("Exception running neo4j rules! ", e);
            procssedMessages.forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.FAILED_NEO_4_J));
        } finally {
            messageStatusRepository.saveAll(procssedMessages);
        }
    }
}
