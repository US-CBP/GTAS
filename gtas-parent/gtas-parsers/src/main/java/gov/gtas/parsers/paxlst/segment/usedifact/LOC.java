/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.usedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

public class LOC extends Segment {
    public enum LocCode {
        DEPARTURE,
        ARRIVAL
    }
    
    private LocCode locationCode;
    private String iataCountryCode;
    private String iataAirportCode;
    private String c_codeListIdentifier;

    public LOC(List<Composite> composites) {
        super(LOC.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                switch (c.getElement(0)) {
                case "005":
                    this.locationCode = LocCode.DEPARTURE;
                    break;
                case "008":
                    this.locationCode = LocCode.ARRIVAL;
                    break;
                default:
                    logger.error("unknown location code: " + c.getElement(0));
                    return;
                }
                break;
            
            case 1:
                // Two-character Country code (IATA), followed by a 3-character Airport code (IATA)
                String code = c.getElement(0);
                if (code != null) {
                    this.iataCountryCode = code.substring(0, 2);
                    this.iataAirportCode = code.substring(2, code.length());
                }                
                this.c_codeListIdentifier = c.getElement(1);
                break;
            }
        }
    }

    public LocCode getLocationCode() {
        return locationCode;
    }

    public String getIataCountryCode() {
        return iataCountryCode;
    }

    public String getIataAirportCode() {
        return iataAirportCode;
    }

    public String getC_codeListIdentifier() {
        return c_codeListIdentifier;
    }
}
