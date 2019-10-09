package gov.gtas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.services.GtasEmailService;
import gov.gtas.services.dto.EmailDTO;

@RestController
public class GtasEmailServiceController {
	
	@Autowired
	private GtasEmailService emailService;
	
	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public void sendEmail(@RequestBody EmailDTO request) {
		emailService.send(request);
		
	}

}
