/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst.segment.unedifact;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * COM: COMMUNICATION CONTACT
 * <p>
 * Function: To specify the communication number(s) of the person responsible
 * for the message content. Up to 3 communication numbers can be provided.
 * <p>
 * Example: COM+202 628 9292:TE+202 628 4998:FX+davidsonr.at.iata.org:EM’
 */
public class COM extends Segment {
	private String phoneNumber;
	private String faxNumber;
	private String email;

	public COM(List<Composite> composites) {
		super(COM.class.getSimpleName(), composites);
		for (int i = 0; i < numComposites(); i++) {
			Composite c = getComposite(i);
			String type = c.getElement(1);
			if (type == null) {
				continue;
			}

			switch (type) {
			case "TE":
				this.phoneNumber = c.getElement(0);
				break;
			case "FX":
				this.faxNumber = c.getElement(0);
				break;
			case "EM":
				this.email = c.getElement(0);
				break;
			}
		}
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public String getEmail() {
		return email;
	}
}
