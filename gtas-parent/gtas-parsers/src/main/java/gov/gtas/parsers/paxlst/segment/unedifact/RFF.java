/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * RFF: REFERENCE
 */
public class RFF extends Segment {
    public enum RffCode {
        TRANSACTION_REF_NUMBER,
        RESERVATION_REF_NUMBER,
        PASSENGER_REF_NUMBER,
        ASSIGNED_SEAT,
        GOVT_AGENCY_REF_NUMBER,
        CUSTOMER_REF_NUMBER
    }
    
    private RffCode referenceCodeQualifier;
    private String referenceIdentifier;
    private int revisionIdentifier;
    
    public RFF(List<Composite> composites) {
        super(RFF.class.getSimpleName(), composites);
        Composite c = getComposite(0);
        if (c == null) {
            return;
        }
         
        String code = c.getElement(0);
        if (StringUtils.isBlank(code)) {
            return;
        }
        
        switch(code) {
        case "TN":
            this.referenceCodeQualifier = RffCode.TRANSACTION_REF_NUMBER;
            break;
        case "AVF":
            this.referenceCodeQualifier = RffCode.RESERVATION_REF_NUMBER;
            break;
        case "ABO":
            this.referenceCodeQualifier = RffCode.PASSENGER_REF_NUMBER;
            break;
        case "SEA":
            this.referenceCodeQualifier = RffCode.ASSIGNED_SEAT;
            break;
        case "AEA":
            this.referenceCodeQualifier = RffCode.GOVT_AGENCY_REF_NUMBER;
            break;
        case "CR":
            this.referenceCodeQualifier = RffCode.CUSTOMER_REF_NUMBER;
            break;
        }
            
        this.referenceIdentifier = c.getElement(1);
        if (c.numElements() >= 5) {
            this.revisionIdentifier = Integer.valueOf(c.getElement(4));
        }
    }

    public RffCode getReferenceCodeQualifier() {
        return referenceCodeQualifier;
    }

    public String getReferenceIdentifier() {
        return referenceIdentifier;
    }

    public int getRevisionIdentifier() {
        return revisionIdentifier;
    }
}
