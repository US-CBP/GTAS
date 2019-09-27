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
 * FTI: FREQUENT TRAVELLER INFORMATION
 */
public class FTI extends Segment {
	public class FrequentFlierDetails {
		private String airlineCode;
		private String freqTravelerNumber;
		private String travelerReferenceNumber;

		public String getAirlineCode() {
			return airlineCode;
		}

		public void setAirlineCode(String airlineCode) {
			this.airlineCode = airlineCode;
		}

		public String getFreqTravelerNumber() {
			return freqTravelerNumber;
		}

		public void setFreqTravelerNumber(String freqTravelerNumber) {
			this.freqTravelerNumber = freqTravelerNumber;
		}

		public String getTravelerReferenceNumber() {
			return travelerReferenceNumber;
		}

		public void setTravelerReferenceNumber(String travelerReferenceNumber) {
			this.travelerReferenceNumber = travelerReferenceNumber;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
	}

	private List<FrequentFlierDetails> frequentFlierInfo = new ArrayList<>();

	public FTI(List<Composite> composites) {
		super(FTI.class.getSimpleName(), composites);

		for (Composite c : getComposites()) {
			FrequentFlierDetails d = new FrequentFlierDetails();
			d.setAirlineCode(c.getElement(0));
			d.setFreqTravelerNumber(c.getElement(1));
			d.setTravelerReferenceNumber(c.getElement(2));
			frequentFlierInfo.add(d);
		}
	}

	public List<FrequentFlierDetails> getFrequentFlierInfo() {
		return frequentFlierInfo;
	}
}
