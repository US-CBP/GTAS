/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

/**
 * The Class Pnr CreditCard Link.
 */
public class PnrCreditCardLink extends PnrAttributeLink {
	private static final long serialVersionUID = -1157351677154533276L;

	/**
	 * Instantiates a new pnr credit card link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param creditCardId
	 *            the credit card id
	 */
	public PnrCreditCardLink(final long pnrId, final long creditCardId) {
		super(pnrId, creditCardId);
	}

	/**
	 * property access.
	 * 
	 * @return creditCard ID.
	 */
	public long getCreditCardId() {
		return super.getLinkAttributeId();
	}
}
