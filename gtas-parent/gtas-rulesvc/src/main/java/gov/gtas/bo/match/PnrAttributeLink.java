/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo.match;

import java.io.Serializable;
import java.util.Objects;

/**
 * The Class Pnr Attribute Link.
 */
public abstract class PnrAttributeLink implements Serializable {
	private static final long serialVersionUID = -7317834427317049240L;

	private long pnrId;
	private long linkAttributeId;

	/**
	 * Instantiates a new pnr attribute link.
	 *
	 * @param pnrId
	 *            the pnr id
	 * @param attributeId
	 *            the attribute id
	 */
	protected PnrAttributeLink(final long pnrId, final long attributeId) {
		this.pnrId = pnrId;
		this.linkAttributeId = attributeId;
	}

	/**
	 * @return the pnrId
	 */
	public long getPnrId() {
		return pnrId;
	}

	/**
	 * @return the childAttributeId
	 */
	public long getLinkAttributeId() {
		return linkAttributeId;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PnrAttributeLink)) return false;
		PnrAttributeLink that = (PnrAttributeLink) o;
		return getPnrId() == that.getPnrId() &&
				getLinkAttributeId() == that.getLinkAttributeId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPnrId(), getLinkAttributeId());
	}
}
