/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov;

import org.junit.Before;
import org.junit.Test;

import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.PnrVo;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class PnrGovParserTest {
    private static final String PNR_MESSAGE_PG_77 = "/pnr-messages/pnrMessagePg77.txt";
    private static final String PNR_MESSAGE_PG_76 = "/pnr-messages/pnrMessagePg76.txt";
    private static final String PNR_BAD_FORMAT = "/pnr-messages/pnrBadFormat.txt";
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
        LocalDateTime May23rd2013At212400 = LocalDateTime.of(2013, 5, 23, 21, 24);
        assertTrue(May23rd2013At212400.isEqual(dateBooked));
    }

    @Test
    public void bookingDateMapsToFirstDateWithCode710() throws ParseException, IOException, URISyntaxException {
        String message76 = getMessageText(PNR_MESSAGE_PG_76);
        PnrVo vo = this.parser.parse(message76);
        LocalDate dateBooked = getLocalDate(vo.getDateBooked());
        LocalDate Feb142013 = LocalDate.of(2013, 2, 14);
        assertTrue(Feb142013.isEqual(dateBooked));
    }

    @Test
    public void badFormattingAtEndOfMessage() throws IOException, URISyntaxException, ParseException {
        String badFormatMessage = getMessageText(PNR_BAD_FORMAT);
        PnrVo vo = this.parser.parse(badFormatMessage);
    }



    private LocalDate getLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private LocalDateTime getLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private static String getMessageText(String messageRelativePath) throws IOException, URISyntaxException {
        ClassPathResource resource = new ClassPathResource(messageRelativePath);
        URL url = resource.getURL();
        java.nio.file.Path resPath = Paths.get(url.toURI());
        return new String(Files.readAllBytes(resPath), StandardCharsets.UTF_8);
    }
}
