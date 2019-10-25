package gov.gtas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.services.GtasEmailService;

@RestController
public class GtasEmailServiceController {

	@Autowired
	private GtasEmailService emailService;

	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public void sendEmail(@RequestParam String [] to, @RequestParam String paxId, @RequestParam String note, @RequestParam String hitViewStatus) {
		emailService.send(to, Long.parseLong(paxId), note, hitViewStatus);

	}

}
