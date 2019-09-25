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
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.inject.internal.util.Lists;

import gov.gtas.model.Document;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.WatchlistCategory;
import gov.gtas.repository.AppConfigurationRepository;
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

	private final HitsSummaryRepository hitsSummaryService;

	private final AppConfigurationService appConfigurationService;

	private final WatchlistCatService watchlistCatService;

	public NotificatonServiceImpl(SnsService snsService, HitsSummaryRepository hitsSummaryService,
			AppConfigurationService appConfigurationService, WatchlistCatService watchlistCatService) {
		this.snsService = snsService;
		this.hitsSummaryService = hitsSummaryService;
		this.appConfigurationService = appConfigurationService;
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
	 * 1. Watchlist category ID is not Interpol Red Notices
	 * 
	 * 2. The passenger dob is January first
	 * 
	 */
	@Override
	public void sendHitNotifications(List<HitsSummary> hitsSummaryList) {

		try {
			boolean hitNotificationEnabled = Boolean.parseBoolean(appConfigurationService
					.findByOption(AppConfigurationRepository.ENABLE_INTERPOL_HIT_NOTIFICATION).getValue());

			if (!hitNotificationEnabled)
				return;
			long start = System.nanoTime();

			List<NotificationTextVo> notifications = this.notificationTexts(hitsSummaryList);

			final Long interpolRedNoticesId = Long.parseLong(
					appConfigurationService.findByOption(AppConfigurationRepository.INTERPOL_WATCHLIST_ID).getValue());
			logger.debug("Interpol Red Notices ID: {}", interpolRedNoticesId);

			// filter out hits on passengers with dob equals January first
			notifications = notifications.stream().filter(dayMonthEqualsJanuaryFirst()).collect(Collectors.toList());

			// filter out hits on non Interpol Red Notices
			notifications = notifications.stream().filter(p -> p.getWlCategoryId().longValue() == interpolRedNoticesId)
					.collect(Collectors.toList());

			// for every hit summary, send a separate notification
			notifications.forEach(this::sendHitNotification);

			logger.debug("Notifications sent in {} m/s", (System.nanoTime() - start) / 1000000);

		} catch (Exception e) {
			logger.error("Sending Interpol Red Notices notification failed.", e);
		}

	}

	/**
	 * Predicate to check if DOB is January First
	 * 
	 * @return
	 */
	public static Predicate<NotificationTextVo> dayMonthEqualsJanuaryFirst() {

		return p -> {

			int day = DateCalendarUtils.getDayOfDate(p.getDob(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT));

			int month = DateCalendarUtils.getMonthOfDate(p.getDob(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ROOT));

			return (day != 01) || (month != 01);
		};
	}

	private void sendHitNotification(NotificationTextVo notification) {
		snsService.sendNotification(notification.toString());
	}

	/**
	 * 
	 * @param hitsSummaryList
	 * @return List<NotificationTextVo>
	 */
	public List<NotificationTextVo> notificationTexts(List<HitsSummary> hitsSummaryList) {

		List<NotificationTextVo> notificationTexts = Lists.newArrayList();
		hitsSummaryList = (List<HitsSummary>) this.hitsSummaryService
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
