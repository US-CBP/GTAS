/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.ParseUtils;

/**
 * <p>
 * FOP: FORM OF PAYMENT
 * <p>
 * Class FOP to hold Form of Payment details for a ticket
 * <p>
 * If payment is via credit card, then the provision of the cardholder name is
 * via the IFT if different from the passenger.
 * <p>
 * Ex:
 * <ul>
 * <li>Paid with an American Express card, with an expiration date of
 * 12/11(FOP+CC::416.00:AX:373212341234123:1211’)
 * <li>Form of payment is cash.(FOP+CA::731.00')
 * <li>Form of payment is Government receipt(FOP+GR::200.00::AB123456')
 * <li>Old form of payment was VISA card with an expiration date of August,
 * 2013(FOP+CC:2:628.32:VI:4235792300387826:0813’)
 * </ul>
 */
public class FOP extends Segment {
    private static final String CREDIT_CARD_TYPE = "CC";
    private static final String CASH_TYPE = "CA";
    private static final String MISC_TYPE = "MS";
    
    public class Payment {
        private String paymentType;
        private String paymentAmount;
        private String vendorCode;
        private String accountNumber;
        private Date expirationDate;
        private boolean isCreditCard;
        private boolean isCash;
        private boolean isMiscelleneous;
        
        public String getPaymentType() {
            return paymentType;
        }
        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }
        public String getPaymentAmount() {
            return paymentAmount;
        }
        public void setPaymentAmount(String paymentAmount) {
            this.paymentAmount = paymentAmount;
        }
        public String getVendorCode() {
            return vendorCode;
        }
        public void setVendorCode(String vendorCode) {
            this.vendorCode = vendorCode;
        }
        public String getAccountNumber() {
            return accountNumber;
        }
        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }
        public Date getExpirationDate() {
            return expirationDate;
        }
        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }
        public boolean isCreditCard() {
            return isCreditCard;
        }
        public void setCreditCard(boolean isCreditCard) {
            this.isCreditCard = isCreditCard;
        }
        public boolean isCash() {
            return isCash;
        }
        public void setCash(boolean isCash) {
            this.isCash = isCash;
        }
		public boolean isMiscelleneous() {
			return isMiscelleneous;
		}
		public void setMiscelleneous(boolean isMiscelleneous) {
			this.isMiscelleneous = isMiscelleneous;
		}
        
    }
    
    private List<Payment> payments = new ArrayList<>();

    public FOP(List<Composite> composites) throws ParseException {
        super(FOP.class.getSimpleName(), composites);
        
        for (Composite c : composites) {
            Payment p = new Payment();
            this.payments.add(p);
            
            p.paymentType = c.getElement(0);
            if (CREDIT_CARD_TYPE.equals(p.paymentType)) {
                p.isCreditCard = true;
                p.vendorCode = c.getElement(3);
                p.accountNumber = c.getElement(4);
                String d = c.getElement(5);
                if (d != null) {
                	//Credit Card Holder #353 code fix
                    p.expirationDate = ParseUtils.parseDateTime(d, "MMyy"); 
                }
            } else if (CASH_TYPE.equals(p.paymentType)) {
                p.isCash = true;
            }
            else if(MISC_TYPE.equals(p.paymentType)){
            	p.isMiscelleneous = true;
            	if(c.numElements() > 1){
            		if(c.getElement(1) != null && CREDIT_CARD_TYPE.equals(c.getElement(1) )){
            			//FOP+MS+CC:::VI:XXXXXX3842:0818'
            			Payment p1 = new Payment();
            			p1.paymentType = c.getElement(1);
            			p1.isCreditCard = true;
                        p1.vendorCode = c.getElement(4);
                        p1.accountNumber = c.getElement(5);
                        String d1 = c.getElement(6);
                        if (d1 != null) {
                        	//Credit Card Holder #353 code fix
                            p1.expirationDate = ParseUtils.parseDateTime(d1, "MMyy"); 
                        }
                        this.payments.add(p1);
            		}
            		p.vendorCode = c.getElement(3);
            	}
            	
            	//FOP+MS'
            	
            }
            p.paymentAmount = c.getElement(2);
        }
    }

    public List<Payment> getPayments() {
        return payments;
    }
}
