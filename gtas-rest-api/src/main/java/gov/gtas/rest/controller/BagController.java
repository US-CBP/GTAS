package gov.gtas.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.rest.model.Bag;
import gov.gtas.rest.model.Passenger;
import gov.gtas.rest.request.BagRequest;
import gov.gtas.rest.request.PassengerRequest;
import gov.gtas.rest.response.BagResponse;
import gov.gtas.rest.response.PassengerResponse;
import gov.gtas.rest.service.BagService;
import gov.gtas.rest.service.PassengerService;
import gov.gtas.rest.validator.BagValidator;
import gov.gtas.rest.validator.ValidationResult;

@RestController
@RequestMapping(path = "/gtas-web-service")
public class BagController {
	
	
	@Autowired
	private BagService bagService;
	
	@Autowired
	private BagValidator bagValidator;
	
	@RequestMapping(method = RequestMethod.POST, value = "/bag", produces = MediaType.APPLICATION_JSON_VALUE)
	public BagResponse findPassenger(@RequestBody Bag bag)
	{
		
		BagRequest bagRequest = new BagRequest();
		BagResponse bagResponse = new BagResponse();
		ValidationResult validationResult =bagValidator.validateEvent(bag);
		
		
		if(validationResult.getHasValidationError())
		{
			bagResponse.setValidationResult(validationResult);
			return bagResponse;
		}
		else
		{
			
			bagRequest.getModel().setFlightId(bag.getFlightId());
			bagRequest.getModel().setPassengerId(bag.getPassengerId());
			bagResponse = bagService.findBagByFlightIdPassengerId(bagRequest);
		}
				

		return bagResponse;
		
	}

}
