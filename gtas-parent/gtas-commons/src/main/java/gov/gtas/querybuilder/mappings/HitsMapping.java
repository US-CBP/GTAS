/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import gov.gtas.enumtype.TypeEnum;

public enum HitsMapping implements IEntityMapping {

	HAS_RULE_HIT("isRuleHit", "Has Rule Hit", TypeEnum.BOOLEAN.getType()),

	HAS_WATCHLIST_HIT("isWatchListHit", "Has Watch List Hit", TypeEnum.BOOLEAN.getType()),

	RULE_ID("id", "Rules Hit Id", TypeEnum.INTEGER.getType());

	private String fieldName;
	private String friendlyName;
	private String fieldType;
	private boolean displayField;

	private HitsMapping(String fieldName, String friendlyName, String fieldType, boolean displayField) {
		this.fieldName = fieldName;
		this.friendlyName = friendlyName;
		this.fieldType = fieldType;
		this.displayField = displayField;
	}

	private HitsMapping(String fieldName, String friendlyName, String fieldType) {
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

	public boolean isDisplayField() {
		return displayField;
	}

}
