package gov.gtas.job.scheduler;

import gov.gtas.job.scheduler.service.DataRetentionService;
import gov.gtas.model.*;
import gov.gtas.repository.DocumentRepository;
import gov.gtas.repository.DocumentRetentionPolicyAuditRepository;
import gov.gtas.repository.PassengerDetailRepository;
import gov.gtas.repository.PassengerDetailRetentionPolicyAuditRepository;
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
public class ApisDataDeletionThread  extends DataSchedulerThread implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(ApisDataDeletionThread.class);

    private final PassengerService passengerService;

    private final DataRetentionService dataRetentionService;

    public ApisDataDeletionThread(PassengerService passengerService, DataRetentionService dataRetentionService, DocumentRepository documentRepository, DocumentRetentionPolicyAuditRepository documentRetentionPolicyAuditRepository, PassengerDetailRepository passengerDetailRepository, PassengerDetailRetentionPolicyAuditRepository passengerDetailRetentionPolicyAuditRepository) {
        this.passengerService = passengerService;
        this.dataRetentionService = dataRetentionService;

    }

    @Override
    public Boolean call() {
        boolean success = true;
        try {
            long start = System.nanoTime();
            logger.debug("Starting rule running scheduled task");
            if (getMessageStatuses().isEmpty()) {
                logger.debug("No messages to process, ending deletion process");
                return success;
            }
            MessageAndFlightIds messageAndFlightIds = getApisMessageIdsAndFlightIds();
            Set<Passenger> passengers = passengerService.getFullPassengersFromMessageIds(messageAndFlightIds.getMessageIds(), messageAndFlightIds.getFlightIds());

            getDefaultShareConstraint().createFilter(passengers);

            logger.info("Processed passengers in.....  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            PassengerDeletionResult passengerDeletionResult = PassengerDeletionResult.processApisPassengers(passengers, getApisCutOffDate(), getPnrCutOffDate(), getDefaultShareConstraint());
            Set<Long> passengerIds = passengers.stream().map(Passenger::getId).collect(Collectors.toSet());
            Set<Document> documents = passengerService.getPassengerDocuments(passengerIds, messageAndFlightIds.getFlightIds());
            DocumentDeletionResult documentDeletionResult = DocumentDeletionResult.processApisPassengers(documents, getApisCutOffDate(), getPnrCutOffDate(), getDefaultShareConstraint());
            logger.info("Processed documents in.....  " + (System.nanoTime() - start) / 1000000 + "m/s.");
            dataRetentionService.deleteApisMessage(documentDeletionResult, passengerDeletionResult, getMessageStatuses());
            logger.info("Total rule running data deleting task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
        } catch (Exception e) {
            getMessageStatuses().forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.APIS_DELETE_ERROR));
            dataRetentionService.saveMessageStatus(getMessageStatuses());
            logger.error("", e);
            success = false;
        }
        return success;
    }
}
