package gov.gtas.model;

import java.util.Set;

public interface MessageFields {

    EdifactMessage getEdifactMessage();

    void setEdifactMessage(EdifactMessage edifactMessage);

    Set<Passenger> getPassengers();

    void setPassengers(Set<Passenger> passengers);

    Set<Flight> getFlights();

    void setFlights(Set<Flight> flights);


}
