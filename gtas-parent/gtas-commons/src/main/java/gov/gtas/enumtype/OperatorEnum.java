/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

public enum OperatorEnum {

	EQUAL("="),

	EQUALS("="),

	NOT_EQUAL("!="),

	IN("in"),

	NOT_IN("not in"),

	LESS("<"),

	LESS_OR_EQUAL("<="),

	GREATER(">"),

	GREATER_OR_EQUAL(">="),

	BETWEEN("BETWEEN"),

	NOT_BETWEEN("NOT BETWEEN"),

	BEGINS_WITH("LIKE"),

	NOT_BEGINS_WITH("NOT LIKE"),

	CONTAINS("LIKE"),

	NOT_CONTAINS("NOT LIKE"),

	ENDS_WITH("LIKE"),

	NOT_ENDS_WITH("NOT LIKE"),

	IS_EMPTY("IS EMPTY"),

	IS_NOT_EMPTY("IS NOT EMPTY"),

	IS_NULL("IS NULL"),

	IS_NOT_NULL("IS NOT NULL");

	private String operator;

	private OperatorEnum(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public static OperatorEnum getEnum(String value) {

		for (OperatorEnum opEnum : OperatorEnum.values()) {
			if (opEnum.name().equalsIgnoreCase(value)) {
				return opEnum;
			}
		}

		throw new IllegalArgumentException("Illegal operator enum: " + value);
	}

}
