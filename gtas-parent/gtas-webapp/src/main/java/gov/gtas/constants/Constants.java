/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constants;

public final class Constants {

    // Query URI
    public static final String QUERY_SERVICE = "/query";
    public static final String INIT = "/init";
    public static final String RUN_QUERY_FLIGHT_URI = "/queryFlights";
    public static final String RUN_QUERY_PASSENGER_URI = "/queryPassengers";
    public static final String PATH_VARIABLE_ID = "/{id}";
    
    //UDR URI
    public static final String UDR_ROOT = "/udr";
    public static final String UDR_GET_BY_AUTHOR_TITLE = "/udr/{authorId}/{title}";
    public static final String UDR_GET_BY_ID = "/udr/{id}";
    public static final String UDR_GETALL_BY_USER = "/udr";
    public static final String UDR_GETALL = "/all_udr";
    public static final String UDR_GETDRL = "/udr_drl";
    public static final String UDR_GETDRL_BY_NAME = "/udr_drl/{kbName}";
    public static final String UDR_POST = "/udr";
    public static final String UDR_COPY = "/copy_udr/{id}";
    public static final String UDR_PUT = "/udr/{id}";
    public static final String UDR_DELETE = "/udr/{id}";
    public static final String UDR_TEST = "/udr_test";

    //Targeting URI
    public static final String TARGET_ONE_APIS_MSG = "/apis/{id}";
    public static final String TARGET_ALL_APIS = "/apis";
    public static final String TARGET_ALL_PNR = "/pnr";
    public static final String TARGET_ALL_MSG = "/target";
    
    //WATCH LIST URI
    public static final String WL_GET_BY_NAME = "/wl/{entity}/{name}";
    public static final String WL_GETALL = "/wl";
    public static final String WL_GETDRL = "/wl_drl";
    public static final String WL_CREATE_UPDATE_DELETE_ITEMS = "/wl/{entity}";//POST/PUT
    public static final String WL_DELETE = "/wl/{entity}/{name}";
    public static final String WL_COMPILE = "/wl/compile";//GET
    public static final String WL_TEST = "/testwl";//GET

    
    // Query Messages
    public static final String QUERY_SAVED_SUCCESS_MSG = "Query saved successfully";
    public static final String QUERY_EDITED_SUCCESS_MSG = "Query updated successfully";
    public static final String QUERY_DELETED_SUCCESS_MSG = "Query deleted successfully";
    public static final String QUERY_SERVICE_ERROR_MSG = "An error occurred while trying to process your request";
    
    public static final String QUERYOBJECT_OBJECTNAME = "queryObject";
    public static final String QUERYREQUEST_OBJECTNAME = "queryRequest";
    
    // Hits Summary
    public static final String HITS_SUMMARY_SERVICE = "/hit";
    public static final String HITS_SUMMARY_RULES_BY_TRAVELER_ID = "/traveler/{id}";
    
    //Security Roles
    public static final String MANAGE_RULES_ROLE = "MANAGE_RULES";
    public static final String MANAGE_QUERIES_ROLE = "MANAGE_QUERIES";
    public static final String VIEW_FLIGHT_PASSENGERS_ROLE = "VIEW_FLIGHT_PASSENGERS";
    public static final String MANAGE_WATCHLIST_ROLE = "MANAGE_WATCHLIST";
    public static final String ADMIN_ROLE = "ADMIN";
    
    //Security URL Paths
    public static final String LOGIN_PAGE = "/login.html";
    public static final String HOME_PAGE = "/home.action";
    public static final String LOGOUT_MAPPING = "/logout.action";
    

}
