/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class PnrEmailLink.
 */
public class PnrEmailLink extends PnrAttributeLink {
	private static final long serialVersionUID = -4878307401317536141L;

	/**
	 * Instantiates a new pnr email link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param emailId
	 *            the email id
	 */
	public PnrEmailLink(final long pnrId, final long emailId) {
		super(pnrId, emailId);
	}

	/**
	 * property access.
	 * 
	 * @return email ID.
	 */
	public long getEmailId() {
		return super.getLinkAttributeId();
	}
}
