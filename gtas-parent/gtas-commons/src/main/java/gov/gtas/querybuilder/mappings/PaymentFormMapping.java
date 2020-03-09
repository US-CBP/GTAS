/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum PaymentFormMapping implements IEntityMapping {

	PAYMENT_AMOUNT("wholeDollarAmount", "Whole Monetary Amount", TypeEnum.INTEGER.getType()),

	FORM_OF_PAYMENT("paymentType", "Payment Type", TypeEnum.STRING.getType());

	private String fieldName;
	private String friendlyName;
	private String fieldType;
	private boolean displayField;

	PaymentFormMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
		this.fieldName = fieldName;
		this.friendlyName = friendlyName;
		this.fieldType = fieldType;
		this.displayField = displayField;
	}

	PaymentFormMapping(String fieldName, String friendlyName, String fieldType) {
		this(fieldName, friendlyName, fieldType, true);
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @return the displayField
	 */
	public boolean isDisplayField() {
		return displayField;
	}

}
