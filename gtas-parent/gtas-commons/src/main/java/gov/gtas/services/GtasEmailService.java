package gov.gtas.services;

import java.io.File;
import java.util.Date;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.User;

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
	
	@Value("${login.page.url}")
	private String urlToLoginPage;

	@Transactional
	public void send(String[] to, Long paxId, String note, User sentBy) {
		Passenger passenger = passengerService.findByIdWithFlightPaxAndDocumentsAndHitDetails(paxId);

		String subject = createEmailSubject(passenger);
		String body = createEmailBodyText(passenger, note);
		String senderInfo = sentBy.getFirstName() + 
				" " + sentBy.getLastName() + 
				" (" + sentBy.getUserId() + ")";

		body += simpleHtmlFormater("Sent By", "<font color=red>" + senderInfo + "</font>");
		body += getGTASLoginUrl();

		if (pathToAttachment == null || pathToAttachment.isEmpty()) {
			sendSimpleEmail(from, to, subject, body);
		} else {
			sendEmailWithAttachment(from, to, subject, body, pathToAttachment);
		}
	}

	private void sendSimpleEmail(String from, String[] to, String subject, String body) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			// 'true' indicates multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);

			javaMailSender.send(message);
		} catch (MessagingException ignored) {
			logger.error("Error!", ignored);
		}
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

	private String createEmailBodyText(Passenger passenger, String note) {
		return  simpleHtmlFormater("<font color=red>NOTES</font>", "<i>" + note + "</i>") + "<br>"
				+ getPassengerInfo(passenger)
				+ getFlightInfo(passenger.getFlight())
				+ getHitCategoryInfo(passenger.getHitDetails());
							

	}
	
	private String getPassengerInfo(Passenger passenger) {
		return  simpleHtmlFormater("First Name", passenger.getPassengerDetails().getFirstName())
				+ simpleHtmlFormater("Last Name", passenger.getPassengerDetails().getLastName())
				+ simpleHtmlFormater("DOB", passenger.getPassengerDetails().getDob().toString())
				+ simpleHtmlFormater("Gender", passenger.getPassengerDetails().getGender())
				+ getDocumentInfo(passenger.getDocuments());
	}
	
	private String getFlightInfo(Flight flight) {
		Date countDownToDate = flight.getFlightCountDownView().getCountDownTimer();	
		
		String timeRemaining = "";
		
		if ("I".equals(flight.getDirection())) {
			timeRemaining = simpleHtmlFormater("Time Remaining before Arrival", getTimeRemaining(countDownToDate));
		}
		else {
			timeRemaining = simpleHtmlFormater("Time Remaining before Departure", getTimeRemaining(countDownToDate));
		}
		
		return  simpleHtmlFormater("Flight Number", flight.getFlightNumber()) 
				+ simpleHtmlFormater("Flight Origin", flight.getOrigin())
				+ simpleHtmlFormater("Flight Destination", flight.getDestination())
				+ simpleHtmlFormater("Carrier", flight.getCarrier())
				+ timeRemaining;
	}
	

	private String getHitCategoryInfo(Set<HitDetail> hitDetails) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("<b>Hit Details: </b><br>");
		if (hitDetails == null || hitDetails.size() == 1) {
			builder.append("<br><font color=red>There is no hit related to this passenger!</font><br>");
		}
		else {
			builder.append("<table border ='1'>");
			builder.append(createHtmlTableRow(new String[]{"Severity", "Category", "Rule", "Type", "Status"}, true));

			for (HitDetail hd : hitDetails) {
				String severity = hd.getHitMaker().getHitCategory().getSeverity().toString();
				String category = hd.getHitMaker().getHitCategory().getName();
				String title = hd.getTitle();
				String type = hd.getHitType();
				String status = hd.getHitViewStatus().toArray()[0].toString();

				builder.append(createHtmlTableRow(new String[] {severity, category, title, type, status}, false));
			}
			builder.append("</table><br>");
		}
		return builder.toString();
		
	}
	
	private String getDocumentInfo(Set<Document> documents) {
		StringBuilder builder = new StringBuilder();

		if (documents == null || documents.size() == 0) {
			builder.append("<br><font color=red>Passenger does not have documents! </font><br>");
		}
		else {
			builder.append("<b>Documents:</b><br>");
			builder.append("<table border ='1'>");
			builder.append(createHtmlTableRow(new String[] {"Type", "Number"}, true));

			for(Document doc : documents) {
				builder.append(createHtmlTableRow(new String[] {doc.getDocumentType(), doc.getDocumentNumber()}, false));
			}

			builder.append("</table><br>");
		}
		return builder.toString();
	}
	
	private String createEmailSubject(Passenger passenger) {
		return "GTAS Passenger Hit Status Notification: " 
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
	
	private String simpleHtmlFormater(String label, String value) {
		return "<b>" + label + ":  </b>" + value + "<br>";
	}
	
	private String createHtmlTableRow(String [] data, boolean isHeader) {
		String result = "<tr>";
		String openingTag = isHeader ? "<th>" : "<td>";
		String closingTag = isHeader ? "</th>" : "</td>";
		
		for (int i = 0; i < data.length; i++) {
			result += openingTag + data[i] + closingTag;
		}
		result += "</tr>";
		
		return result;
	}
	
	private String getGTASLoginUrl() {
		return "<a href=" + urlToLoginPage + ">GTAS Login</a>";
	}
	

}
