/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
	public static final String CTCT="CTCT";
	public static final String CTC="CTC";
	public static final String APM="APM";
	public static final String FP="F/FP";
    private String theText;
    private boolean isAgency=false;
    private boolean isPhone=false;
    private boolean isContact=false;
    private boolean isFormPayment=false;
    private boolean isCashPayment=false;
    
    public LTS(List<Composite> composites) {
        super(LTS.class.getSimpleName(), composites);
        Composite c = getComposite(0);
         if (c != null) {
            this.theText = c.getElement(0);
            if(StringUtils.isNotBlank(theText)){
            	if(theText.contains(CTCT)){
            		isAgency=true;
            	}
            	else if(theText.contains(CTC)){
            		isContact=true;
            	}
            	else if(theText.contains(APM)){
            		isPhone=true;
            	}
               	else if(theText.contains(FP)){
               		isFormPayment=true;
               		for (int i=1; i<getComposites().size(); i++) {
               		 c = getComposite(i);
               		 	if (c != null) {
               		 		if(theText.contains("CASH")){
               		 			isCashPayment=true;
               		 		    theText=c.getElement(0);
               		 		}
               		 	}
               		}
            	}
            }
        }
    }

    public boolean isAgency() {
		return isAgency;
	}

	public void setAgency(boolean isAgency) {
		this.isAgency = isAgency;
	}

	public boolean isPhone() {
		return isPhone;
	}

	public void setPhone(boolean isPhone) {
		this.isPhone = isPhone;
	}

	public boolean isContact() {
		return isContact;
	}

	public void setContact(boolean isContact) {
		this.isContact = isContact;
	}

	public void setTheText(String theText) {
		this.theText = theText;
	}

	public String getTheText() {
        return theText;
    }

	public boolean isFormPayment() {
		return isFormPayment;
	}

	public void setFormPayment(boolean isFormPayment) {
		this.isFormPayment = isFormPayment;
	}

	public boolean isCashPayment() {
		return isCashPayment;
	}

	public void setCashPayment(boolean isCashPayment) {
		this.isCashPayment = isCashPayment;
	}
	
}
