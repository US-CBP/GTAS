/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

/**
 * The Enum AuditActionType.
 */
public enum AuditActionType {

	ACCESS_INFORMATION,

	CREATE_UDR,

	UPDATE_UDR,

	UPDATE_UDR_META,

	DELETE_UDR,

	CREATE_WL,

	UPDATE_WL,

	DELETE_WL,

	DELETE_ALL_WL,

	LOAD_APIS,

	LOAD_PNR,

	CREATE_USER,

	UPDATE_USER,

	SUSPEND_USER,

	DELETE_USER,

	TARGETING_RUN,

	LOADER_RUN,

	UPDATE_DASHBOARD_RUN,

	MESSAGE_INGEST_PARSING,

	RULE_HIT_CASE_OPEN,

	DISPOSITION_STATUS_CHANGE,

	DATA_TRUNCATION
}
