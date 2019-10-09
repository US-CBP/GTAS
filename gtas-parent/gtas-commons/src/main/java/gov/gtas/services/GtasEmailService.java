package gov.gtas.services;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import gov.gtas.services.dto.EmailDTO;

@Component
public class GtasEmailService {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String from;
	
	@Value("${path-to-attachment}")
	private String pathToAttachment;
	
	public void send(EmailDTO email) {
		String [] to =  email.getTo();
		String subject = email.getSubject();
		String body = email.getBody();
		String pathToAttachment = email.getPathToAttachment();
		
		if (pathToAttachment == null) {
			sendSimpleEmail(from, to, subject, body);
		}
		else {
			sendEmailWithAttachment(from, to, subject, body, pathToAttachment);
		}
	}
	
	private void sendSimpleEmail(String from, String [] to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setSubject(subject);
		message.setTo(to);
		message.setFrom(from);
		message.setText(body); 
		
		javaMailSender.send(message);
	}
	
	
	private void sendEmailWithAttachment(String from, String [] to, String subject, String body, String pathToAttachment) {
		try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // 'true' indicates multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            String fileName = file.getFilename();
            helper.addAttachment(fileName, file);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
	}
}

