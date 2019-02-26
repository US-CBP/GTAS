/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.services.DataManagementService;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.dto.FlightsPageDto;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightPassengerController {
    @Autowired
    private FlightService flightService;

    @Autowired
    private PassengerService paxService;
    
    @Autowired
    private DataManagementService dataManagementService;

    @RequestMapping(value = "/flights", method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody FlightsPageDto getAllFlights(@RequestBody FlightsRequestDto request, HttpServletRequest hsr) {
        hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext());
        
    	return flightService.findAll(request);
    }

    @RequestMapping(value = "/flights/flight/{id}/passengers", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody PassengersPageDto getFlightPassengers(@PathVariable(value = "id") Long flightId, @RequestBody PassengersRequestDto request, HttpServletRequest hsr) {
        SecurityContextHolder.setContext((SecurityContext) hsr.getSession()
                .getAttribute("SPRING_SECURITY_CONTEXT"));
    	return paxService.getPassengersByCriteria(flightId, request);
    }

    @RequestMapping(value = "/passengers", method = RequestMethod.POST)
    public @ResponseBody PassengersPageDto getAllPassengers(@RequestBody PassengersRequestDto request, HttpServletRequest hsr) {
        hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext());
    	return paxService.getPassengersByCriteria(null, request);
    }
    
    @RequestMapping(value = "/flights/seats/{flightId}", method = RequestMethod.GET)
    public @ResponseBody java.util.List<String> getSeatsByFlightId(@PathVariable(value = "flightId") Long flightId, HttpServletRequest hsr) {
        hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext());
    	return flightService.getSeatsByFlightId(flightId);
    }
}
