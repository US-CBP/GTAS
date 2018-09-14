/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class Pnr BookingDetail Link.
 */
public class PnrBookingDetailLink extends PnrAttributeLink {
	private static final long serialVersionUID = -1157351677154533276L;

	/**
	 * Instantiates a new pnr booking detail link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param bookingDetailId
	 *            the booking detail id
	 */
	public PnrBookingDetailLink(final long pnrId, final long bookingDetailId) {
		super(pnrId, bookingDetailId);
	}

	/**
	 * property access.
	 * 
	 * @return bookingDetail ID.
	 */
	public long getBookingDetailId() {
		return super.getLinkAttributeId();
	}
}
