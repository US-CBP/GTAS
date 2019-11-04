/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import freemarker.template.TemplateException;
import gov.gtas.email.HitEmailNotificationService;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.services.dto.EmailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.google.inject.internal.util.Lists;
import com.google.inject.internal.util.Sets;

import gov.gtas.aws.HitNotificationConfig;
import gov.gtas.model.Document;
import gov.gtas.model.HitDetail;
import gov.gtas.model.Passenger;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.vo.NotificationTextVo;

/**
 * Sends email notification for Interpol Red Notices Watchlist hits
 */
@Service
@Transactional
public class NotificatonServiceImpl implements NotificatonService {

	private final Logger logger = LoggerFactory.getLogger(NotificatonServiceImpl.class);

	// Date format in the subject line and body of the notification email
	private static final String DOB_FORMAT = "dd-MMM-yy";
	private final SnsService snsService;

	private AmazonSNS amazonSNS;

	private String topicArn;
	private String topicSubject;

	private final HitCategoryService watchlistCatService;
	private final GtasEmailService emailService;
	private final HitEmailNotificationService hitEmailNotificationService;

	public NotificatonServiceImpl(SnsService snsService,
								  HitCategoryService watchlistCatService,
								  GtasEmailService emailService,
								  HitEmailNotificationService hitEmailNotificationService) {
		this.snsService = snsService;
		this.watchlistCatService = watchlistCatService;
		this.emailService = emailService;
		this.hitEmailNotificationService = hitEmailNotificationService;
	}

	/**
	 * Sends email notification for Interpol Red Notices Watchlist hits
	 * 
	 * Notifications are sent using AWS SNS topic. All subscribers will receive
	 * separate email. Currently the email include a link to unsubscribe from the
	 * topic
	 * 
	 * The method is toggled, notification can be disabled by updating the lookup
	 * table app_configureation (ENABLE_HIT_NOTIFICATION = false)
	 * 
	 * Skip sending notification if
	 * 
	 * 1. Watchlist category ID is not Interpol Red Notices AND
	 * 
	 * 2. The passenger dob is January first
	 * 
	 */
	@Override
	public Set<String> sendHitNotifications(HitNotificationConfig config) {

		this.amazonSNS = config.getAmazonSNS();
		this.topicArn = config.getTopicArn();
		this.topicSubject = config.getTopicSubject();
		Set<String> messageIds = Sets.newHashSet();
		try {

			long start = System.nanoTime();

			/*
			 * Set SNS topic Amazon Resource Name (ARN) and the subject line
			 */
			List<NotificationTextVo> notifications = this.notificationTexts(config.getHits());
			logger.debug("Interpol Red Notices ID: {}", config.getTargetwatchlistId());

			// filter out hits on passengers with dob equals January first
			notifications = notifications.stream().filter(dayMonthEqualsJanuaryFirst()).collect(Collectors.toList());
			// filter out hits on non Interpol Red Notices
			notifications = notifications.stream()
					.filter(p -> p.getWlCategoryId().longValue() == config.getTargetwatchlistId())
					.collect(Collectors.toList());
			// for every hit summary, send a separate notification
			for (NotificationTextVo n : notifications) {
				messageIds.add(this.sendHitNotification(n));
			}
			logger.debug("Notifications sent in {} m/s", (System.nanoTime() - start) / 1000000);

		} catch (Exception e) {
			logger.error("Sending Interpol Red Notices notification failed.", e);
			return messageIds;
		}

		return messageIds;
	}

	@Override
	@Transactional
	public void sendAutomatedHitEmailNotifications(Set<Passenger> passengers) throws IOException, TemplateException {
		List<EmailDTO> emailDTOS = hitEmailNotificationService.generateAutomatedHighPriorityHitEmailDTOs(passengers);

		for(EmailDTO emailDTO: emailDTOS) {
			try {
				emailService.sendHTMLEmail(emailDTO);
				logger.info("Sent Email Notification to " + Arrays.toString(emailDTO.getTo()));
			} catch(Exception ex) {
				logger.warn(String.format("Automated hit email notification failed for email: %s, with the exception: %s", emailDTO.getTo()[0], ex));
			}
		}
	}

	@Override
	@Transactional
	public void sendManualNotificationEmail(String[] to, String note, Long paxId, String userId) throws IOException, TemplateException, MessagingException {
		EmailDTO emailDTO = hitEmailNotificationService.generateManualNotificationEmailDTO(to, note, paxId, userId);
		emailService.sendHTMLEmail(emailDTO);
	}

	/**
	 * Predicate to check if DOB is January First
	 * 
	 * @return if the day and month is jan 1
	 */
	private static Predicate<NotificationTextVo> dayMonthEqualsJanuaryFirst() {

		return p -> {
			int day = DateCalendarUtils.getDayOfDate(p.getDob(), DateTimeFormatter.ofPattern(DOB_FORMAT, Locale.ROOT));

			int month = DateCalendarUtils.getMonthOfDate(p.getDob(),
					DateTimeFormatter.ofPattern(DOB_FORMAT, Locale.ROOT));
			return (day != 1) || (month != 1);
		};
	}

	private String sendHitNotification(NotificationTextVo notification) {
		return snsService.sendNotification(this.amazonSNS, notification.toString(),
				this.getSubjectLine(notification, topicSubject), topicArn);
	}

	private String getSubjectLine(NotificationTextVo notification, String subject) {
		StringJoiner subjectBuilder = new StringJoiner(" ", "", subject);
		subjectBuilder.add(notification.getLastName().toUpperCase());
		subjectBuilder.add(notification.getFirstName().toLowerCase());
		subjectBuilder.add(notification.getDob());
		subjectBuilder.add(notification.getFlightNumber());
		subjectBuilder.add(notification.getTimeRemaining());
		subjectBuilder.add(" ");
		return subjectBuilder.toString();
	}

	/**
	 * 
	 * @return List<NotificationTextVo>
	 */

	private List<NotificationTextVo> notificationTexts(Set<Passenger> passengersWithHits) {

		List<NotificationTextVo> notificationTexts = Lists.newArrayList();

		for (Passenger passenger : passengersWithHits) {
			passenger.getHitDetails().stream().filter(h -> h.getHitType().equals("P")).findFirst()
					.ifPresent(hitDetail -> notificationTexts.add(getNotificationVo(passenger, hitDetail)));
		}

		return notificationTexts;
	}

	private NotificationTextVo getNotificationVo(Passenger p, HitDetail hitDetail) {

		NotificationTextVo notificationVo = new NotificationTextVo();
		HitCategory wlCategory = this.watchlistCatService.findById(hitDetail.getHitMakerId());

		notificationVo.setFirstName(p.getPassengerDetails().getFirstName());
		notificationVo.setLastName(p.getPassengerDetails().getLastName());
		notificationVo.setFlightNumber(p.getFlight().getFullFlightNumber());

		if (wlCategory != null) {
			notificationVo.setWatchlistOrRuleName(wlCategory.getName());
			notificationVo.setWlCategoryId(wlCategory.getId());
		}
		notificationVo.setDob(DateCalendarUtils.format(p.getPassengerDetails().getDob(), DOB_FORMAT));
		// If the passenger has more than one document, reduce the list into a comma
		// separate document numbers string
		notificationVo
				.setPassportNo(p.getDocuments().stream().map(Document::getDocumentNumber).reduce(",", (a, b) -> a + b));
		// The Time remaining is the time of departure or arrival depending on flight
		// direction
		CountDownCalculator calculator = new CountDownCalculator(new Date());
		notificationVo.setTimeRemaining(calculator
				.getCountDownFromDate(p.getFlight().getFlightCountDownView().getCountDownTimer()).getCountDownTimer());
		logger.debug("{}", notificationVo);

		return notificationVo;
	}

}
