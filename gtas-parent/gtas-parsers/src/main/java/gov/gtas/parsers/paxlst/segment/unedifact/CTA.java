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
 * CTA CONTACT INFORMATION
 * <p>
 * Function: To identify a person or a department to whom communication should
 * be directed.Contact information
 * <p>
 * Note: this segment appears in version 13b of the UN paxlst spec. It does not
 * exist in prior versions.
 */
public class CTA extends Segment {
    public CTA(List<Composite> composites) {
        super(CTA.class.getSimpleName(), composites);
    }
}
