package gov.gtas.controller;

import gov.gtas.services.NotificatonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.security.service.GtasSecurityUtils;

import javax.transaction.Transactional;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
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

}
