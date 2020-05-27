package gov.gtas.job.scheduler;

import gov.gtas.job.scheduler.service.DataRetentionService;
import gov.gtas.model.*;
import gov.gtas.repository.PnrRepository;
import gov.gtas.services.NoteTypeService;
import gov.gtas.services.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class PnrDataDeletionThread extends DataSchedulerThread implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(PnrDataDeletionThread.class);

    private final PassengerService passengerService;

    private final DataRetentionService dataRetentionService;

    private final NoteTypeService noteTypeService;


    public PnrDataDeletionThread(PassengerService passengerService, DataRetentionService dataRetentionService, PnrRepository pnrRepository, NoteTypeService noteTypeService) {
        this.passengerService = passengerService;
        this.dataRetentionService = dataRetentionService;
        this.noteTypeService = noteTypeService;
    }

    @Override
    public Boolean call() {
        boolean success = true;
        try {
            long start = System.nanoTime();
            if (getMessageStatuses().isEmpty()) {
                logger.debug("No messages to process, ending deletion process");
                return success;
            }
            MessageAndFlightIds messageAndFlightIds = getPnrMessageIdsAndFlightIds();
            Set<Passenger> passengers = passengerService.getFullPassengersFromMessageIds(
                    messageAndFlightIds.getMessageIds(),
                    messageAndFlightIds.getFlightIds());
            getDefaultShareConstraint().createFilter(passengers);
            logger.debug("Fetched passengers in.........  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            Set<Long> passengerIds = passengers.stream().map(Passenger::getId).collect(Collectors.toSet());
            Set<Document> documents = passengerService.getPassengerDocuments(passengerIds, messageAndFlightIds.getFlightIds());
            DocumentDeletionResult documentDeletionResult = DocumentDeletionResult.processPnrPassengers(documents, getApisCutOffDate(), getPnrCutOffDate(), getDefaultShareConstraint());
            logger.debug("document deletion in......");
            PassengerDeletionResult passengerDeletionResult = PassengerDeletionResult.processPnrPassengers(passengers, getApisCutOffDate(), getPnrCutOffDate(), getDefaultShareConstraint());
            NoteType noteType = noteTypeService.getDeletedNoteType();
            NoteDeletionResult noteDeletionResult = NoteDeletionResult.processPassengers(passengers, getApisCutOffDate(), getPnrCutOffDate(), getDefaultShareConstraint(), noteType);
            logger.debug("Processed passengers in.....  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            PnrFieldsToScrub pnrFieldsToScrub = dataRetentionService.scrubPnrs(messageAndFlightIds.getFlightIds(), messageAndFlightIds.getMessageIds(), getPnrCutOffDate(), getDefaultShareConstraint());
            logger.debug("Scrubbed pnrs in....  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            dataRetentionService.deletePnrMessage(noteDeletionResult, pnrFieldsToScrub, documentDeletionResult, passengerDeletionResult, getMessageStatuses());
            logger.debug("Total rule running data deleting task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");

        } catch (Exception e) {
            getMessageStatuses().forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.PNR_DELETE_ERROR));
            dataRetentionService.saveMessageStatus(getMessageStatuses());
            logger.error("", e);
            success = false;
        }
        return success;
    }
}