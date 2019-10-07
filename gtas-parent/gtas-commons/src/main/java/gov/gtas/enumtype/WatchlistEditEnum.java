/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

import org.apache.commons.lang3.StringUtils;

public enum WatchlistEditEnum {
	C("Create"),

	U("Update"),

	D("Delete");

	private String operationName;

	private WatchlistEditEnum(String opName) {
		this.operationName = opName;
	}

	/**
	 * @return the operationName
	 */
	public String getOperationName() {
		return operationName;
	}

	/**
	 * Converts operation name to the enum value.
	 * 
	 * @param opName
	 *            the operation name.
	 * @return the enum.
	 */
	public static WatchlistEditEnum getEditEnumForOperationName(String opName) {
		if (!StringUtils.isEmpty(opName)) {
			for (WatchlistEditEnum en : WatchlistEditEnum.values()) {
				if (opName.equalsIgnoreCase(en.getOperationName())) {
					return en;
				}
			}
		}
		throw new IllegalArgumentException(
				"WatchlistEditEnum.getEditEnumForOperationName() - Unknown operation:" + opName);
	}
}
