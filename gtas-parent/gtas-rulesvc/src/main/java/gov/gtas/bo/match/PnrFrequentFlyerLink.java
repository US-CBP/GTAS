/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class Pnr FrequentFlyer Link.
 */
public class PnrFrequentFlyerLink extends PnrAttributeLink {

	/** serial version UID. */
	private static final long serialVersionUID = 6781453461421106156L;

	/**
	 * Instantiates a new pnr frequent flyer link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param frequentFlierId
	 *            the frequent flier id
	 */
	public PnrFrequentFlyerLink(final long pnrId, final long frequentFlierId) {
		super(pnrId, frequentFlierId);
	}

	/**
	 * property access.
	 * 
	 * @return frequent flier ID.
	 */
	public long getFrequentFlierId() {
		return super.getLinkAttributeId();
	}
}
