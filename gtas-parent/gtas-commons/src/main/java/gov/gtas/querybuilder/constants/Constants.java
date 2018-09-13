/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.constants;


public final class Constants {

    public static final String SELECT = "select";
    public static final String SELECT_DISTINCT = "select distinct";
    public static final String FROM = "from";
    public static final String WHERE = "where";
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String JOIN = " join ";
    public static final String JOIN_FETCH = " join fetch ";
    public static final String LEFT_JOIN = " left join ";
    public static final String LEFT_JOIN_FETCH = " left join fetch ";
    public static final String EXISTS = "exists";
    
    // Entities
    public static final String ADDRESS = "ADDRESS";
    public static final String CREDITCARD = "CREDITCARD";
    public static final String DOCUMENT = "DOCUMENT";
    public static final String EMAIL = "EMAIL";
    public static final String FLIGHT = "FLIGHT";
    public static final String FREQUENTFLYER = "FREQUENTFLYER";
    public static final String HITS = "HITSSUMMARY";
    public static final String PASSENGER = "PASSENGER";
    public static final String PHONE = "PHONE";
    public static final String PNR = "PNR";
    public static final String AGENCY = "AGENCY";
    public static final String DWELLTIME = "DWELLTIME";
    public static final String BAG = "BAG";
    public static final String FLIGHTPAX = "FLIGHTPAX";
    //public static final String FLIGHTLEG = "FLIGHTLEG";
    public static final String BOOKINGDETAIL = "BOOKINGDETAIL";
    
    public static final String QUERYOBJECT_OBJECTNAME = "queryObject";
    public static final String USERQUERY_OBJECTNAME = "userQuery";
    
    public static final String UNIQUE_TITLE_QUERY = "UserQuery.checkUniqueTitle";
    public static final String LIST_QUERY = "UserQuery.listQueryByUser";
    public static final String IS_VALID_USER = "UserQuery.isValidUser";
    
    public static final String IS_RULE_HIT = "isRuleHit";
    public static final String HITS_ID = "id";
    public static final String PNR_ID = "id";
    public static final String IS_WATCHLIST_HIT = "isWatchListHit";
    public static final String EXISTS_HITS_PREFIX = "exists (select";
    public static final String NOT_EXISTS_HITS_PREFIX = "not exists (select";
    public static final String SELECT_COUNT_DISTINCT = "select count(distinct";
    public static final String HITS_FLIGHT_REF = ".flight.id";
    public static final String ID = ".id";
    public static final String HITS_PASSENGER_REF = ".passenger.id";
    public static final String RULE_HIT_TYPE = "hitType like '%r%'";
    public static final String PASSENGER_HIT_TYPE = "hitType like '%p%'";
    public static final String DOCUMENT_HIT_TYPE = "hitType like '%d%'";
    
    public static final String SEAT = "SEAT";
    public static final String PAYMENTFORMS = "PAYMENTFORMS";
    public static final String FORM_OF_PAYMENT = "formOfPayment";
    
    public static final String QUERY_EXISTS_ERROR_MSG = "A query with the same title already exists. Please rename this query or edit the existing one.";
    public static final String QUERY_DOES_NOT_EXIST_ERROR_MSG = "Query cannot be found.";
    public static final String NULL_QUERY = "Query is null.";
    public static final String INVALID_USER = "Invalid user.";
}
