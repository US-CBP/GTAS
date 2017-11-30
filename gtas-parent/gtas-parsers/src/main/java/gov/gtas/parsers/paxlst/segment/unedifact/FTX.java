/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;

/**
 * <p>
 * FTX: FREE TEXT - GR. 4
 * <p>
 * This segment is optional and may be used to report Bag Tag Identification.
 * <p>
 * Examples:
 * <ul>
 * <li>FTX+BAG+++BA987654’ - Single Bag Tag reference
 * <li>FTX+BAG+++AF012345:3’ - Indicates 3 bags checked beginning with a
 * sequential reference of AF012345.
 * </ul>
 */
public class FTX extends Segment {
    public enum FtxCode {
        BAG
    }

    private FtxCode ftxCode;

    /** This element reports the Bag Tag identification reference */
    private String bagId;
    
    //UGANDA SPECIFIC WEIGHT-NOT AVAILABLE IN REGULAR APIS FILE
    private String bagWeight;

    /**
     * Conditional: This element reports a numeric value indicating a sequence
     * of values in a +1 increment beginning with the value in the previous
     * element.
     */
    private String numBags;

    public FTX(List<Composite> composites) throws ParseException {
        super(FTX.class.getSimpleName(), composites);
        for (int i = 0; i < numComposites(); i++) {
            Composite c = getComposite(i);
            switch (i) {
            case 0:
                switch (c.getElement(0)) {
                case "BAG":
                    this.ftxCode = FtxCode.BAG;
                    break;
                }
                break;
            case 1:
                if (this.ftxCode == FtxCode.BAG) {
                    this.bagWeight = c.getElement(0);
                }
                break;
                
            case 3:
                if (this.ftxCode == FtxCode.BAG) {
                    this.bagId = c.getElement(0);
                    this.numBags = c.getElement(1);
                }
                break;
            }
        }
    }

    
    public String getBagWeight() {
		return bagWeight;
	}


	public FtxCode getFtxCode() {
        return ftxCode;
    }

    public String getBagId() {
        return bagId;
    }

    public String getNumBags() {
        return numBags;
    }
}
