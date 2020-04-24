/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.services.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.dto.FlightsPageDto;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;

@RestController
public class FlightPassengerController {
	@Autowired
	private FlightService flightService;

	@Autowired
	private PassengerService paxService;

	@RequestMapping(value = "/flights", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody FlightsPageDto getAllFlights(@RequestParam(value = "request", required = false) String requestDTO) throws IOException {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PriorityVettingListRequest.DATE_FORMAT);
		final ObjectMapper objectMapper = new ObjectMapper().setDateFormat(simpleDateFormat);
		final FlightsRequestDto request = objectMapper.readValue(requestDTO, FlightsRequestDto.class);
		return flightService.findAll(request);
	}

	@RequestMapping(value = "/flights", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody FlightsPageDto getAllFlights(@RequestBody FlightsRequestDto request) {

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
