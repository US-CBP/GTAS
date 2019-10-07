/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP)
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Splitter;
import gov.gtas.parsers.edifact.segment.UNA;
import gov.gtas.parsers.pnrgov.PnrUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.InternalHDRPercentiles;

/**
 * <p>
 * TBD: TRAVELER BAGGAGE DETAILS/Electronic Ticketing
 * <p>
 * To specify the baggage details, including number of bags and serial numbers.
 * This segment is for the checked in baggage and not for excess bag details
 * EXAMPLE bags, weight 84 kilos, Head of Pool, tags 4074902824, 3 in sequence
 * to MSP TBD++3:84:700++HP+KL:4074902824:3:MSP' Bag pool members with Head of
 * Pool ticket. TBD+++MP:0741234123456' Total 5 bags, weight 155 pounds, 2
 * checked to MSP, 3 short checked to JFK TBD++5:155:701+++KL: 8074902824:2:MSP+
 * KL: 8074902826:3:JFK
 * <p>
 * 700 unit qualifier is for KGS 701 unit qualifier is for pounds 702 No Unit
 */
public class TBD extends Segment {
	/*
	 * Please refer to spec guide for individual data components of the elements.
	 * Parsing the TBD has 4 data elements, STATUS, CODED (up to 1x) BAGGAGE DETAILS
	 * (up to 2x) BAGGAGE REFERENCE DETAILS (up to 1x) BAGTAG DETAILS (up to 99x)
	 **/

	private String codedStatus;
	private List<TBD_BD> baggageDetails = new ArrayList<>();
	private List<TBD_BagTagDetails> bagTagDetails = new ArrayList<>();
	private String pooledBagIndicator;
	private String baggagePoolReferenceNumber;

	public TBD(List<Composite> composites) {
		super(TBD.class.getSimpleName(), composites);
		final int CODED_STATUS = 0;
		final int BAGGAGE_DETAILS_1 = 1;
		final int BAGGAGE_DETAILS_2 = 2;
		final int BAGGAGE_REFERENCE_DETAILS = 3;
		final int BAG_TAG_DETAILS = 4;

		for (int i = 0; i < composites.size(); i++) {
			switch (i) {
			case CODED_STATUS:
				Composite codeComposite = composites.get(CODED_STATUS);
				codedStatus = codeComposite.getElement(0); // Only 1 element in data element. No parsing required.
				break;
			case BAGGAGE_DETAILS_1:
			case BAGGAGE_DETAILS_2:
				// Baggage details repeats up to two times.
				Composite baggageDetailsComposite = composites.get(i);
				if (baggageDetailsComposite.hasPopulatedElements()) {
					TBD_BD tbd_bd = new TBD_BD(this, baggageDetailsComposite.getElements());
					baggageDetails.add(tbd_bd);
				}
				break;
			case BAGGAGE_REFERENCE_DETAILS:
				Composite bagReferenceComp = composites.get(BAGGAGE_REFERENCE_DETAILS);
				// BAGGAGE REFERENCE DETAILS
				// 0 : Processing indicator, coded
				// 1 : Identify number
				pooledBagIndicator = bagReferenceComp.getElement(0);
				baggagePoolReferenceNumber = bagReferenceComp.getElement(1);
				break;
			// Bag tag details can happen 0-99 times.
			case BAG_TAG_DETAILS:
			default:
				Composite bagTagDetailsComposite = composites.get(i);
				if (bagTagDetailsComposite.hasPopulatedElements()) {
					TBD_BagTagDetails tbd_bagTagDetails = new TBD_BagTagDetails(this,
							bagTagDetailsComposite.getElements());
					bagTagDetails.add(tbd_bagTagDetails);
				}
			}
		}
	}

	public boolean hasBagInformation() {
		return !bagTagDetails.isEmpty();
	}

	public String getCodedStatus() {
		return codedStatus;
	}

	public void setCodedStatus(String codedStatus) {
		this.codedStatus = codedStatus;
	}

	public List<TBD_BD> getBaggageDetails() {
		return baggageDetails;
	}

	public void setBaggageDetails(List<TBD_BD> baggageDetails) {
		this.baggageDetails = baggageDetails;
	}

	public List<TBD_BagTagDetails> getBagTagDetails() {
		return bagTagDetails;
	}

	public void setBagTagDetails(List<TBD_BagTagDetails> bagTagDetails) {
		this.bagTagDetails = bagTagDetails;
	}

	public String getPooledBagIndicator() {
		return pooledBagIndicator;
	}

	public void setPooledBagIndicator(String pooledBagIndicator) {
		this.pooledBagIndicator = pooledBagIndicator;
	}

	public String getBaggagePoolReferenceNumber() {
		return baggagePoolReferenceNumber;
	}

	public void setBaggagePoolReferenceNumber(String baggagePoolReferenceNumber) {
		this.baggagePoolReferenceNumber = baggagePoolReferenceNumber;
	}
}
