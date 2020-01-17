package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.PhysicalLocation;

public interface PhysicalLocationRepository extends CrudRepository<PhysicalLocation, Long> {

	@Query("select u from PhysicalLocation u where u.active = 1")
	List<PhysicalLocation> findAllActivePhysicalLocation();
}
