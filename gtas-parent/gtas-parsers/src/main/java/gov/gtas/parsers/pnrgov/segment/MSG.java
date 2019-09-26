/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * MSG: MESSAGE ACTION DETAILS
 * <p>
 * Specifies the function of the message. (Message action details) Value 22
 * means a Full transmission of all PNR data for a flight Value 141 means
 * changed PNRs only.Ex:MSG+:22' or MSG+:141' Business Function, Coded (Element
 * 4025) is only used in the MSG Gr9 of PNRGOV to specify the type of service
 * (car, hotel, train, etc.)
 * 
 * <p>
 * To specify that the TVL is for a hotel segment.(MSG+8') Push PNR data to
 * States(MSG+:22’) To identify a change PNRGOV message(MSG+:141’)
 */
public class MSG extends Segment {
	public enum MsgCode {
		PUSH_PNR("22"), CHANGE_PNR("141");

		private final String code;

		private MsgCode(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		private static final Map<String, MsgCode> BY_CODE_MAP = new LinkedHashMap<>();
		static {
			for (MsgCode rae : MsgCode.values()) {
				BY_CODE_MAP.put(rae.code, rae);
			}
		}

		public static MsgCode forCode(String code) {
			return BY_CODE_MAP.get(code);
		}
	}

	private MsgCode messageTypeCode;

	public MSG(List<Composite> composites) {
		super(MSG.class.getSimpleName(), composites);

		String code = null;
		Composite c = getComposite(0);
		if (c != null) {
			if (c.getElement(0) != null) {
				code = c.getElement(0);
			} else {
				if (c.getElement(1) != null) {
					code = c.getElement(1);
				}
			}
			this.messageTypeCode = MsgCode.forCode(code);
		}
	}

	public MsgCode getMessageTypeCode() {
		return messageTypeCode;
	}
}
