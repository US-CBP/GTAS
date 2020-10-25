package gov.gtas.services;

import gov.gtas.model.HitDetail;
import gov.gtas.summary.MessageSummaryList;
import gov.gtas.summary.SummaryMetaData;

import java.util.Set;

public interface AdditionalProcessingService {
    void passengersAdditionalHits(Set<HitDetail> passengerList, Set<Long> messageIds);
    MessageSummaryList listFromPassenger(String rtfl, String note, String noteCategory,Long passengerId, boolean sendRaw);
    void sendMessage(MessageSummaryList msl, SummaryMetaData smd);
}
