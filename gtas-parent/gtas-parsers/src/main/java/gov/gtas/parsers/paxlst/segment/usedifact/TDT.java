/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.usedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

public class TDT extends Segment {
    private String c_flightNumber;
    private String c_modeOfTransport;
    private String c_airlineCode;
    private boolean isCrewOnlyManifest;
    
    public TDT(List<Composite> composites) {
        super(TDT.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 1:
            	String flightNumberWithCarrier = c.getElement(0);
                this.c_flightNumber = flightNumberWithCarrier.substring(2);
                this.c_airlineCode = flightNumberWithCarrier.substring(0, 2);
                               
                if (this.c_flightNumber != null && this.c_flightNumber.endsWith("C")) {
                    isCrewOnlyManifest = true;
                } else {
                    isCrewOnlyManifest = false;
                }
                break;
            case 2:
                this.c_modeOfTransport = c.getElement(0);
                break;
            case 3:
            	// obtain carrier from the flight number in 2nd composite above.
                // this.c_airlineCode = c.getElement(0);
                break;
            }
        }
    }

    public String getC_flightNumber() {
        return c_flightNumber;
    }

    public String getC_modeOfTransport() {
        return c_modeOfTransport;
    }

    public String getC_airlineCode() {
        return c_airlineCode;
    }

    public boolean isCrewOnlyManifest() {
        return isCrewOnlyManifest;
    }
}
