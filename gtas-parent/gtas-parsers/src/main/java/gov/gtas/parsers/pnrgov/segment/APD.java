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
 * APD: ADDITIONAL PRODUCT DETAILS
 * <p>
 * Flight equipment
 * <p>
 * Ex:Equipment Type of Boeing 747 (APD+747')
 */
public class APD extends Segment {
    public APD(List<Composite> composites) {
        super(APD.class.getSimpleName(), composites);
    }
}
