/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * MEA MEASUREMENTS
 * <p>
 * Function: To specify physical measurements. This segment used to report
 * number of Checked Bags.
 */
public class MEA extends Segment {
    public MEA(List<Composite> composites) {
        super(MEA.class.getSimpleName(), composites);
    }
}
