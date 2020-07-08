package gov.gtas.job.scheduler;

import gov.gtas.model.Passenger;

import java.util.List;
import java.util.Set;

public interface GTASShareConstraint {
    void createFilter(List<Passenger> passengerList);
    void createFilter(Set<Passenger> passengerList);
    Set<Passenger> getWhitelistedPassengers();
    Set<Long> getWhiteListedPassenerIds();
}
