/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * CNT CONTROL TOTAL
 * <p>
 * Function: To provide control total (total # of passengers in this message).
 */
public class CNT extends Segment {
    public CNT(List<Composite> composites) {
        super(CNT.class.getSimpleName(), composites);
    }
}
