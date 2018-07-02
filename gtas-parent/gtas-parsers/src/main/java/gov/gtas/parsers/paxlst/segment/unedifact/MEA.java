/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * MEA MEASUREMENTS - GR. 4
 * <p>
 * Function: To specify physical measurements. This segment used to report
 * number of Checked Bags.
 */
public class MEA extends Segment {
	
	/**
	 * Measurement Unit Code 
	 */
	public enum MeasurementUnitCode {
		KGM, LBR
	}

	/**
	 * MEASUREMENT PURPOSE CODE QUALIFIER
	 */
	public enum MeasurementCodeQualifier {
		CT, WT
	}

	private String numBags;
	private String bagWeight;
	private MeasurementUnitCode weightUnit;
	
	/**
	 * Used to indicate at least one segment of MEA exist in the APIS message, applies only to v.16b APIS message
	 */
	private boolean segmentIncludedInAPISMessage;
	
	
	private MeasurementCodeQualifier code;


	public MEA(List<Composite> composites) {
		super(MEA.class.getSimpleName(), composites);
		this.setSegmentIncludedInAPISMessage(false);
		for (int i = 0; i < numComposites(); i++) {

			// v.16b APIS message
			this.setSegmentIncludedInAPISMessage(true);

			Composite c = getComposite(i);
			
			switch (i) {
			case 0:
				// PURPOSE CODE QUALIFIER
				this.code = MeasurementCodeQualifier.valueOf(c.getElement(i));
				break;
			case 1:
				//
				break;
			case 2:
				// Measurement Unit Code: Measure
				switch (code) {
				case CT:
					this.setNumBags(c.getElement(0)); // number of bags
					break;
				case WT:
					this.setBagWeight(c.getElement(1)); //Total weight
					this.setWeightUnit(MeasurementUnitCode.valueOf(c.getElement(0))); // weight unit
					break;
				}
				break;
			}


		}

	}

	public String getNumBags() {
		return numBags;
	}

	public void setNumBags(String numBags) {
		this.numBags = numBags;
	}

	public String getBagWeight() {
		return bagWeight;
	}

	public void setBagWeight(String bagWeight) {
		this.bagWeight = bagWeight;
	}

	public MeasurementUnitCode getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(MeasurementUnitCode weightUnit) {
		this.weightUnit = weightUnit;
	}

	public MeasurementCodeQualifier getCode() {
		return code;
	}

	public void setCode(MeasurementCodeQualifier code) {
		this.code = code;
	}

	public boolean isSegmentIncludedInAPISMessage() {
		return segmentIncludedInAPISMessage;
	}

	public void setSegmentIncludedInAPISMessage(boolean segmentIncludedInAPISMessage) {
		this.segmentIncludedInAPISMessage = segmentIncludedInAPISMessage;
	}
	
}
