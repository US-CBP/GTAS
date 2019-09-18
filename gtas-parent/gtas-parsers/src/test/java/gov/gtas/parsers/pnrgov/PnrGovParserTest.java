/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov;

import gov.gtas.parsers.ParserTestHelper;
import gov.gtas.parsers.vo.SeatVo;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.PnrVo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class PnrGovParserTest implements ParserTestHelper {
    private static final String PNR_MESSAGE_PG_77 = "/pnr-messages/pnrMessagePg77.txt";
    private static final String PNR_MESSAGE_PG_76 = "/pnr-messages/pnrMessagePg76.txt";
    private static final String PNR_MESSAGE_PG_75 = "/pnr-messages/pnrMessagePg75.txt";
    private static final String PNR_BAD_FORMAT = "/pnr-messages/pnrBadFormat.txt";
    private static final String BIG_PNR = "/pnr-messages/pnrWithBags.txt";
    private static final String PNR_WITH_BAGS = "/pnr-messages/bigMessagePnr.txt";
    private static final String PNR_EXAMPLE = "/pnr-messages/pnrMessageExample.txt";
    private static final String PNR_EDGE = "/pnr-messages/pnrEdge.txt";
    private static final String PNR_CTC_M = "/pnr-messages/pnrPhoneCTC_M.txt";
    private static final String PNR_CTCM = "/pnr-messages/pnrPhoneCTCM.txt";
    private static final String PNR_ADD_CTCM = "/pnr-messages/pnrAddressPhoneCTCM.txt";
    private static final String PNR_SEAT_NSST = "/pnr-messages/pnrSeatFormats.txt";
    private static final String failingMessage1 = "/pnr-messages/failingMessage1.txt";
    private static final String PNR_DOCO = "/pnr-messages/pnrWithDoco.txt";
    
    private EdifactParser<PnrVo> parser;

    @Before
    public void setUp() {
        this.parser = new PnrGovParser();
    }

    /*
     * The following test use slightly modified messages found on page 76 and 77 of the
     * PASSENGER AND AIRPORT DATA
     * INTERCHANGE STANDARDS EDIFACT IMPLEMENTATION GUIDE
     * PNR DATA PUSHED TO STATES  OR OTHER AUTHORITIES
     * PNRGOV MESSAGE
     * Version 13.1
     * */

    // PNR origin should equal the TVL5 origin, not the TVL0 origin for
    // a multi-leg trip
    @Test
    public void PnrOriginTest() throws ParseException, IOException, URISyntaxException {
      String msg = getMessageText(PNR_EXAMPLE);
      PnrVo vo = this.parser.parse(msg);

      assertEquals(vo.getOrigin(), "WDH");
    }

    @Test
    public void reservationDateMapsToRCITimeCreated() throws ParseException, IOException, URISyntaxException {
        String message77 = getMessageText(PNR_MESSAGE_PG_77);
        PnrVo vo = this.parser.parse(message77);
        LocalDateTime reservationDate = getLocalDateTime(vo.getReservationCreateDate());
        LocalDateTime May23rd2013At181348 = LocalDateTime.of(2013, 5, 23, 18, 13, 48);
        assertTrue(May23rd2013At181348.isEqual(reservationDate));
    }

    @Test
    public void dateBookedMapsToPaymentMadeDate() throws ParseException, IOException, URISyntaxException {
        String message77 = getMessageText(PNR_MESSAGE_PG_77);
        PnrVo vo = this.parser.parse(message77);
        LocalDateTime dateBooked = getLocalDateTime(vo.getDateBooked());
        LocalDateTime May23rd2013At212400 = LocalDateTime.of(2013, 5, 23, 18, 13, 48);
        assertTrue(May23rd2013At212400.isEqual(dateBooked));
    }

    @Test
    public void bookingDateMapsToFirstDateWithCode710() throws ParseException,   IOException, URISyntaxException {
        String message76 = getMessageText(PNR_MESSAGE_PG_76);
        PnrVo vo = this.parser.parse(message76);
        LocalDate dateBooked = getLocalDate(vo.getDateBooked());
        LocalDate Feb142013 = LocalDate.of(2013, 2, 15);
        assertTrue(Feb142013.isEqual(dateBooked));
    }

    @Test
    public void badFormattingAtEndOfMessage() throws IOException, URISyntaxException, ParseException {
        String badFormatMessage = getMessageText(PNR_BAD_FORMAT);
        PnrVo vo = this.parser.parse(badFormatMessage);
    }

    @Test
    @Ignore
    public void pnrParsePage75() throws IOException, URISyntaxException, ParseException {
        String badFormatMessage = getMessageText(PNR_MESSAGE_PG_75);
        PnrVo vo = this.parser.parse(badFormatMessage);
    }

    @Test
    public void pnrWithBgs() throws IOException, URISyntaxException, ParseException {
        String pnrWithBags = getMessageText(PNR_WITH_BAGS);
        PnrVo vo = this.parser.parse(pnrWithBags);
        assertFalse(vo.getBagVos().isEmpty());
    }

    @Test
    public void pnrExampleTest() throws IOException, URISyntaxException, ParseException {
        String pnrExample = getMessageText(PNR_EXAMPLE);
        PnrVo vo = this.parser.parse(pnrExample);
        Integer bagsInPNRExample = 18;
        assertEquals(bagsInPNRExample, vo.getTotal_bag_count());
    }

    @Test
    public void testingEscapeCharacter() throws IOException, URISyntaxException, ParseException {
        String pnrExample = getMessageText(PNR_EDGE);
        PnrVo vo = this.parser.parse(pnrExample);
        assertEquals(vo.getEmails().get(0).getAddress(), " MOLLY_LOUUNT+50BB@GMAIL.COM");
    }

    @Test
    public void pnrPhoneWithSpaceTest() throws IOException, URISyntaxException, ParseException {
        String pnrExample = getMessageText(PNR_CTC_M);
        PnrVo vo = this.parser.parse(pnrExample);
        assertEquals(vo.getPhoneNumbers().get(1).getNumber(), "123456789");
    }
    @Test
    public void pnrPhoneNumberNoSpaceTest() throws IOException, URISyntaxException, ParseException {
        String pnrExample = getMessageText(PNR_CTCM);
        PnrVo vo = this.parser.parse(pnrExample);
        assertEquals(vo.getPhoneNumbers().get(1).getNumber(), "123456789");
    }
    @Test
    public void pnrAddressPhoneNumber() throws IOException, URISyntaxException, ParseException {
        String pnrExample = getMessageText(PNR_ADD_CTCM);
        PnrVo vo = this.parser.parse(pnrExample);
        assertEquals(vo.getPhoneNumbers().get(1).getNumber(), "5432109876");
    }
    @Test
    public void pnrSeatVo() throws IOException, URISyntaxException, ParseException {
        String pnrExample = getMessageText(PNR_SEAT_NSST);
        PnrVo vo = this.parser.parse(pnrExample);
        assertEquals(vo.getPassengers().get(0).getSeatAssignments().size(), 2);
        SeatVo seat = vo.getPassengers().get(0).getSeatAssignments().get(1);
        assertEquals(seat.getApis(), false);
        assertEquals(seat.getDestination(), "IAD");
        assertEquals(seat.getOrigin(), "SFO");
        assertEquals(seat.getNumber(), "074E");
        assertEquals(seat.getTravelerReferenceNumber(), "1");
    }
    
    @Test
    public void pnrDocoTest() throws IOException, URISyntaxException, ParseException{
    	String pnrExample = getMessageText(PNR_DOCO);
    	PnrVo vo = this.parser.parse(pnrExample);
    	DocumentVo docoVo = vo.getPassengers().get(0).getDocuments().get(1); //DOCS is required for passenger to be generated, meaning DOCO will be the second document.
    	assertEquals(docoVo.getDocumentNumber(), "8675309");
    	assertEquals(docoVo.getDocumentType(), "V");
    	assertEquals(docoVo.getIssuanceCountry(), "OTHER PLACE");
    	assertEquals(docoVo.getIssuanceDate().toString(), "Fri Dec 22 00:00:00 EST 2006");
    	assertEquals(docoVo.getExpirationDate(), null); //DOCO does not have expiration date per spec.
    }
    
    /*    @Test
    public void failingMessage1() throws IOException, URISyntaxException, ParseException {
        String pnrExample = getMessageText(failingMessage1);
        PnrVo dob = this.parser.parse(pnrExample);
        System.out.println("test");
    }*/

}
