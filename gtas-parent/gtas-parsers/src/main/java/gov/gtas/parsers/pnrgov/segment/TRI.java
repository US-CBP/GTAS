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
 * <p>TRI: TRAVELLER REFERENCE INFORMATION 
 * <p>Check-in info; sequence number.
 */
public class TRI extends Segment {
    private String travelerReferenceNumber;
    
    public TRI(List<Composite> composites) {
        super(TRI.class.getSimpleName(), composites);
        Composite c = getComposite(1);
        if (c != null) {
            this.travelerReferenceNumber = c.getElement(3);
        }
    }

    public String getTravelerReferenceNumber() {
        return travelerReferenceNumber;
    }

    public void setTravelerReferenceNumber(String travelerReferenceNumber) {
        this.travelerReferenceNumber = travelerReferenceNumber;
    }
}
