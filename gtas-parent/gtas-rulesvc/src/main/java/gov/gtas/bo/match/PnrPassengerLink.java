/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class Pnr Passenger Link.
 */
public class PnrPassengerLink extends PnrAttributeLink {
	private static final long serialVersionUID = 7360778064356140448L;

	/**
	 * Instantiates a new pnr passenger link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param passengerId
	 *            the passenger id
	 */
	public PnrPassengerLink(final long pnrId, final long passengerId) {
		super(pnrId, passengerId);
	}

	/**
	 * property access.
	 * 
	 * @return passenger ID.
	 */
	public long getPassengerId() {
		return super.getLinkAttributeId();
	}
}
