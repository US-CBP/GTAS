package gov.gtas.repository;

import gov.gtas.model.PassengerWLTimestamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerWatchlistRepository extends JpaRepository<PassengerWLTimestamp, Long>  {
}
