/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * SSR: SPECIAL REQUIREMENTS DETAILS 
 * <p>
 * SSR’s in GR.1 apply to all flights and may apply to all passengers or may
 * apply to specific passenger based on the traveler reference number in
 * SSR/9944 and TIF/9944.
 * <p>
 * SSR’s in GR.2 apply to the specific passenger.
 * <p>
 * SSR’s in GR.5 (per TVL) apply to a specific flight and may apply to all
 * passengers or may apply to a specific passenger based on the traveler
 * reference number in SSR/9944 and TIF/9944.
 * SSR+CTCE:HK:1:UA:::::/FIRSTNAMELASTNAME//YAHOO.COM+::001
 * SSR+CTCE:HK:001:YY:::::USERNAME..//HOTMAIL.COM-1FIRSTNAMELASTNAME'
 * 
 */
public class SSR extends Segment {
    public static final String DOCS = "DOCS";
    public static final String DOCA = "DOCA";
    public static final String DOCO = "DOCO";
    public static final String SEAT = "SEAT";
    public static final String CTCE="CTCE";
    private String typeOfRequest;
    private String action;
    private String quantity;
    private String carrier;
    private String boardCity;
    private String offCity;
    private String freeText;

    public class SpecialRequirementDetails {
        private String specialRequirementData;
        private String unit;
        private String travelerReferenceNumber;
        private String seatCharacteristic;
        
        public String getSpecialRequirementData() {
            return specialRequirementData;
        }
        public void setSpecialRequirementData(String specialRequirementData) {
            this.specialRequirementData = specialRequirementData;
        }
        public String getUnit() {
            return unit;
        }
        public void setUnit(String unit) {
            this.unit = unit;
        }
        public String getTravelerReferenceNumber() {
            return travelerReferenceNumber;
        }
        public void setTravelerReferenceNumber(String travelerReferenceNumber) {
            this.travelerReferenceNumber = travelerReferenceNumber;
        }
        public String getSeatCharacteristic() {
            return seatCharacteristic;
        }
        public void setSeatCharacteristic(String seatCharacteristic) {
            this.seatCharacteristic = seatCharacteristic;
        }
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
        }
    }
    
    private List<SpecialRequirementDetails> details = new ArrayList<>();
    
    public SSR(List<Composite> composites) {
        super(SSR.class.getSimpleName(), composites);
        
        Composite c = getComposite(0);
        if (c != null) {
            this.typeOfRequest = c.getElement(0);
            this.action = c.getElement(1);
            this.quantity = c.getElement(2);
            this.carrier = c.getElement(3);
            // indices 4, 5 processing indicators
            this.boardCity = c.getElement(6);
            this.offCity = c.getElement(7);
    
            StringBuffer b = new StringBuffer();
            for (int i=8; i<c.numElements(); i++) {
                b.append(c.getElement(i));
            }
            if (b.length() != 0) {
                this.freeText = b.toString();
            }
        }
        
        for (int i=1; i<getComposites().size(); i++) {
            c = getComposite(i);
            if (c != null) {
                SpecialRequirementDetails d = new SpecialRequirementDetails();
                d.setSpecialRequirementData(c.getElement(0));
                d.setUnit(c.getElement(1));
                d.setTravelerReferenceNumber(c.getElement(2));
                d.setSeatCharacteristic(c.getElement(3));
                this.details.add(d);
            }
        }
    }

    public String getTypeOfRequest() {
        return typeOfRequest;
    }

    public String getAction() {
        return action;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getBoardCity() {
        return boardCity;
    }

    public String getOffCity() {
        return offCity;
    }

    public String getFreeText() {
        return freeText;
    }

    public List<SpecialRequirementDetails> getDetails() {
        return details;
    }
}
