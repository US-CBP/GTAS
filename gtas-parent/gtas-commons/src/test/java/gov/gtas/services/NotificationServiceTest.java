/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.lookup.HitCategory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.sns.AmazonSNS;
import com.google.inject.internal.util.Sets;

import gov.gtas.aws.HitNotificationConfig;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightCountDownView;
import gov.gtas.model.HitDetail;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;

public class NotificationServiceTest {

	private NotificatonService notificationService;
	@Mock
	private GtasEmailService emailService;
	@Mock
	private SnsService snsService;
	@Mock
	private HitCategoryService watchlistCatService;
	@Mock
	private AmazonSNS amazonSNS;

	private String subject = "Test";
	private String arn = "test";
	private final Long TARGET_WATCHLIST_ID = 4L;
	private final Long WATCHLIST_ITEM_ID = 1L;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		notificationService = new NotificatonServiceImpl(snsService, watchlistCatService, emailService);

		String messageId = "FcdR4553DF";
		String message = "this is a test message";
		when(snsService.sendNotification(amazonSNS, message, subject, arn)).thenReturn(messageId);
		// Create test WatchlistCategory
		HitCategory category = createTestWatchlistCategory(TARGET_WATCHLIST_ID);
		when(this.watchlistCatService.findById(WATCHLIST_ITEM_ID)).thenReturn(category);

	}

	@Test
	public void testSendNotification_WrongTargetWatchlistId_returnsZeroMessageId() {

		// Create test hits summary
		Passenger p = createFakePassenger(java.sql.Date.valueOf(LocalDate.of(1998, 1, 12)));

		// Set hit details
		HitDetail hitDetail = createTestHitDetail();
		p.getHitDetails().add(hitDetail);

		Long WRONG_TARGET_WATCHLIST_ID = Long.MAX_VALUE;
		HitNotificationConfig config = new HitNotificationConfig(amazonSNS, Collections.singleton(p), arn, subject,
				WRONG_TARGET_WATCHLIST_ID);
		Set<String> messageIds = this.notificationService.sendHitSnsNotifications(config);

		assertEquals(0, messageIds.size());
	}

	@Test
	public void testSendNotification_PassengerDobJanuaryFirst_returnsZeroMessageId() {

		// Create test hits summary
		Passenger p = createFakePassenger(java.sql.Date.valueOf(LocalDate.of(1998, 1, 1)));

		// Set hit details
		HitDetail hitDetail = createTestHitDetail();
		p.getHitDetails().add(hitDetail);

		HitNotificationConfig config = new HitNotificationConfig(amazonSNS, Collections.singleton(p), arn, subject,
				TARGET_WATCHLIST_ID);
		Set<String> messageIds = this.notificationService.sendHitSnsNotifications(config);

		assertEquals(0, messageIds.size());
	}

	@Test
	public void testSendNotification_PassengerDobNOTJanuaryFirstWithValidWatchlistId_returnsOneMessageId() {

		// Create test hits summary
		Passenger p = createFakePassenger(java.sql.Date.valueOf(LocalDate.of(1998, 10, 4)));

		// Set hit details
		HitDetail hitDetail = createTestHitDetail();
		p.getHitDetails().add(hitDetail);

		HitNotificationConfig config = new HitNotificationConfig(amazonSNS, Collections.singleton(p), arn, subject,
				TARGET_WATCHLIST_ID);
		Set<String> messageIds = this.notificationService.sendHitSnsNotifications(config);

		assertEquals(1, messageIds.size());
	}

	private HitDetail createTestHitDetail() {
		HitDetail hitDetail = new HitDetail(HitTypeEnum.WATCHLIST_PASSENGER);
		hitDetail.setHitType("P");
		hitDetail.setHitMakerId(WATCHLIST_ITEM_ID);
		return hitDetail;
	}

	private HitCategory createTestWatchlistCategory(Long watchListId) {
		HitCategory category = new HitCategory();
		category.setId(watchListId);
		category.setDescription("Interpol");
		category.setName("Interpol");
		return category;
	}

	private Passenger createFakePassenger(Date passengerDob) {

		// Set passenger information
		Passenger passenger = new Passenger();
		PassengerDetails passengerDetail = new PassengerDetails();
		Flight flight = new Flight();
		passenger.setDocuments(Sets.newHashSet());
		flight.setFullFlightNumber("001FRT");

		FlightCountDownView counter = new FlightCountDownView();
		counter.setCountDownTimer(new Date());
		flight.setFlightCountDownView(counter);

		passengerDetail.setDob(passengerDob);

		passengerDetail.setFirstName("John");
		passengerDetail.setLastName("Doe");
		passenger.setFlight(flight);
		passenger.setPassengerDetails(passengerDetail);

		Document document = new Document();
		document.setDocumentNumber("AAA09876");
		passenger.addDocument(document);
		return passenger;
	}
}
