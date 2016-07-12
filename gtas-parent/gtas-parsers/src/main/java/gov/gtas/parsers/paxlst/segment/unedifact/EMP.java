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
 * EMP EMPLOYMENT DETAILS
 * <p>
 * Function: To specify employment details.
 */
public class EMP extends Segment {
    public EMP(List<Composite> composites) {
        super(EMP.class.getSimpleName(), composites);
    }
}
