/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.usedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

public class UNS extends Segment {
    public UNS(List<Composite> composites) {
        super(UNS.class.getSimpleName(), composites);
    }
}
