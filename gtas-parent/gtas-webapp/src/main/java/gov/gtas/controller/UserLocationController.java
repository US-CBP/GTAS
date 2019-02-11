package gov.gtas.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.constants.Constants;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.UserLocationService;
import gov.gtas.vo.passenger.UserLocationVo;

@RestController
public class UserLocationController {

	private static final Logger logger = LoggerFactory.getLogger(UserLocationController.class);

	@Autowired
	private UserLocationService userLocationService;

	@RequestMapping(value = "/getAllUserlocations", method = RequestMethod.GET)
	public @ResponseBody List<UserLocationVo> getAllUserlocations() {

		List<UserLocationVo> userLocationVoList = null;
		String userId = GtasSecurityUtils.fetchLoggedInUserId();

		try {
			userLocationVoList = userLocationService.getUserLocation(userId);
		} catch (Exception e) {
			logger.error("An Error occurrend when reading user location in " + this.getClass().getName());
		}

		return userLocationVoList;
	}

	@RequestMapping(value = "/saveUserLocation", method = RequestMethod.POST)
	public @ResponseBody boolean saveUserLocation(@RequestBody UserLocationVo userLocationVo,
			HttpServletRequest httpServletRequest) {

		boolean confirmation = false;
		String userId = GtasSecurityUtils.fetchLoggedInUserId();

		if (userLocationVo.getAirport() == null || userLocationVo.getAirport().trim().isEmpty()) {
			logger.error("Error! The value for office location value is null. " + this.getClass().getName());
			return confirmation;
		}

		try {

			httpServletRequest.getSession().setAttribute(Constants.USER_PRIMARY_LOCATION, userLocationVo.getAirport());
			logger.info("The office location is updated successfully to " + userLocationVo.getAirport()
					+ " in this session.");

			boolean result = userLocationService.updateUserPrimaryLocation(userId, userLocationVo.getAirport(), true);

			if (result) {
				confirmation = true;
				logger.info("The office location is updated successfully to " + userLocationVo.getAirport()
						+ " in GTAS database.");
			} else {
				confirmation = false;
				logger.error("Error! The office location " + userLocationVo.getAirport()
						+ "could not be saved in GTAS database" + this.getClass().getName());
			}

		} catch (Exception e) {
			logger.error("An Error occurrend when saving user location in " + this.getClass().getName());
		}

		return confirmation;
	}

}
