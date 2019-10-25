package gov.gtas.services;

import java.io.File;
import java.util.Date;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import gov.gtas.services.dto.EmailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.Passenger;

@Component
public class GtasEmailService {

	private static Logger logger = LoggerFactory.getLogger(GtasEmailService.class);

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private PassengerService passengerService;

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

	@Transactional
	public void send(String[] to, Long paxId, String note, String hitViewStatus) {
		Passenger passenger = passengerService.findByIdWithFlightPaxAndDocumentsAndHitDetails(paxId);

		String subject = createEmailSubject(passenger);
		String body = createEmailBodyText(passenger, note, hitViewStatus);		

		if (pathToAttachment == null || pathToAttachment.isEmpty()) {
			sendSimpleEmail(from, to, subject, body);
		} else {
			sendEmailWithAttachment(from, to, subject, body, pathToAttachment);
		}
	}

	private void sendSimpleEmail(String from, String[] to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setSubject(subject);
		message.setTo(to);
		message.setFrom(from);
		message.setText(body);

		javaMailSender.send(message);
	}

	private void sendEmailWithAttachment(String from, String[] to, String subject, String body,
			String pathToAttachment) {
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
		} catch (MessagingException ignored) {
			logger.error("Error!", ignored);
		}
	}

	private String createEmailBodyText(Passenger passenger, String note, String hitViewStatus) {
		return  "NOTES: " + note + "\n\n"
				+ "Hit Status: " + hitViewStatus + "\n" 
				+ getPassengerInfo(passenger)
				+ getFlightInfo(passenger.getFlight())
				+ getHitCategoryInfo(passenger.getHitDetails());
							

	}
	
	private String getPassengerInfo(Passenger passenger) {
		return  "First Name: " + passenger.getPassengerDetails().getFirstName() + "\n" 
				+ "Last Name: " + passenger.getPassengerDetails().getLastName()  + "\n"
				+ "DOB: " + passenger.getPassengerDetails().getDob() + "\n"
				+ "Gender: " + passenger.getPassengerDetails().getGender() + "\n"
				+ getDocumentInfo(passenger.getDocuments());
	}
	
	private String getFlightInfo(Flight flight) {
		Date countDownToDate = flight.getFlightCountDownView().getCountDownTimer();	
		
		String timeRemaining = "";
		
		if ("I".equalsIgnoreCase(flight.getDirection())) {
			timeRemaining = "Arrival Time Remaining: " + getTimeRemaining(countDownToDate);
		}
		else {
			timeRemaining = "Departure Time Remaining: " + getTimeRemaining(countDownToDate);
		}
		
		return  "Flight Number: " + flight.getFlightNumber() + "\n" 
				+ "Flight Origin: " + flight.getOrigin() + "\n"
				+ "Flight Destination: " + flight.getDestination() +  "\n"
				+ timeRemaining + "\n";
	}
	
	private String getHitCategoryInfo(Set<HitDetail> hitDetails) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("Severity | Category | Rule (Type):\n");
		for (HitDetail hd : hitDetails) {
			String severity = hd.getHitMaker().getHitCategory().getSeverity().toString();
			String category = hd.getHitMaker().getHitCategory().getName();
			String title = hd.getTitle();
			String type = hd.getHitType();
			
			builder.append("\t" + severity + " | " + category + " | " + title + "(" + type + ")\n");
		}
		
		return builder.toString();
		
	}
	
	private String getDocumentInfo(Set<Document> documents) {
		StringBuilder builder = new StringBuilder();
		builder.append("Documents: \n\tType\t\tNumber \n");
		
		for(Document doc : documents) {
			builder.append("\t" + doc.getDocumentType() + "\t\t" + doc.getDocumentNumber() + "\n");
		}
		
		return builder.toString();
	}
	
	private String createEmailSubject(Passenger passenger) {
		return "(GTAS) Hit Status Notification: " 
				+ passenger.getPassengerDetails().getLastName().toUpperCase() + ", "
				+ passenger.getPassengerDetails().getFirstName();
			
				
	}
	
	private String getTimeRemaining(Date date) {
		Date now = new Date();
		Long timeRemainingInMilliSeconds = date.getTime() - now.getTime();
		Long seconds = timeRemainingInMilliSeconds / 1000;
		Long days = seconds / 86400;
		seconds = seconds % 86400;
		Long hours = seconds / 3600;
		seconds = seconds % 3600;
		Long minutes = seconds % 60;
		
		return Math.abs(days) + "d " + Math.abs(hours) + "h " + Math.abs(minutes) + "m";
		
	}
	

}
