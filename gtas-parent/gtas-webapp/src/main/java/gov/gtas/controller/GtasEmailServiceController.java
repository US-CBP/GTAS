package gov.gtas.controller;

import freemarker.template.TemplateException;
import gov.gtas.services.NotificatonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.security.service.GtasSecurityUtils;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import java.io.IOException;

@RestController
public class GtasEmailServiceController {

	@Autowired
	private NotificatonService notificationService;

	@Transactional
	@RequestMapping(value = "/notify", method = RequestMethod.POST)
	public void sendEmail(@RequestParam String [] to, @RequestParam Long paxId, @RequestParam String note) throws IOException, TemplateException, MessagingException {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		notificationService.sendManualNotificationEmail(to, note, paxId, userId);
	}

}
