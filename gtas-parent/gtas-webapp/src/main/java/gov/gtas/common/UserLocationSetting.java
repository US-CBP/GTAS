package gov.gtas.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.gtas.constants.Constants;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.UserLocationService;
import gov.gtas.vo.passenger.UserLocationVo;

@Component
public class UserLocationSetting {

	private static final Logger logger = LoggerFactory.getLogger(UserLocationSetting.class);

	@Autowired
	private UserLocationService userLocationService;

	@Autowired
	private AppConfigurationService appConfigurationService;

	public UserLocationStatus setPrimaryLocation(HttpServletRequest httpServletRequest, String userId) {

		UserLocationStatus userLocationStatus = getPrimaryLocationStatus(httpServletRequest, userId);

		/*
		 * The location exists and is also a primary one is enabled in GTAS
		 * user_location table so just set the http session
		 */
		if (userLocationStatus.getPrimaryLocationAirport() != null && userLocationStatus.isPrimaryLocationCreated()
				&& userLocationStatus.isPrimaryLocationEnabledInDb()) {
			httpServletRequest.getSession().setAttribute(Constants.USER_PRIMARY_LOCATION,
					userLocationStatus.getPrimaryLocationAirport());
			logger.info(
					"So user primary location exists so the user Location is just set in the HTTP session successfull");
		}

		else if (userLocationStatus.getPrimaryLocationAirport() != null && userLocationStatus.isPrimaryLocationCreated()
				&& !userLocationStatus.isPrimaryLocationEnabledInDb()) {
			/*
			 * One or more location airports exist but none of them are marked primary in
			 * GTAS user_location table so mark one of them as primary location and set the
			 * http session
			 */
			try {
				userLocationService.updateUserPrimaryLocation(userId, userLocationStatus.getPrimaryLocationAirport(),
						true);
				logger.info("One of the user's location airports is set as a primary location in user_location table");
			} catch (Exception e) {
				logger.error("An error has occurred when updating user location in the database.");
			}

		} else if (userLocationStatus.getPrimaryLocationAirport() != null
				&& !userLocationStatus.isPrimaryLocationCreated()
				&& !userLocationStatus.isPrimaryLocationEnabledInDb()) {
			/*
			 * No location airports were found in user_location table so insert the
			 * dashboard airport in the user_location table, mark it as a primary location,
			 * and set the http session
			 */
			try {

				userLocationService.createUserPrimaryLocation(userId, userLocationStatus.getPrimaryLocationAirport(),
						true);
				logger.info("Primary User Location is created " + userLocationStatus.getPrimaryLocationAirport()
						+ " in GTAS database");
				httpServletRequest.getSession().setAttribute(Constants.USER_PRIMARY_LOCATION,
						userLocationStatus.getPrimaryLocationAirport());
				logger.info("User Primary Location is set to the default airport (from appConfig table): "
						+ userLocationStatus.getPrimaryLocationAirport() + " in session. " + this.getClass().getName());

			} catch (Exception e) {
				logger.error("An error has occurred when creating new user location in the database.");
			}

		} else if (userLocationStatus.getPrimaryLocationAirport() == null) {
			logger.error("ERROR! The user location could not be set correctly");
		}

		return userLocationStatus;
	}

	/*
	 * Get the user location and its status related to it existance in GTAS
	 * user_location table
	 * 
	 */
	public UserLocationStatus getPrimaryLocationStatus(HttpServletRequest httpServletRequest, String userId) {

		List<UserLocationVo> userLocationVoList = null;
		UserLocationStatus userLocationStatus = new UserLocationStatus();

		try {
			userLocationVoList = userLocationService.getUserLocation(userId);
		} catch (Exception e) {
			logger.error("An Error occurrend when reading user location in " + this.getClass().getName());
		}

		finally {

			/*
			 * Case 1: The user has one or more airport locations and one of them is marked
			 * as primary in GTAS user_location_table
			 */
			if (userLocationVoList != null && !userLocationVoList.isEmpty()) {

				/*
				 * Case 1.1 The user has a primary location Airport 2. The primary location is
				 * enabled in user location table 3. The primary location airport record is
				 * already created in GTAS user_location table
				 */

				for (UserLocationVo userLocationVo : userLocationVoList) {

					if (userLocationVo.isPrimaryLocation()) {
						userLocationStatus.setPrimaryLocationAirport(userLocationVo.getAirport());
						userLocationStatus.setPrimaryLocationEnabledInDb(true);
						userLocationStatus.setPrimaryLocationCreated(true);
						break;
					}

				}

				/*
				 * Case 1.2 The user has one or more locations but a primary location is not
				 * enabled in the user_location table
				 */

				if (!userLocationStatus.isPrimaryLocationEnabledInDb()) {

					for (int i = 0; i < userLocationVoList.size(); i++) {
						if (userLocationVoList.get(i).getAirport() != null
								&& !userLocationVoList.get(i).getAirport().isEmpty()) {

							userLocationStatus.setPrimaryLocationAirport(userLocationVoList.get(i).getAirport());
							userLocationStatus.setPrimaryLocationEnabledInDb(false);
							userLocationStatus.setPrimaryLocationCreated(true);

						}
					}
				}

			} else {
				/*
				 * Case 2
				 * 
				 * The user does not have any location in the GTAS database so the
				 * DASHBOARD_AIRPORT will be the user's default airport
				 * 
				 */

				AppConfiguration appConfiguration = appConfigurationService.findByOption("DASHBOARD_AIRPORT");
				logger.info(
						"No user location so the DASHBOARD_AIRPORT will be the user default location. DASHBOARD_AIRPORT: "
								+ appConfiguration.getValue());

				userLocationStatus.setPrimaryLocationAirport(appConfiguration.getValue());
				userLocationStatus.setPrimaryLocationEnabledInDb(false);
				userLocationStatus.setPrimaryLocationCreated(false);

			}

		}

		return userLocationStatus;

	}

}
