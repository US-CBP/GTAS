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
 * UNZ Interchange Trailer
 */
public class UNZ extends Segment {
    private String interchangeControlCount;
    private String interchangeControlReference;

    public UNZ(List<Composite> composites) {
        super(UNZ.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                this.interchangeControlCount = c.getElement(0);
                break;
            case 1:
                this.interchangeControlReference = c.getElement(0);
                break;
            }
        }
    }

    public String getInterchangeControlCount() {
        return interchangeControlCount;
    }

    public String getInterchangeControlReference() {
        return interchangeControlReference;
    }
}
