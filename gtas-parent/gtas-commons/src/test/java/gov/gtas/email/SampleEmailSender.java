package gov.gtas.email;

import freemarker.template.TemplateException;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.enumtype.HitSeverityEnum;
import gov.gtas.enumtype.HitViewStatusEnum;
import gov.gtas.enumtype.LookoutStatusEnum;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightCountDownView;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitMaker;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.User;
import gov.gtas.model.UserGroup;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.services.NotificatonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;

import static gov.gtas.enumtype.HitTypeEnum.WATCHLIST_PASSENGER;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class })
public class SampleEmailSender {

    private static final String RECIPIENT_EMAIL =  "simbamarufu1@gmail.com";

    @Value("${hit.priority.category}")
    private Long testHitPriorityCategory;

    @Autowired
    private NotificatonService notificationService;

    @Test
    public void sendSampleHitEmailNotification() throws IOException, TemplateException {
        Passenger testPassenger = generateSamplePassenger();
        notificationService.sendAutomatedHitEmailNotifications(Collections.singleton(testPassenger));

    }

    private Passenger generateSamplePassenger() {
        Passenger passenger = new Passenger();

        PassengerDetails passengerDetails = new PassengerDetails();
        passengerDetails.setFirstName("John");
        passengerDetails.setLastName("Doe");
        passengerDetails.setGender("MALE");
        passengerDetails.setDob(java.sql.Date.valueOf(LocalDate.of(2000,1,2)));
        passenger.setPassengerDetails(passengerDetails);

        HitDetail hitDetail = new HitDetail(WATCHLIST_PASSENGER);
        HitMaker hitMaker = new WatchlistItem();
        hitDetail.setHitMaker(hitMaker);
        hitDetail.setTitle("Age Rule");
        hitDetail.setHitType("R");

        HitCategory hitCategory = new HitCategory();
        hitMaker.setHitCategory(hitCategory);
        hitCategory.setName("Terrorism");
        hitCategory.setSeverity(HitSeverityEnum.TOP);
        hitCategory.setId(testHitPriorityCategory);

        UserGroup userGroup = new UserGroup();
        User user = new User();
        user.setEmail(RECIPIENT_EMAIL);
        user.setEmailEnabled(true);
        user.setHighPriorityHitsEmailNotification(true);
        userGroup.setGroupMembers(Collections.singleton(user));
        hitCategory.setUserGroups(Collections.singleton(userGroup));
        passenger.setHitDetails(Collections.singleton(hitDetail));

        HitViewStatus hitViewStatus = new HitViewStatus(hitDetail, userGroup, HitViewStatusEnum.NEW, passenger, LookoutStatusEnum.ACTIVE);
        hitDetail.setHitViewStatus(Collections.singleton(hitViewStatus));

        Document document = new Document();
        document.setDocumentNumber("A123456");
        document.setDocumentType("P");
        passenger.setDocuments(Collections.singleton(document));

        Flight flight = new Flight();
        flight.setFlightNumber("F435435");
        flight.setOrigin("BWI");
        flight.setDestination("LHR");
        flight.setCarrier("Spirit");

        FlightCountDownView flightCountDownView = new FlightCountDownView();
        flightCountDownView.setCountDownTimer(java.sql.Date.valueOf(LocalDate.of(2030,1,2)));
        flight.setFlightCountDownView(flightCountDownView);
        passenger.setFlight(flight);

        return passenger;
    }

}