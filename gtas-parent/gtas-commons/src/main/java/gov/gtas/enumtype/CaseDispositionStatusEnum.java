
/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.enumtype;

public enum CaseDispositionStatusEnum {

	NEW("NEW"),

	OPEN("OPEN"),

	CLOSED("CLOSED"),

	REOPEN("RE-OPEN"),

	PENDINGCLOSURE("PENDING CLOSURE");

	private String type;

	private CaseDispositionStatusEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static CaseDispositionStatusEnum getEnum(String value) {

		for (CaseDispositionStatusEnum caseDispositionStatusEnum : CaseDispositionStatusEnum.values()) {
			if (caseDispositionStatusEnum.name().equalsIgnoreCase(value)) {
				return caseDispositionStatusEnum;
			}
		}

		throw new IllegalArgumentException();
	}

}
