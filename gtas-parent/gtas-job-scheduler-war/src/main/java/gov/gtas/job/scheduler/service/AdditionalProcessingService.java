package gov.gtas.job.scheduler.service;

import gov.gtas.model.Passenger;

import java.util.Set;

public interface AdditionalProcessingService {
    void passengersAdditionalHits(Set<Passenger> passengerList, Set<Long> messageIds);
}
