package gov.gtas.job.scheduler.service;

import gov.gtas.job.scheduler.*;
import gov.gtas.model.DataRetentionStatus;
import gov.gtas.model.MessageStatus;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface DataRetentionService {
    List<MessageStatus> maskApisMessage(List<MessageStatus> messageStatuses);
    List<MessageStatus> deleteApisMessage(List<MessageStatus> messageStatuses);
    void saveDataRetentionStatus(Set<DataRetentionStatus> drsSet);
    void saveApisFields(NoteDeletionResult noteDeletionResult, DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult);
    void savePnrFields(DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, PnrFieldsToScrub pnrFieldsToScrub);
    void saveMessageStatus(List<MessageStatus> messageStatuses);
    PnrFieldsToScrub scrubPnrs(Set<Long> flightIds, Set<Long> messageIds, Date pnrCutOffDate, GTASShareConstraint gtasShareConstraint);

    void deletePnrMessage(NoteDeletionResult noteDeletionResult, PnrFieldsToScrub pnrFieldsToScrub, DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, List<MessageStatus> messageStatuses);
    void deleteApisMessage(NoteDeletionResult noteDeletionResult, DocumentDeletionResult documentDeletionResult, PassengerDeletionResult passengerDeletionResult, List<MessageStatus> messageStatuses);
}
