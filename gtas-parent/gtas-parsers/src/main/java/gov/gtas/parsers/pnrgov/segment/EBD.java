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
 * EBD: EXCESS BAGGAGE DETAILS
 * <p>
 * Excess Baggage Details of a passenger Used to send paid baggage information
 * Ex:One piece of baggage over the allowance USD 50 (EBD+USD:50.00+1::N’)
 */
public class EBD extends Segment {
	private String currencyCode;
	private String ratePerUnit;
	private String numberInExcess;
	private String pieceOrWeight;
	private String kgsOrPounds;

	public EBD(List<Composite> composites) {
		super(EBD.class.getSimpleName(), composites);

		for (int i = 0; i < numComposites(); i++) {
			Composite c = getComposite(i);
			switch (i) {
			case 0:
				this.currencyCode = c.getElement(0);
				this.ratePerUnit = c.getElement(1);
				break;
			case 1:
				// TODO: technically this composite can be repeated up to 3x
				this.numberInExcess = c.getElement(0);
				this.pieceOrWeight = c.getElement(1);
				this.kgsOrPounds = c.getElement(2);
				break;
			}
		}
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public String getRatePerUnit() {
		return ratePerUnit;
	}

	public String getNumberInExcess() {
		return numberInExcess;
	}

	public String getPieceOrWeight() {
		return pieceOrWeight;
	}

	public String getKgsOrPounds() {
		return kgsOrPounds;
	}
}
