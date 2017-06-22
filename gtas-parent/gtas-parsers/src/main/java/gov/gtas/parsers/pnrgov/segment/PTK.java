/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * PTK: PRICING/TICKETING DETAILS
 * <p>
 * Class PTK to hold Pricing/ticketing details for a passenger.
 * <p>
 * Examples The pricing/ticketing details: the ticket is non-refundable, the
 * ticketing deadline date and time are 10 pm on 6/15/10, the validating carrier
 * is DL and the sales/ticketing location city code is ATL.
 * (PTK+NR++150610:2200+DL+006+ATL')
 */
public class PTK extends Segment {
    public PTK(List<Composite> composites) {
        super(PTK.class.getSimpleName(), composites);
    }
}
