/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import java.util.*;

import gov.gtas.constant.RuleServiceConstants;

public class RuleTemplateConstants {
    public static final String SEAT_ENTITY_NAME = "Seat";
    public static final String SEAT_ATTRIBUTE_NAME = "number";


    //CASE MATTERS -> LINKS TO FRONT END RULE CREATION - Doing a "toUpper" on comparison.
    public static final List<String> passDetails = Arrays.asList("AGE", "DOB", "GENDER", "FIRSTNAME","LASTNAME", "MIDDLENAME", "RESIDENCYCOUNTRY", "CITIZENSHIPCOUNTRY","PASSENGERTYPE");
    public static final Set<String> PASSENGER_DETAILS= Collections.unmodifiableSet(new HashSet<>(passDetails));
    public static final String PASSENGER_DETAILS_NAME= "PassengerDetails";

    //CASE MATTERS -> LINKS TO FRONT END RULE CREATION - Doing a "toUpper" on comparison.
    public static final String PASSENGER_TRIP_DETAILS_NAME = "PassengerTripDetails";
    public static final List<String> passTripDetails = Arrays.asList("EMBARKATION", "DEBARKATION", "RESERVATIONREFERENCENUMBER",
            "TRAVELFREQUENCY", "TOTALBAGWEIGHT", "BAGNUM", "EMBARKCOUNTRY", "DEBARKCOUNTRY");
    public static final Set<String> PASSENGER_TRIP_DETAILS= Collections.unmodifiableSet(new HashSet<>(passTripDetails));



    public static final String PASSENGER_VARIABLE_NAME = "$p";
    public static final String PASSENGER_DETAILS_VARIABLE_NAME = "$pcb";
    public static final String PASSENGER_TRIP_VARIABLE_NAME = "$ptcb";
    public static final String DOCUMENT_VARIABLE_NAME = "$d";
    public static final String FLIGHT_VARIABLE_NAME = "$f";
    public static final String ADDRESS_VARIABLE_NAME = "$addr";
    public static final String PHONE_VARIABLE_NAME = "$ph";
    public static final String EMAIL_VARIABLE_NAME = "$e";
    public static final String CREDIT_CARD_VARIABLE_NAME = "$cc";
    public static final String TRAVEL_AGENCY_VARIABLE_NAME = "$ta";
    public static final String FREQUENT_FLYER_VARIABLE_NAME = "$ff";
    public static final String PNR_VARIABLE_NAME = "$pnr";
    public static final String SEAT_VARIABLE_NAME = "$seat";
    public static final String DWELL_TIME_VARIABLE_NAME = "$dwell";
    public static final String FLIGHT_PAX_VARIABLE_NAME = "$fp";
    public static final String BAG_VARIABLE_NAME = "$b";
    public static final String LINK_VARIABLE_SUFFIX = "link";
    public static final String LINK_PNR_ID = "pnrId";
    public static final String LINK_ATTRIBUTE_ID = "linkAttributeId";
    public static final String BOOKING_DETAIL_VARIABLE_NAME = "$bl";
    public static final String PAYMENT_FORM_VARIABLE_NAME = "$pf";
    public static final String PAYMENT_FORM_ENTITY_NAME = "PaymentForm";
    public static final String PAYMENT_TYPE_ATTRIBUTE_NAME = "paymentType";
    public static final String PAYMENT_FORM_FIELD_ALIAS = "paymentForms";
    
    private RuleTemplateConstants() {
        // to prevent instantiation.
    }
    public static final Set<String> YES_SET;
    static{
        YES_SET = new HashSet<String>();
        for(String member: new String[]{"Y", "y", "Yes", "YES"}){
            YES_SET.add(member);
        }
        
    }
    public static final char COLON_CHAR = ':';
    public static final char DOUBLE_QUOTE_CHAR = '"';
    public static final char SINGLE_QUOTE_CHAR = '\'';
    public static final char LEFT_PAREN_CHAR = '(';
    public static final char RIGHT_PAREN_CHAR = ')';
    public static final char SPACE_CHAR = ' ';
    public static final char COMMA_CHAR = ',';

    public static final String REGEX_WILDCARD = ".*";

    public static final String NEW_LINE = "\n";
    public static final String TRUE_STRING = "true";
    public static final String FALSE_STRING = "false";
        
    public static final String RULE_PACKAGE_NAME = "package gov.gtas.rule;\n";
    public static final String IMPORT_PREFIX = "import ";
    public static final String GLOBAL_RESULT_DECLARATION = "global java.util.List "+RuleServiceConstants.RULE_RESULT_LIST_NAME+";\n\n";
    
}
