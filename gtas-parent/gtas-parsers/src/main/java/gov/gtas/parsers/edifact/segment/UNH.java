/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * UNH: MESSAGE HEADER
 * <p>
 * A service segment starting and uniquely identifying a message. This tends to
 * vary for different message types, but generally they all contain message type
 * and version, which is all we store here.
 * <p>
 * Example: UNH+MSG001+PAXLST:D:12B:UN:IATA
 */
public class UNH extends Segment {
	private String messageReferenceNumber;
	private String messageType;
	private String messageTypeVersion;
	private String messageTypeReleaseNumber;

	public UNH(List<Composite> composites) {
		super(UNH.class.getSimpleName(), composites);
		for (int i = 0; i < numComposites(); i++) {
			Composite c = getComposite(i);
			switch (i) {
			case 0:
				this.messageReferenceNumber = c.getElement(0);
				break;
			case 1:
				this.messageType = c.getElement(0);
				this.messageTypeVersion = c.getElement(1);
				this.messageTypeReleaseNumber = c.getElement(2);
				break;
			}
		}
	}

	public String getMessageReferenceNumber() {
		return messageReferenceNumber;
	}

	public String getMessageType() {
		return messageType;
	}

	public String getMessageTypeVersion() {
		return messageTypeVersion;
	}

	public String getMessageTypeReleaseNumber() {
		return messageTypeReleaseNumber;
	}
}
