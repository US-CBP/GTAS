package gov.gtas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.SignupLocation;

public interface SignupLocationRepository extends CrudRepository<SignupLocation, Long> {

	@Query("select u from SignupLocation u where u.active = 1")
	List<SignupLocation> findAllActiveSignupLocations();
	
	@Query("select l from SignupLocation l where l.name = :name")
	SignupLocation findLocationByName(@Param("name") String name);
}
