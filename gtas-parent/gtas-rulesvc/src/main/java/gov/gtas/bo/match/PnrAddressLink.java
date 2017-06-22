/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class Pnr Address Link.
 */
public class PnrAddressLink extends PnrAttributeLink {

	/** serial version UID. */
	private static final long serialVersionUID = 4794542237529461610L;

	/**
	 * Instantiates a new pnr address link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param addressId
	 *            the address id
	 */
	public PnrAddressLink(final long pnrId, final long addressId) {
		super(pnrId, addressId);
	}

	/**
	 * property access.
	 * 
	 * @return address ID.
	 */
	public long getAddressId() {
		return super.getLinkAttributeId();
	}
}
