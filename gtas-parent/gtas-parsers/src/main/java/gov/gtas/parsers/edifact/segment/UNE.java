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
 * UNE Functional Group Trailer
 */
public class UNE extends Segment {

    private int numberOfMessages;
    private String identificationNumber;

    public UNE(List<Composite> composites) {
        super(UNE.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                this.numberOfMessages = Integer.valueOf(c.getElement(0));
                break;
            case 1:
                this.identificationNumber = c.getElement(0);
                break;
            }
        }
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }
}
