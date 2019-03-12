package gov.gtas.repository;

import gov.gtas.model.PassengerDetails;
import org.springframework.data.repository.CrudRepository;

public interface PassengerDetailRepository extends CrudRepository <PassengerDetails, Long> {
}
