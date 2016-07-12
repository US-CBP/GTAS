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
 * GEI: Processing Information - GR. 4
 * <p>
 * Function: To identify that information for this passenger has been validated.
 * <p>
 * Examples:
 * 
 * GEI+4+173' Indicates that the information contained for this passenger has
 * been verified.
 */
public class GEI extends Segment {
    private boolean verified;

    public GEI(List<Composite> composites) {
        super(GEI.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 1:
                switch (c.getElement(0)) {
                case "173":
                case "ZZZ":
                    this.verified = true;
                    break;
                case "174":
                    this.verified = false;
                    break;
                default:
                    this.verified = false;
                }
                break;
            }
        }
    }

    public boolean isVerified() {
        return verified;
    }
}
