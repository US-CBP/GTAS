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
 * MON: MONETARY INFORMATION
 * <p>
 * To specify ticket amount
 * <p>
 * Ex .Ticket/document amount is $0.00 due to an award
 * certificate.(MON+T:AWARD') Ticket/document amount is 297.50
 * EUR.(MON+T:297.50:EURâ€™)
 * MON+T:AWARD'
 * MON+T:297.50:EUR’
 * MON+B:999.00:SGD+T:1999.99:SGD'
 * 
 */
public class MON extends Segment {
	public static final String BASE = "B";
	public static final String TOTAL = "T";
	private String paymentAmount;
	private String currencyCode;
	
    public MON(List<Composite> composites) {
        super(MON.class.getSimpleName(), composites);
        for (int i=1; i<getComposites().size(); i++) {
        	Composite c  = getComposite(i);
        	if (c != null && c.numElements() == 2 && TOTAL.equals(c.getElement(0))) {
        		paymentAmount="0";
        	}
        	if (c != null && c.numElements() == 3 && TOTAL.equals(c.getElement(0))) {
        		paymentAmount=c.getElement(1);
        		currencyCode=c.getElement(2);
        	}
        }
    }

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
    
    
}
