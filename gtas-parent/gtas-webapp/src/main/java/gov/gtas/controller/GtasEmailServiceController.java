package gov.gtas.controller;

import gov.gtas.email.dto.EmailNotificationDTO;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.NotificatonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@ConditionalOnProperty(prefix = "enable.email.notification", name = "enabled")
public class GtasEmailServiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GtasEmailServiceController.class);

	@Autowired
	private NotificatonService notificationService;

	@Transactional
	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public ResponseEntity<?> sendEmail(@RequestParam String [] to, @RequestParam Long paxId, @RequestParam String note) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		try {
			notificationService.sendManualNotificationEmail(to, note, paxId, userId);
			return new ResponseEntity<>(OK);
		} catch(Exception ex) {
			LOGGER.error(format("Manual email notification from UserId (%s) to failed with the exception: %s", userId, ex.toString()));
			return new ResponseEntity<>(BAD_REQUEST);
		}
	}
	
	@Transactional
	@PostMapping(value = "users/notify", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sendEmail(@RequestBody EmailNotificationDTO emialInfo) {
		
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		try {
			notificationService.sendManualNotificationEmail(emialInfo.getTo(), emialInfo.getNote(), emialInfo.getPaxId(), userId);
			return new ResponseEntity<>(OK);
		} catch(Exception ex) {
			LOGGER.error(format("Manual email notification from UserId (%s) to failed with the exception: %s", userId, ex.toString()));
			return new ResponseEntity<>(BAD_REQUEST);
		}
	}

}
