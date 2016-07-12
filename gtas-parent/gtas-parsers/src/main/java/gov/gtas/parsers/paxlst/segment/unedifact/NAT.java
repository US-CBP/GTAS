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
 * NAT NATIONALITY
 * <p>
 * Function: To specify a nationality.
 * <p>
 * Example: NAT+2+CAN’ Indicates current nationality as a Canadian
 */
public class NAT extends Segment {
    /** ICAO 9303/ISO 3166 codes */
    private String nationalityCode;
    
    public NAT(List<Composite> composites) {
        super(NAT.class.getSimpleName(), composites);
        Composite c = getComposite(1);
        if (c != null) {
            this.nationalityCode = c.getElement(0);
        }
    }

    public String getNationalityCode() {
        return nationalityCode;
    }
}
