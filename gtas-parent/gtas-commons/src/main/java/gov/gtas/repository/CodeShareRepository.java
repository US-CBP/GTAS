package gov.gtas.repository;

import gov.gtas.model.CodeShareFlight;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CodeShareRepository extends CrudRepository<CodeShareFlight, Long> {

    List<CodeShareFlight> findByMarketingFlightNumberAndFlightId(String fullMarketingFlightNumber, Long flightId);
}
