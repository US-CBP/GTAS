/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * MON: MONETARY INFORMATION
 * <p>
 * To specify ticket amount
 * <p>
 * Ex .Ticket/document amount is $0.00 due to an award
 * certificate.(MON+T:AWARD') Ticket/document amount is 297.50
 * EUR.(MON+T:297.50:EUR’)
 */
public class MON extends Segment {
    public MON(List<Composite> composites) {
        super(MON.class.getSimpleName(), composites);
    }
}
