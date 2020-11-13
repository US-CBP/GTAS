package gov.gtas.job.scheduler.service;

import gov.gtas.model.HitDetail;

import java.util.Set;

public interface AdditionalProcessingService {
    void passengersAdditionalHits(Set<HitDetail> passengerList, Set<Long> messageIds);
}
