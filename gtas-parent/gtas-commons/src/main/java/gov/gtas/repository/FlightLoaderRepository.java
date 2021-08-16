package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.FlightLoader;

public interface FlightLoaderRepository  extends CrudRepository<FlightLoader, Long> {
	
	@Query("select fl from FlightLoader fl where fl.idTag = :qidTag")
	public FlightLoader findByidTag(@Param("qidTag") String queryIdTag);
}
