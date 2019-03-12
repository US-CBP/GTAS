package gov.gtas.repository;

import gov.gtas.model.PassengerTripDetails;
import org.springframework.data.repository.CrudRepository;

public interface PassengerTripRepository extends CrudRepository<PassengerTripDetails, Long> {
}
