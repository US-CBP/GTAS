package gov.gtas.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import gov.gtas.services.dto.EmailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class GtasEmailService {
    private static Logger logger = LoggerFactory.getLogger(GtasEmailService.class);

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String from;

	@Value("${path-to-attachment}")
	private String pathToAttachment;

	@Value("${login.page.url}")
	private String urlToLoginPage;

	public void sendHTMLEmail(EmailDTO email) throws MessagingException {

		String[] to = email.getTo();
		String subject = email.getSubject();
		String content = email.getBody();

		sendHTMLEmail(from, to, subject, content);
	}

	private void sendHTMLEmail(String from, String[] to, String subject, String htmlContent) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setSubject(subject);
		helper.setTo(to);
		helper.setText(htmlContent, true);

		javaMailSender.send(message);
	}

}
