/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

public class DomainModelConstants {
	// ENTITY
	public static final int ENTITY_NAME_SIZE = 20;

	// User
	public static final int GTAS_USERID_COLUMN_SIZE = 255;

	// Knowledge Base
	public static final String KB_UNIQUE_CONSTRAINT_NAME = "KB_UNIQUE_NAME";

	// UDR
	public static final String UDR_UNIQUE_CONSTRAINT_NAME = "UDR_UNIQUE_AUTHOR_TITLE";

	// Watch list
	public static final String WL_UNIQUE_CONSTRAINT_NAME = "WL_UNIQUE_NAME";

	public static final int WL_NAME_COLUMN_SIZE = 64;
	public static final int WL_ITEM_DATA_COLUMN_SIZE = 1024;
	public static final int WL_RULE_DATA_COLUMN_SIZE = 1024;
}
