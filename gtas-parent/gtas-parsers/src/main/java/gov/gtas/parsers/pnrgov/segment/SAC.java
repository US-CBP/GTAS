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
 * SAC: SOURCE AND ACTION INFORMATION
 * <p>
 * Used in conjunction with other segments where the item was actioned. Eg Name
 * Change, flight etc Flown segments are to be included in history.
 * <p>
 * Examples: The history line contains a cancelled item(SAC+++X') The history
 * line contains an added item(SAC+++A’)
 */
public class SAC extends Segment {
    public enum SacCode {
        ADDED,
        CANCELLED
    }
    
    private SacCode action;

    public SAC(List<Composite> composites) {
        super(SAC.class.getSimpleName(), composites);
        
        Composite c = getComposite(2);
        if (c != null) {
            String code = c.getElement(0);
            switch (code) {
            case "A":
                this.action = SacCode.ADDED;
                break;
            case "X":
                this.action = SacCode.CANCELLED;
                break;
            }
        }
    }

    public SacCode getAction() {
        return action;
    }
}
