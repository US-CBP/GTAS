/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.bo.match;

public class PnrFormOfPaymentLink extends PnrAttributeLink {
	private static final long serialVersionUID = 836077675240448L;

	public PnrFormOfPaymentLink(final long pnrId, final long formOfPaymentId) {
		super(pnrId, formOfPaymentId);
	}

	public long getFormOfPaymentId() {
		return super.getLinkAttributeId();
	}

}
