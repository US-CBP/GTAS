/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.email;

import freemarker.template.TemplateException;
import gov.gtas.enumtype.HitSeverityEnum;
import gov.gtas.model.*;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.services.dto.EmailDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static gov.gtas.enumtype.HitTypeEnum.*;

@RunWith(MockitoJUnitRunner.class)
public class HighPriorityHitEmailNotificationServiceTest {

    @Mock
    private EmailTemplateLoader emailTemplateLoader;

    @InjectMocks
    private HighPriorityHitEmailNotificationService highPriorityHitEmailNotificationService;

    private static final String RECIPIENT_EMAIL =  "simbamarufu1@gmail.com";

    private Long testHitPriorityCategory = 2L;

    @Before
    public void before(){
        ReflectionTestUtils.setField(highPriorityHitEmailNotificationService, "priorityHitCategory", testHitPriorityCategory);
    }

    @Test
    public void testEmptySet() throws IOException, TemplateException {
        List<EmailDTO> emails = highPriorityHitEmailNotificationService.generateEmailDTOs(new HashSet<>());
        Assert.assertEquals(0L, emails.size());
    }

    @Test
    public void testWLPassenger() throws IOException, TemplateException {
        Mockito.when(emailTemplateLoader.generateHtmlString(Mockito.any(), Mockito.any())).thenReturn("Super Awesome HTML representation of a passenger");
        ReflectionTestUtils.setField(highPriorityHitEmailNotificationService, "priorityHitCategory", testHitPriorityCategory);
        Passenger p = generateSamplePassenger();
        for (HitDetail hd : p.getHitDetails()) {
            hd.setHitEnum(WATCHLIST);
        }
        List<EmailDTO> emails = highPriorityHitEmailNotificationService.generateEmailDTOs(Collections.singleton(p));
        Assert.assertEquals(1L, emails.size());
    }

    @Test()
    public void testPWLPassengerIgnored() throws IOException, TemplateException {
        ReflectionTestUtils.setField(highPriorityHitEmailNotificationService, "priorityHitCategory", testHitPriorityCategory);
        Passenger p = generateSamplePassenger();
        for (HitDetail hd : p.getHitDetails()) {
            hd.setHitEnum(PARTIAL_WATCHLIST);
        }
       List<EmailDTO> emails =  highPriorityHitEmailNotificationService.generateEmailDTOs(Collections.singleton(p));
        Assert.assertEquals(0, emails.size());
    }

    @Test()
    public void testDobIgnored() throws IOException, TemplateException {
        ReflectionTestUtils.setField(highPriorityHitEmailNotificationService, "priorityHitCategory", testHitPriorityCategory);
        Passenger p = generateSamplePassenger();
        for (HitDetail hd : p.getHitDetails()) {
            hd.setHitEnum(WATCHLIST);
        }
        p.getPassengerDetails().setDob(java.sql.Date.valueOf(LocalDate.of(2000,1,1)));
        List<EmailDTO> emails =  highPriorityHitEmailNotificationService.generateEmailDTOs(Collections.singleton(p));
        Assert.assertEquals(0, emails.size());
    }


    private Passenger generateSamplePassenger() {
        Passenger passenger = new Passenger();

        PassengerDetails passengerDetails = new PassengerDetails();
        passengerDetails.setFirstName("John");
        passengerDetails.setLastName("Doe");
        passengerDetails.setGender("MALE");
        passengerDetails.setDob(java.sql.Date.valueOf(LocalDate.of(2000,2,13)));
        passenger.setPassengerDetails(passengerDetails);

        HitDetail hitDetail = new HitDetail(WATCHLIST_PASSENGER);
        HitMaker hitMaker = new WatchlistItem();
        hitDetail.setHitMaker(hitMaker);
        hitDetail.setTitle("Age Rule");
        hitDetail.setHitType("R");

        HitCategory hitCategory = new HitCategory();
        hitMaker.setHitCategory(hitCategory);
        hitCategory.setId(-1L);
        hitCategory.setName("Terrorism");
        hitCategory.setSeverity(HitSeverityEnum.TOP);
        hitCategory.setId(testHitPriorityCategory);

        UserGroup userGroup = new UserGroup();
        User user = new User();
        user.setEmail(RECIPIENT_EMAIL);
        userGroup.setGroupMembers(Collections.singleton(user));
        hitCategory.setUserGroups(Collections.singleton(userGroup));
        passenger.setHitDetails(Collections.singleton(hitDetail));

        Document document = new Document();
        document.setDocumentNumber("A123456");
        document.setDocumentType("P");
        passenger.setDocuments(Collections.singleton(document));

        Flight flight = new Flight();
        flight.setFlightNumber("F435435");

        FlightCountDownView flightCountDownView = new FlightCountDownView();
        flightCountDownView.setCountDownTimer(java.sql.Date.valueOf(LocalDate.of(2030,1,1)));
        flight.setFlightCountDownView(flightCountDownView);
        passenger.setFlight(flight);

        return passenger;
    }
}
