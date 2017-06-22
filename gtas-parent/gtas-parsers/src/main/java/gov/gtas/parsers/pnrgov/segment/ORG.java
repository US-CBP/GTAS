/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * ORG: ORIGINATOR OF REQUEST DETAILS
 * <p>
 * The ORG in GR.1 at level 2 is the originator of the booking. For “update”
 * pushes when the push flight/date is cancelled from a PNR or the complete PNR
 * is cancelled or not found, the ORG is sent as an empty segment,i.e., does not
 * contain data.
 * <p>
 * Examples:
 * <ul>
 * <li>The originator of the booking is an LH agent located in Amsterdam hosted
 * on Amadeus.(ORG+1A:MUC+12345678:111111+AMS+LH+A+NL:NLG:NL+0001AASU’)
 * <li>The originator of the booking is an Amadeus travel agent
 * request.(ORG+1A:NCE+12345678:DDGS+++T')
 * </ul>
 */
public class ORG extends Segment {

    private String airlineCode;
    private String locationCode;
                                
    private String travelAgentIdentifier;
    private String reservationSystemCode;
    private String reservationSystemKey;
    
    private String agentLocationCode;
    
    private String companyIdentification;
    private String systemLocationCode;
    
    private String originatorTypeCode;
    private String originatorCountryCode;
    private String originatorCurrencyCode;
    private String originatorLanguageCode;

    public ORG(List<Composite> composites) {
        super("ORG", composites);
        
        for (int i=0; i<numComposites(); i++) {
            Composite c = getComposite(i);
            
            switch (i) {
            case 0:
                this.airlineCode = c.getElement(0);
                this.locationCode = c.getElement(1);
                break;
            case 1:
                this.travelAgentIdentifier = c.getElement(0);
                this.reservationSystemCode = c.getElement(1);
                this.reservationSystemKey = c.getElement(2);
                break;
            case 2:
                this.agentLocationCode = c.getElement(0);
                break;
            case 3:
                this.companyIdentification = c.getElement(0);
                this.systemLocationCode = c.getElement(1);
                break;
            case 4:
                this.originatorTypeCode = c.getElement(0);
                this.originatorCountryCode = c.getElement(1);
                this.originatorCurrencyCode = c.getElement(2);
                this.originatorLanguageCode = c.getElement(3);
                break;
            }
        }
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public String getTravelAgentIdentifier() {
        return travelAgentIdentifier;
    }

    public String getReservationSystemCode() {
        return reservationSystemCode;
    }

    public String getReservationSystemKey() {
        return reservationSystemKey;
    }

    public String getAgentLocationCode() {
        return agentLocationCode;
    }

    public String getCompanyIdentification() {
        return companyIdentification;
    }

    public String getSystemLocationCode() {
        return systemLocationCode;
    }

    public String getOriginatorTypeCode() {
        return originatorTypeCode;
    }

    public String getOriginatorCountryCode() {
        return originatorCountryCode;
    }

    public String getOriginatorCurrencyCode() {
        return originatorCurrencyCode;
    }

    public String getOriginatorLanguageCode() {
        return originatorLanguageCode;
    }
}
