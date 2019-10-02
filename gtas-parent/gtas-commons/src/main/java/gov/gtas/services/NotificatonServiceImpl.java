/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sns.AmazonSNS;
import com.google.inject.internal.util.Lists;
import com.google.inject.internal.util.Sets;

import gov.gtas.aws.HitNotificationConfig;
import gov.gtas.model.Document;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.WatchlistCategory;
import gov.gtas.repository.HitsSummaryRepository;
import gov.gtas.services.watchlist.WatchlistCatService;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.vo.NotificationTextVo;

/**
 * Sends email notification for Interpol Red Notices Watchlist hits
 */
@Service
@Transactional
public class NotificatonServiceImpl implements NotificatonService {

	private final Logger logger = LoggerFactory.getLogger(NotificatonServiceImpl.class);

	private final SnsService snsService;

	private AmazonSNS amazonSNS;

	private String topicArn;
	private String topicSubject;

	private final HitsSummaryRepository hitsSummaryRepository;

	private final WatchlistCatService watchlistCatService;

	public NotificatonServiceImpl(SnsService snsService, HitsSummaryRepository hitsSummaryService,
			WatchlistCatService watchlistCatService) {
		this.snsService = snsService;
		this.hitsSummaryRepository = hitsSummaryService;
		this.watchlistCatService = watchlistCatService;
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

			/**
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
			notifications.forEach(n -> messageIds.add(this.sendHitNotification(n)));
			logger.debug("Notifications sent in {} m/s", (System.nanoTime() - start) / 1000000);

		} catch (Exception e) {
			logger.error("Sending Interpol Red Notices notification failed.", e);
			return messageIds;
		}

		return messageIds;
	}

	/**
	 * Predicate to check if DOB is January First
	 * 
	 * @return
	 */
	private static Predicate<NotificationTextVo> dayMonthEqualsJanuaryFirst() {

		return p -> {
			int day = DateCalendarUtils.getDayOfDate(p.getDob(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT));

			int month = DateCalendarUtils.getMonthOfDate(p.getDob(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT));
			return (day != 01) || (month != 01);
		};
	}

	private String sendHitNotification(NotificationTextVo notification) {
		return snsService.sendNotification(this.amazonSNS, notification.toString(), this.topicSubject, topicArn);
	}

	/**
	 * 
	 * @param hitsSummaryList
	 * @return List<NotificationTextVo>
	 */

	private List<NotificationTextVo> notificationTexts(List<HitsSummary> hitsSummaryList) {

		List<NotificationTextVo> notificationTexts = Lists.newArrayList();
		hitsSummaryList = (List<HitsSummary>) this.hitsSummaryRepository
				.findAllById(hitsSummaryList.stream().map(HitsSummary::getId).collect(Collectors.toList()));

		hitsSummaryList.forEach(o -> {
			Passenger p = o.getPassenger();
			HitDetail hitDetail = o.getHitdetails().stream().filter(h -> h.getHitType().equals("P")).findFirst().get();
			notificationTexts.add(getNotificationVo(o, p, hitDetail));
		});

		return notificationTexts;
	}

	private NotificationTextVo getNotificationVo(HitsSummary o, Passenger p, HitDetail hitDetail) {

		NotificationTextVo notificationVo = new NotificationTextVo();
		WatchlistCategory wlCategory = this.watchlistCatService.findCatByWatchlistItemId(hitDetail.getRuleId());

		notificationVo.setFirstName(p.getPassengerDetails().getFirstName());
		notificationVo.setLastName(p.getPassengerDetails().getLastName());
		notificationVo.setFlightNumber(p.getFlight().getFullFlightNumber());

		if (wlCategory != null) {
			notificationVo.setWatchlistOrRuleName(wlCategory.getName());
			notificationVo.setWlCategoryId(wlCategory.getId());
		}
		notificationVo.setDob(p.getPassengerDetails().getDob().toString());
		// If the passenger has more than one document, reduce the list into a comma
		// separate document numbers string
		notificationVo
				.setPassportNo(p.getDocuments().stream().map(Document::getDocumentNumber).reduce(",", (a, b) -> a + b));
		// The Time remaining is the time of departure or arrival depending on flight
		// direction
		CountDownCalculator calculator = new CountDownCalculator(new Date());
		notificationVo.setTimeRemaining(calculator
				.getCountDownFromDate(o.getFlight().getFlightCountDownView().getCountDownTimer()).getCountDownTimer());
		logger.debug("{}", notificationVo);

		return notificationVo;
	}

}
