/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * To end and check the completeness of a message by counting the segments in
 * the message (including UNH and UNT) and validating that the message reference
 * number equates to data element 0062 in the UNH segment
 * 
 * <p>
 * ex:UNT+2578+MSG001'UNT+2578+1'
 */
public class UNT extends Segment {
    private Integer numberOfSegments;
    private String messageRefNumber;

    public UNT(List<Composite> composites) {
        super(UNT.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                this.numberOfSegments = Integer.valueOf(c.getElement(0));
                break;
            case 1:
                this.messageRefNumber = c.getElement(0);
                break;
            }
        }
    }

    public int getNumberOfSegments() {
        return numberOfSegments;
    }

    public String getMessageRefNumber() {
        return messageRefNumber;
    }
}
