/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;

public class LOC extends Segment {
    public enum LocCode {
        // DTM
        DEPARTURE_AIRPORT,
        ARRIVAL_AIRPORT,
        BOTH_DEPARTURE_AND_ARRIVAL_AIRPORT,
        FINAL_DESTINATION,
        FILING_LOCATION,
        REPORTING_LOCATION,
        GATE_PASS_LOCATION,
        
        // NAD
        AIRPORT_OF_FIRST_US_ARRIVAL,
        COUNTRY_OF_RESIDENCE,
        PORT_OF_EMBARKATION,
        PORT_OF_DEBARKATION,
        PLACE_OF_BIRTH,
        
        // DOC
        PLACE_OF_DOCUMENT_ISSUE
    }
    
    private LocCode functionCode;
    private String locationNameCode;
    private String firstRelatedLocationName;
    private String secondRelatedLocationName;
    
    public LOC(List<Composite> composites) throws ParseException {
        super(LOC.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            
            switch (i) {
            case 0:
                switch(Integer.valueOf(c.getElement(0))) {
                case 125:
                    this.functionCode = LocCode.DEPARTURE_AIRPORT;
                    break;                    
                case 87:
                    this.functionCode = LocCode.ARRIVAL_AIRPORT;
                    break;                    
                case 92:
                    this.functionCode = LocCode.BOTH_DEPARTURE_AND_ARRIVAL_AIRPORT;
                    break;                   
                case 130:
                    this.functionCode = LocCode.FINAL_DESTINATION;
                    break;                   
                case 188:
                    this.functionCode = LocCode.FILING_LOCATION;
                    break;                    
                case 172:
                    this.functionCode = LocCode.REPORTING_LOCATION;
                    break;                    
                // ambiguous?
//                case 91:
//                    this.functionCode = LocCode.GATE_PASS_LOCATION;
//                    break;
                    
                case 22:
                    this.functionCode = LocCode.AIRPORT_OF_FIRST_US_ARRIVAL;
                    break;
                case 174:
                    this.functionCode = LocCode.COUNTRY_OF_RESIDENCE;
                    break;
                case 178:
                    this.functionCode = LocCode.PORT_OF_EMBARKATION;
                    break;
                case 179:
                    this.functionCode = LocCode.PORT_OF_DEBARKATION;
                    break;
                case 180:
                    this.functionCode = LocCode.PLACE_OF_BIRTH;
                    break;
                    
                case 91:
                    this.functionCode = LocCode.PLACE_OF_DOCUMENT_ISSUE;
                    break;
                default:
                    throw new ParseException("LOC: invalid party function code: " + c.getElement(0));                    
                }
                break;

            case 1:
                if (c.numElements() == 1) {
                    // LOC+174+CAN'
                    this.locationNameCode = c.getElement(0);
                } else {
                    // LOC+180+:::AMBER HILL GBR'
                    // TODO: in this case set a flag indicating it's not a country code
                    this.locationNameCode = c.getElement(3);
                }
                break;
                
            case 2:
                this.firstRelatedLocationName = c.getElement(3);
                break;

            case 3:
                this.secondRelatedLocationName = c.getElement(3);
                break;
            }
        }
    }

    public LocCode getFunctionCode() {
        return functionCode;
    }

    public String getLocationNameCode() {
        return locationNameCode;
    }

    public String getFirstRelatedLocationName() {
        return firstRelatedLocationName;
    }

    public String getSecondRelatedLocationName() {
        return secondRelatedLocationName;
    }
}
