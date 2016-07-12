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
 * SEGMENT REPETITION CONTROL
 * <p>
 * Used as trigger segment for PNRGOV GR.1 and will repeat for each PNR in the
 * message. This trigger segment is sent as an empty segment.(Ex: SRC')
 */
public class SRC extends Segment {
    public SRC(List<Composite> composites) {
        super(SRC.class.getSimpleName(), composites);
    }
}
