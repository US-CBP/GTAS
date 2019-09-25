/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * EQN: NUMBER OF UNITS
 * 
 * <p>
 * The EQN at level 0 is used to specify the total number of PNRs being sent for
 * the flight push. In case of full PNR push, the total number of PNRs contained
 * in the full PNR push regardless of the number of messages used for the full
 * push. In the case of update PNR push, the total number of PNRs contained in
 * the update PNR push regardless of the number of messages used for the update
 * push should be used.
 * 
 * <p>
 * Example: Total number of PNRs(EQN+98')
 */
public class EQN extends Segment {
	private Integer value;

	public EQN(List<Composite> composites) {
		super(EQN.class.getSimpleName(), composites);
		Composite c = getComposite(0);
		if (c != null) {
			this.value = Integer.valueOf(c.getElement(0));
		}
	}

	public Integer getValue() {
		return value;
	}
}
