/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class Pnr Phone Link.
 */
public class PnrPhoneLink extends PnrAttributeLink {
	private static final long serialVersionUID = -5957136488500600885L;

	/**
	 * Instantiates a new pnr phone link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param phoneId
	 *            the phone id
	 */
	public PnrPhoneLink(final long pnrId, final long phoneId) {
		super(pnrId, phoneId);
	}

	/**
	 * property access.
	 * 
	 * @return phone ID.
	 */
	public long getPhoneId() {
		return super.getLinkAttributeId();
	}
}
