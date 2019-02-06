package gov.gtas.repository;

import gov.gtas.model.FlightHitsRule;
import org.springframework.data.repository.CrudRepository;

public interface FlightHitsRuleRepository extends
        CrudRepository<FlightHitsRule, Long> {
}
