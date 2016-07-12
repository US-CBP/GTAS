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
 * AUT AUTHENTICATION RESULT
 * <p>
 * Function: To specify results of the application of an authentication
 * procedure.
 * <p>
 * Note: this segment is specified in the UN/edifact spec, but we have
 * yet to see a message in the wild with one.
 */
public class AUT extends Segment {
    public AUT(List<Composite> composites) {
        super(AUT.class.getSimpleName(), composites);
    }
}
