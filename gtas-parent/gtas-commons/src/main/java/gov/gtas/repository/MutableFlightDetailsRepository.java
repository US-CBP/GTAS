package gov.gtas.repository;

import gov.gtas.model.MutableFlightDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MutableFlightDetailsRepository extends JpaRepository<MutableFlightDetails, Long> {
}
