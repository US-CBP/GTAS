package gov.gtas.repository;

import gov.gtas.model.FlightHitsWatchlist;
import org.springframework.data.repository.CrudRepository;

public interface FlightHitsWatchlistRepository  extends
        CrudRepository<FlightHitsWatchlist, Long> {
}
