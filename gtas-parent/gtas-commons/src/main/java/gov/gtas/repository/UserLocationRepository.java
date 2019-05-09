package gov.gtas.repository;

import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.UserLocation;

public interface UserLocationRepository extends CrudRepository<UserLocation, String> {
	
	
	@Query("SELECT u FROM UserLocation u  WHERE u.userId = (:userId)")
    public Set<UserLocation> getUserLocationByUserId(@Param("userId") String userId);
	
	@Modifying
	@Transactional
	@Query("update UserLocation u set u.primaryLocation = :flag where u.userId = :userId AND u.airport = :location")
	public Integer updateUserPrimaryLocation(@Param("userId") String userId, @Param("location") String location, @Param("flag") Boolean flag );
	
	


}
