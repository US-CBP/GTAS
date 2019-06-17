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

	public void setPrimaryLocation(HttpServletRequest httpServletRequest, String userId) {
		List<UserLocationVo> userLocationVoList = null;

		try {
			userLocationVoList = userLocationService.getUserLocation(userId);
		} catch (Exception e) {
			logger.error("An Error occurrend when reading user location in " + this.getClass().getName());
		}

		finally {

			boolean hasPrimaryLocation = false;

			if (userLocationVoList != null && !userLocationVoList.isEmpty()) {

				for (UserLocationVo userLocationVo : userLocationVoList) {

					if (userLocationVo.isPrimaryLocation()) {
						httpServletRequest.getSession().setAttribute(Constants.USER_PRIMARY_LOCATION,
								userLocationVo.getAirport());
						hasPrimaryLocation = true;
						logger.info("User Primary Location is set to " + userLocationVo.getAirport() + " in session. "
								+ this.getClass().getName());
						break;
					}

				}

				// if the user has a location but does not have a primary location, then set the
				// first one as a primary location
				if (!hasPrimaryLocation) {

					for (int i = 0; i < userLocationVoList.size(); i++) {
						if (userLocationVoList.get(i).getAirport() != null
								&& !userLocationVoList.get(i).getAirport().isEmpty()) {
							userLocationVoList.get(i).setPrimaryLocation(true);
							httpServletRequest.getSession().setAttribute(Constants.USER_PRIMARY_LOCATION,
									userLocationVoList.get(i).getAirport());
							logger.info("User Primary Location is set to " + userLocationVoList.get(i).getAirport()
									+ " in session. " + this.getClass().getName());
							try {
								userLocationService.updateUserPrimaryLocation(userId,
										userLocationVoList.get(i).getAirport(), true);
							} catch (Exception e) {
								logger.error("An error has occurred when updating user location in the database.");
							}

						}
					}
				}

			} else {

				// if the user does not have a location, then use the default from app config
				// table and make it the primary location.
				logger.info("No user location Reading from the AppConfig table...: ");
				AppConfiguration appConfiguration = appConfigurationService.findByOption("DASHBOARD_AIRPORT");
				logger.info(
						"No user location so the DASHBOARD_AIRPORT will be the user default location. DASHBOARD_AIRPORT: "
								+ appConfiguration.getValue());

				try {

					userLocationService.createUserPrimaryLocation(userId, appConfiguration.getValue(), true);
					logger.info("Primary User Location is set successfully to  " + appConfiguration.getValue()
							+ " in GTAS database");
					httpServletRequest.getSession().setAttribute(Constants.USER_PRIMARY_LOCATION,
							appConfiguration.getValue());
					logger.info("User Primary Location is set to the default airport (from appConfig table): "
							+ appConfiguration.getValue() + " in session. " + this.getClass().getName());

				} catch (Exception e) {
					logger.error("An error has occurred when creating new user location in the database.");
				}
			}

		}

	}

}
