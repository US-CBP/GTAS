package gov.gtas.job.scheduler;

import gov.gtas.model.Passenger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultShareConstraint implements GTASShareConstraint{

    private Set<Passenger> whiteListPassengers = new HashSet<>();
    private Set<Long> whiteListPassengersId = new HashSet<>();


    public DefaultShareConstraint() {}

    @Override
    public void createFilter(List<Passenger> passengerList) {
        for (Passenger p : passengerList) {
            if (!p.getHitDetails().isEmpty()) {
                whiteListPassengers.add(p);
            }
        }
        whiteListPassengersId = whiteListPassengers.stream().map(Passenger::getId).collect(Collectors.toSet());
    }

    @Override
    public void createFilter(Set<Passenger> passengerList) {
        for (Passenger p : passengerList) {
            if (!p.getHitDetails().isEmpty()) {
                whiteListPassengers.add(p);
            }
        }
        whiteListPassengersId = whiteListPassengers.stream().map(Passenger::getId).collect(Collectors.toSet());
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
