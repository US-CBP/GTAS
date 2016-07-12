/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class Pnr TravelAgency Link.
 */
public class PnrTravelAgencyLink extends PnrAttributeLink {
	private static final long serialVersionUID = 5014564222013081462L;

	/**
	 * Instantiates a new pnr travel agency link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param travelAgencyId
	 *            the travel agency id
	 */
	public PnrTravelAgencyLink(final long pnrId, final long travelAgencyId) {
		super(pnrId, travelAgencyId);
	}

	/**
	 * property access.
	 * 
	 * @return address ID.
	 */
	public long getTravelAgencyId() {
		return super.getLinkAttributeId();
	}
}
