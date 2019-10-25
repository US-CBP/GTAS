package gov.gtas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.GtasEmailService;

@RestController
public class GtasEmailServiceController {

	@Autowired
	private GtasEmailService emailService;
	
	@Autowired
	UserRepository userRepository;

	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public void sendEmail(@RequestParam String [] to, @RequestParam String paxId, @RequestParam String note, @RequestParam String hitViewStatus) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		User loggedInUser = userRepository.userAndGroups(userId).orElseThrow(RuntimeException::new);
		emailService.send(to, Long.parseLong(paxId), note, hitViewStatus, loggedInUser);

	}

}
