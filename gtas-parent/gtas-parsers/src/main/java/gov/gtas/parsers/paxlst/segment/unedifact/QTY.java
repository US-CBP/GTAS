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
 * QTY QUANTITY
 * <p>
 * Function: To specify a pertinent quantity.
 */
public class QTY extends Segment {
    public QTY(List<Composite> composites) {
        super(QTY.class.getSimpleName(), composites);
    }
}
