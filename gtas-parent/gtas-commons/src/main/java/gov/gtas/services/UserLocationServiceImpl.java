package gov.gtas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gov.gtas.model.UserLocation;
import gov.gtas.repository.UserLocationRepository;
import gov.gtas.vo.passenger.UserLocationVo;

@Service
public class UserLocationServiceImpl implements UserLocationService {

	private static final Logger logger = LoggerFactory.getLogger(UserLocationServiceImpl.class);

	@Resource
	private UserLocationRepository userLocationRepository;

	@Override
	public List<UserLocationVo> getUserLocation(String userId) throws Exception {

		logger.debug("Getting user location for for user: " + userId + " " + this.getClass().getName());

		List<UserLocationVo> userLocationVoList = new ArrayList<UserLocationVo>();

		try {

			Set<UserLocation> userLocationSet = userLocationRepository.getUserLocationByUserId(userId);

			if (userLocationSet != null) {

				for (UserLocation userLocation : userLocationSet) {
					UserLocationVo userLocationVo = new UserLocationVo();

					if (userLocation != null) {
						userLocationVo.setUserId(userLocation.getUserId());
						userLocationVo.setAirport(userLocation.getAirport());
						if (userLocation != null && userLocation.getPrimaryLocation() != null)
							userLocationVo.setPrimaryLocation(userLocation.getPrimaryLocation().booleanValue());
					}

					userLocationVoList.add(userLocationVo);
				}

			}
		} catch (Exception e) {
			logger.error("An Exception has occurred when reading user location.");
			throw (e);
		}

		return userLocationVoList;
	}

	@Override
	public boolean updateUserPrimaryLocation(String userId, String location, Boolean flag) throws Exception {

		logger.debug(
				"Updating user location for for user: " + userId + " " + location + "  " + this.getClass().getName());

		boolean isPrimaryLocationUpdated = false;

		try {

			Set<UserLocation> userLocationSet = userLocationRepository.getUserLocationByUserId(userId);

			if (userLocationSet != null) {
				for (UserLocation userLocation : userLocationSet) {
					if (userLocation.getPrimaryLocation() != null)
						userLocationRepository.updateUserPrimaryLocation(userId, userLocation.getAirport(), null);
				}
			}
			userLocationRepository.updateUserPrimaryLocation(userId, location, flag);
			isPrimaryLocationUpdated = true;
			logger.info("Updated user primary location for for user: " + userId + " " + location + " successfully.");
		} catch (Exception e) {
			logger.error("An Exception has occurred when updating user primary location to " + location);
			throw (e);
		}

		return isPrimaryLocationUpdated;
	}

	public Boolean createUserPrimaryLocation(String userId, String location, Boolean flag) throws Exception {

		logger.debug("Inserting user primary location for user: " + userId + " " + location + " with flag " + flag + " "
				+ this.getClass().getName());

		boolean isPrimaryLocationInserted = false;

		try {
			UserLocation userLocation = new UserLocation();
			userLocation.setUserId(userId);
			userLocation.setAirport(location);
			userLocation.setPrimaryLocation(flag);
			userLocationRepository.save(userLocation);
			isPrimaryLocationInserted = true;
			logger.info("Inserted user primary location for for user: " + userId + " " + location + " successfully.");
		} catch (Exception e) {
			logger.error("An Exception has occurred when inserting user primary location to " + location);
			throw (e);
		}

		return isPrimaryLocationInserted;
	}

}
