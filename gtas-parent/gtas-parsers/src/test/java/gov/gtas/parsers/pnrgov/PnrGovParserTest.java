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
import static org.junit.Assert.assertTrue;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class PnrGovParserTest {
    private EdifactParser<PnrVo> parser;

    @Before
    public void setUp() {
        this.parser = new PnrGovParser();
    }
    /*
    * The following test use a slightly modified message found on page 77 of the
    * PASSENGER AND AIRPORT DATA
    * INTERCHANGE STANDARDS EDIFACT IMPLEMENTATION GUIDE
    * PNR DATA PUSHED TO STATES  OR OTHER AUTHORITIES
    * PNRGOV MESSAGE
    * Version 13.1
    * */
    @Test
    public void reservationDateMapsToRCITimeCreated() throws ParseException {
       PnrVo vo = this.parser.parse(pnrTestMessages.fullPnrMessage());
        LocalDateTime reservationDate =  Instant.ofEpochMilli(vo.getReservationCreateDate().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
       LocalDateTime May23rd2013At181348 = LocalDateTime.of(2013, 5, 23, 18, 13, 48);
        assertTrue(May23rd2013At181348.isEqual(reservationDate));
    }

    @Test
    public void dateBookedMapsToPaymentMadeDate() throws ParseException {
        PnrVo vo = this.parser.parse(pnrTestMessages.fullPnrMessage());
        LocalDateTime dateBooked =  Instant.ofEpochMilli(vo.getDateBooked().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime May23rd2013At212400 = LocalDateTime.of(2013, 5, 23, 21, 24);
        assertTrue(May23rd2013At212400.isEqual(dateBooked));
    }
}
