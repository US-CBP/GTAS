/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */

package gov.gtas.controller;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.common.UserLocationSetting;
import gov.gtas.common.UserLocationStatus;
import gov.gtas.constants.Constants;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.FlightSearchVo;

@RestController
public class UtilController {

	private static final Logger logger = LoggerFactory.getLogger(UtilController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserLocationSetting userLocationSetting;

	@RequestMapping(value = "/flightdirectionlist", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody FlightSearchVo getFlightDirections(HttpServletRequest httpServletRequest) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		return getFlightSearchVo(httpServletRequest, userId);
	}

	FlightSearchVo getFlightSearchVo(HttpServletRequest httpServletRequest, String userId) {
		FlightSearchVo flightSearchVo = new FlightSearchVo();
		try {
			boolean isAdmin = userService.isAdminUser(userId);
			if (isAdmin) {
				flightSearchVo.setAdminUser(true);
				flightSearchVo.getFlightDirectionList().add(flightSearchVo.new FlightDirectionVo("A", "Any"));
				flightSearchVo.getFlightDirectionList().add(flightSearchVo.new FlightDirectionVo("I", "Inbound"));
				flightSearchVo.getFlightDirectionList().add(flightSearchVo.new FlightDirectionVo("O", "Outbound"));
			} else {
				flightSearchVo.setAdminUser(false);
				flightSearchVo.getFlightDirectionList().add(flightSearchVo.new FlightDirectionVo("I", "Inbound"));
				flightSearchVo.getFlightDirectionList().add(flightSearchVo.new FlightDirectionVo("O", "Outbound"));

				String userLocationAirport;
				Object userLocationObject = httpServletRequest.getSession()
						.getAttribute(Constants.USER_PRIMARY_LOCATION);
				if (userLocationObject != null) {
					userLocationAirport = userLocationObject.toString();
				} else {
					UserLocationStatus userLocationStatus = userLocationSetting.setPrimaryLocation(httpServletRequest,
							userId);
					userLocationAirport = userLocationStatus.getPrimaryLocationAirport();
				}
				flightSearchVo.setUserLocation(userLocationAirport);
			}
		} catch (Exception ignored) {
			logger.error("ERROR! An error has occurred when retrieving inbound list. ", ignored);
		}
		return flightSearchVo;
	}
}
