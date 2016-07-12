/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

public enum AuditActionType {
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
    UPDATE_DASHBOARD_RUN
}
