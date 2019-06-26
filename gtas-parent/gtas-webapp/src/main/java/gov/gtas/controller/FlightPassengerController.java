/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.common.UserLocationSetting;
import gov.gtas.constants.Constants;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.DataManagementService;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.UserLocationService;
import gov.gtas.services.dto.FlightsPageDto;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.UserLocationVo;

@RestController
public class FlightPassengerController {
	@Autowired
	private FlightService flightService;

	@Autowired
	private PassengerService paxService;

	@Autowired
	private DataManagementService dataManagementService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserLocationSetting userLocationSetting;

	private static final Logger logger = LoggerFactory.getLogger(FlightPassengerController.class);

	@RequestMapping(value = "/flights", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody FlightsPageDto getAllFlights(@RequestBody FlightsRequestDto request, HttpServletRequest hsr) {
		hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		boolean isAdmin = userService.isAdminUser(userId);

		if (!isAdmin && hsr.getSession().getAttribute(Constants.USER_PRIMARY_LOCATION) == null)
		{
			userLocationSetting.setPrimaryLocation(hsr, userId);
		}
		

		return flightService.findAll(request);
	}

	@RequestMapping(value = "/flights/flight/{id}/passengers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PassengersPageDto getFlightPassengers(@PathVariable(value = "id") Long flightId,
			@RequestBody PassengersRequestDto request, HttpServletRequest hsr) {
		SecurityContextHolder.setContext((SecurityContext) hsr.getSession().getAttribute("SPRING_SECURITY_CONTEXT"));
		return paxService.getPassengersByCriteria(flightId, request);
	}

	@RequestMapping(value = "/passengers", method = RequestMethod.POST)
	public @ResponseBody PassengersPageDto getAllPassengers(@RequestBody PassengersRequestDto request,
			HttpServletRequest hsr) {
		hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		return paxService.getPassengersByCriteria(null, request);
	}

	/*private void setUserPrimaryLocation(HttpServletRequest httpServletRequest, String userId) {
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
				logger.info("No user location so the DASHBOARD_AIRPORT will be the user default location. DASHBOARD_AIRPORT: "
								+ appConfiguration.getValue());

				try {

					userLocationService.createUserPrimaryLocation(userId, appConfiguration.getValue(), true);
					logger.info("Primary User Location is set successfully to  "+ appConfiguration.getValue() + " in GTAS database");
					httpServletRequest.getSession().setAttribute(Constants.USER_PRIMARY_LOCATION,
							appConfiguration.getValue());
					logger.info("User Primary Location is set to the default airport (from appConfig table): " 
							+ appConfiguration.getValue() + " in session. " + this.getClass().getName());

				} catch (Exception e) {
					logger.error("An error has occurred when creating new user location in the database.");
				}
			}

		}

	}*/

}
