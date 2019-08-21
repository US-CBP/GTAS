/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import gov.gtas.common.UserLocationStatus;
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
		String userLocationAirport;
		UserLocationStatus  userLocationStatus = null;
		Set <String> defaultDestAirport = new HashSet<String>();

		if (!isAdmin )
		{
			Object userLocationObject = hsr.getSession().getAttribute(Constants.USER_PRIMARY_LOCATION);
			if(userLocationObject!=null)
			{
				userLocationAirport = userLocationObject.toString();
			}
			else
			{
				userLocationStatus = userLocationSetting.setPrimaryLocation(hsr, userId);
				userLocationAirport = userLocationStatus.getPrimaryLocationAirport();
			}
			
			if(request.getSearchSubmitFlag()==null && userLocationAirport!=null )
			{
				defaultDestAirport.add(userLocationAirport);
				request.setDestinationAirports(defaultDestAirport);
			}
			
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



}
