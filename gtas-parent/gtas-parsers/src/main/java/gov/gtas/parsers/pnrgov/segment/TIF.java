/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
 * TIF: TRAVELLER INFORMATION
 * <p>
 * To specify a traveller(s) and personal details relating to the traveller(s).
 * <p>
 * Only one surname and given name should be sent in one occurrence of the TIF
 * even if there are multiple names for a surname in the PNR. The Traveller
 * Reference Number (9944) is assigned by the sending system and this number in
 * Gr.2 may be used to cross reference an SSR in Gr.1 or Gr.5 or a TRI in Gr.7.
 * <p>
 * Examples: Passenger Jones/John Mr is an adult.(TIF+JONES+JOHNMR:A') Passenger
 * has a single letter family name – Miss Moan Y – single letter is doubled
 * where MoanMiss was considered the given name. This rule is as defined in
 * AIRIMP rules and its examples.(TIF+YY+MOANMISS:A’)
 * <p>
 * Adult passenger has a single letter family name – Miss Tuyetmai Van A – all
 * given names are combined with the single letter surname where Miss was
 * considered the given name. This rule is as defined in AIRIMP rules and its
 * examples.(TIF+ATUYETMAIVAN+MISS:A’)
 * <p>
 * The PNR is for a group booking with no individual names.(TIF+SEETHE WORLD:G’)
 * <p>
 * Infant no seat Passenger(TIF+RUITER+MISTY:IN’)
 */
public class TIF extends Segment {
    private String travelerSurname;
    private String travelerNameQualifier;

    public class TravelerDetails {
        private String travelerGivenName;
        private String travelerType;

        /**
         * Used as a cross reference between data segments. In GR2 must be
         * unique per passenger
         */
        private String travelerReferenceNumber;
        private String accompaniedBy;
        public String getTravelerGivenName() {
            return travelerGivenName;
        }
        public void setTravelerGivenName(String travelerGivenName) {
            this.travelerGivenName = travelerGivenName;
        }
        public String getTravelerType() {
            return travelerType;
        }
        public void setTravelerType(String travelerType) {
            this.travelerType = travelerType;
        }
        public String getTravelerReferenceNumber() {
            return travelerReferenceNumber;
        }
        public void setTravelerReferenceNumber(String travelerReferenceNumber) {
            this.travelerReferenceNumber = travelerReferenceNumber;
        }
        public String getAccompaniedBy() {
            return accompaniedBy;
        }
        public void setAccompaniedBy(String accompaniedBy) {
            this.accompaniedBy = accompaniedBy;
        }
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
        }        
    }
    
    private List<TravelerDetails> travelerDetails = new ArrayList<>();

    public TIF(List<Composite> composites) {
        super(TIF.class.getSimpleName(), composites);

        Composite c = getComposite(0);
        if (c != null) {
            this.travelerSurname = c.getElement(0);
            this.travelerNameQualifier = c.getElement(1);
        }
        
        for (int i = 1; i < numComposites(); i++) {
            c = getComposite(i);
            TravelerDetails d = new TravelerDetails();
            d.setTravelerGivenName(c.getElement(0));
            d.setTravelerType(c.getElement(1));
            d.setTravelerReferenceNumber(c.getElement(2));
            d.setAccompaniedBy(c.getElement(3));
            this.travelerDetails.add(d);
        }
    }

    public String getTravelerSurname() {
        return travelerSurname;
    }

    public String getTravelerNameQualifier() {
        return travelerNameQualifier;
    }

    public List<TravelerDetails> getTravelerDetails() {
        return travelerDetails;
    }
}
