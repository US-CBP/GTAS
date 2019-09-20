package gov.gtas.repository;

import gov.gtas.model.FlightPassenger;
import org.springframework.data.repository.CrudRepository;

public interface FlightPassengerRepository extends CrudRepository<FlightPassenger, Long> {
}
