/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Edifact Composite class. Consists of zero or more string elements.
 * 
 * @see Segment
 */
public final class Composite {
	private final List<String> elements;

	public Composite(List<String> elements) {
		if (CollectionUtils.isNotEmpty(elements)) {
			this.elements = elements;
		} else {
			this.elements = new ArrayList<>();
		}
	}

	/**
	 * Convenience method that returns element at index 'index'. Will return null
	 * for any index > num elements.
	 */
	public String getElement(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException();
		} else if (index >= numElements()) {
			return null;
		} else {
			return this.elements.get(index);
		}
	}

	public List<String> getElements() {
		return elements;
	}

	public int numElements() {
		return this.elements.size();
	}

	public boolean hasPopulatedElements() {
		return this.numElements() > 1 || StringUtils.isNotBlank(this.getElement(0));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
