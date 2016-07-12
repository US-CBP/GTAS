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
 * ABI: ADDITIONAL BUSINESS SOURCE INFORMATION
 * <p>
 * ex:The creator of the history credit is a DL agent in Atlanta.
 * (ABI+4+05FD28:GS+ATL++DL’)
 */
public class ABI extends Segment {
    public ABI(List<Composite> composites) {
        super(ABI.class.getSimpleName(), composites);
    }
}
