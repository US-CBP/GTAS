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
 * <p>ERC: APPLICATION ERROR INFORMATION
 * 
 * <p>Ex:Application Error - Invalid Departure Time(ERC+103')
 * Ex:Invalid flight number(ERC+114')
 */
public class ERC extends Segment{
    public ERC(String name, List<Composite> composites) {
        super(name, composites);
    }
}
