package gov.gtas.job.scheduler;

import gov.gtas.job.scheduler.service.DataRetentionService;
import gov.gtas.model.*;
import gov.gtas.repository.PnrRepository;
import gov.gtas.services.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Component
public class PnrDataDeletionThread extends DataSchedulerThread implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(PnrDataDeletionThread.class);

    private final PassengerService passengerService;

    private final DataRetentionService dataRetentionService;


    public PnrDataDeletionThread(PassengerService passengerService, DataRetentionService dataRetentionService, PnrRepository pnrRepository) {
        this.passengerService = passengerService;
        this.dataRetentionService = dataRetentionService;
    }

    @Override
    public Boolean call() {
        boolean success = true;
        try {
            long start = System.nanoTime();
            logger.info("Starting rule running scheduled task");
            if (getMessageStatuses().isEmpty()) {
                logger.debug("No messages to process, ending deletion process");
                return success;
            }
            MessageAndFlightIds messageAndFlightIds = getPnrMessageIdsAndFlightIds();
            Set<Passenger> passengers = passengerService.getFullPassengersFromMessageIds(
                    messageAndFlightIds.getMessageIds(),
                    messageAndFlightIds.getFlightIds());
            getDefaultShareConstraint().createFilter(passengers);
            logger.info("Fetched passengers in.........  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            Set<Long> passengerIds = passengers.stream().map(Passenger::getId).collect(Collectors.toSet());
            Set<Document> documents = passengerService.getPassengerDocuments(passengerIds, messageAndFlightIds.getFlightIds());
            DocumentDeletionResult documentDeletionResult = DocumentDeletionResult.processPnrPassengers(documents, getApisCutOffDate(), getPnrCutOffDate(), getDefaultShareConstraint());
            logger.info("document deletion in......");
            PassengerDeletionResult passengerDeletionResult = PassengerDeletionResult.processPnrPassengers(passengers, getApisCutOffDate(), getPnrCutOffDate(), getDefaultShareConstraint());
            logger.info("Processed passengers in.....  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            PnrFieldsToScrub pnrFieldsToScrub = dataRetentionService.scrubPnrs(messageAndFlightIds.getFlightIds(), messageAndFlightIds.getMessageIds(), getPnrCutOffDate(), getDefaultShareConstraint());
            logger.info("Scrubbed pnrs in....  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            dataRetentionService.deletePnrMessage(pnrFieldsToScrub, documentDeletionResult, passengerDeletionResult, getMessageStatuses());
            logger.info("Total rule running data deleting task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
        } catch (Exception e) {
            logger.error("", e);
            getMessageStatuses().forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.PNR_DATA_DELETED));
            dataRetentionService.saveMessageStatus(getMessageStatuses());
            success = false;
        }
        return success;
    }


}