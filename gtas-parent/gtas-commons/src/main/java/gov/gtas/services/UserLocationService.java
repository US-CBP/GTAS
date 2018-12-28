package gov.gtas.services;

import java.util.List;

import gov.gtas.model.UserLocation;
import gov.gtas.vo.passenger.UserLocationVo;

/**
 * An interface to for access UserLocation information
 *
 */
public interface UserLocationService {

	public List<UserLocationVo> getUserLocation(String userId) throws Exception;

	public boolean updateUserPrimaryLocation(String userId, String location, Boolean flag) throws Exception;

	public Boolean createUserPrimaryLocation(String userId, String location, Boolean flag) throws Exception;

}
