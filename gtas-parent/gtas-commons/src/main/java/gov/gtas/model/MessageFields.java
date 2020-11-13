package gov.gtas.model;

import java.util.Set;

// This covers the PNR and APIS messages having the same fields at a lower level of abstraction.
// GTAS evolved to use them both, but for backwards compatibility we are using an interface to bridge the gap
// until changing the database structure is prioritized.
public interface MessageFields {

    EdifactMessage getEdifactMessage();

    void setEdifactMessage(EdifactMessage edifactMessage);

    Set<Passenger> getPassengers();

    void setPassengers(Set<Passenger> passengers);

    Set<Flight> getFlights();

    void setFlights(Set<Flight> flights);


}
