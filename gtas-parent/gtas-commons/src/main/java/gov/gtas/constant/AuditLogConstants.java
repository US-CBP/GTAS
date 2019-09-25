/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

public class AuditLogConstants {
	private AuditLogConstants() {
	}

	// UI dropdown for audit action
	public static final String SHOW_ALL_ACTION = "ALL_ACTIONS";

	// Watchlist logging constants
	public static final String WATCHLIST_LOG_TARGET_PREFIX = "{WLname:";
	public static final String WATCHLIST_LOG_TARGET_SUFFIX = "}";
	public static final String WATCHLIST_LOG_CREATE_MESSAGE = "Watchlist Item created";
	public static final String WATCHLIST_LOG_UPDATE_MESSAGE = "Watchlist Item updated";
	public static final String WATCHLIST_LOG_DELETE_MESSAGE = "Watchlist Item deleted";
	public static final String WATCHLIST_LOG_DELETE_ALL_MESSAGE = "All Watchlist Items deleted for specified Watchlist";

	// UDR logging constants
	// public static final String UDR_LOG_TARGET_PREFIX = "{UDRtitle:";
	// public static final String UDR_LOG_TARGET_SUFFIX = "}";
	public static final String UDR_LOG_CREATE_MESSAGE = "UDR created";
	public static final String UDR_LOG_UPDATE_MESSAGE = "UDR updated";
	public static final String UDR_LOG_UPDATE_META_MESSAGE = "UDR meta data updated";
	public static final String UDR_LOG_DELETE_MESSAGE = "UDR deleted";

	////////////////////////////////////////////////////////////////////////////////////
	// WARNING/ERROR MESSAGES
	////////////////////////////////////////////////////////////////////////////////////
	public static final String AUDIT_LOG_WARNING_CANNOT_CONVERT_JSON_TO_STRING = "Action Data Object supplied for logging cannot be serialized (actionType=%s, Target=%s, message=%s)";
}
