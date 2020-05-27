package gov.gtas.job.scheduler;

import gov.gtas.model.Passenger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RetainNothingShareConstraint implements GTASShareConstraint {
    private Set<Passenger> whiteListPassengers = new HashSet<>();
    private Set<Long> whiteListPassengersId = new HashSet<>();

    @Override
    public void createFilter(List<Passenger> passengerList) {
        makeWhiteLists(passengerList);
    }

    @Override
    public void createFilter(Set<Passenger> passengerList) {
        makeWhiteLists(passengerList);
    }

    private void makeWhiteLists(Collection<Passenger> passengerList) {
    }

    @Override
    public Set<Passenger> getWhitelistedPassengers() {
        return whiteListPassengers;
    }

    @Override
    public Set<Long> getWhiteListedPassenerIds() {
        return whiteListPassengersId;
    }
}
