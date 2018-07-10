package gov.gtas.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import gov.gtas.rest.model.Passenger;
import gov.gtas.rest.request.PassengerRequest;
import gov.gtas.rest.response.PassengerResponse;
import gov.gtas.rest.service.PassengerService;
import gov.gtas.rest.validator.PassengerValidator;
import gov.gtas.rest.validator.ValidationResult;

@RestController
@RequestMapping(path = "/gtas-web-service")
public class PassengerController {
	
	
	@Autowired
	private PassengerService passengerService;
	@Autowired
	private PassengerValidator passengerValidator;
	
	@RequestMapping(method = RequestMethod.POST, value = "/passenger", produces = MediaType.APPLICATION_JSON_VALUE)
	public PassengerResponse findPassenger(@RequestBody Passenger passenger)
	{
		
		PassengerRequest passengerRequest = new PassengerRequest();
		PassengerResponse passengerResponse = new PassengerResponse();
		ValidationResult validationResult = passengerValidator.validatePassenger(passenger);
		
		
		if(!validationResult.getHasValidationError())
		{
			passengerRequest.getModel().setFirstName(passenger.getFirstName());
			passengerRequest.getModel().setLastName(passenger.getLastName());
			passengerResponse = passengerService.findPassengerByName(passengerRequest);
		}	
		passengerResponse.setValidationResult(validationResult);
		return passengerResponse;
		
	}

}
