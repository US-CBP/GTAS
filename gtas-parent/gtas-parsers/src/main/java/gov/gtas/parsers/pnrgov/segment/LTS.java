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
 * LTS: LONG TEXT STRING
 * <p>
 * Flown segments are to be included in history.
 * <p>
 * Ex:Unstructured PNR history.(LTS+ LAX GS WW D006217 2129Z/09DEC 02961B AS
 * DL1314U 19FEB MCOATL NN/SS1 1130A 105P AS SEAT RS 29F TRAN/TRINH')
 */
public class LTS extends Segment {
    private String theText;

    public LTS(List<Composite> composites) {
        super(LTS.class.getSimpleName(), composites);
        Composite c = getComposite(0);
        if (c != null) {
            this.theText = c.getElement(0);
        }
    }

    public String getTheText() {
        return theText;
    }
}
