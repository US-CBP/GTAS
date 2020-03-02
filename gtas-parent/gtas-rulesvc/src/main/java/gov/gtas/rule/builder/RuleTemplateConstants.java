/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import java.util.*;

import gov.gtas.constant.RuleServiceConstants;

public class RuleTemplateConstants {
	static final String SEAT_ENTITY_NAME = "Seat";
	public static final String SEAT_ENTITY_NAME_LOWERCASE = "seat";
	public static final String SEAT_ATTRIBUTE_NAME = "number";
	public static final String DOLLAR_SIGN = "$";

	// CASE MATTERS -> LINKS TO FRONT END RULE CREATION - Doing a "toUpper" on
	// comparison.
	// Field Names coming from the query builder have different format than needed
	// for rule builder, this map syncs the naming conventions
	static final Map<String, String> passDetailsMap;
	static {
		HashMap<String, String> passengerDetailsMap = new HashMap<String, String>() {
		};
		passengerDetailsMap.put("PASSENGERDETAILS.AGE", "age");
		passengerDetailsMap.put("PASSENGERDETAILS.DOB", "dob");
		passengerDetailsMap.put("PASSENGERDETAILS.GENDER", "gender");
		passengerDetailsMap.put("PASSENGERDETAILS.FIRSTNAME", "firstName");
		passengerDetailsMap.put("PASSENGERDETAILS.LASTNAME", "lastName");
		passengerDetailsMap.put("PASSENGERDETAILS.MIDDLENAME", "middleName");
		passengerDetailsMap.put("PASSENGERDETAILS.RESIDENCYCOUNTRY", "residencyCountry");
		passengerDetailsMap.put("PASSENGERDETAILS.NATIONALITY", "nationality");
		passengerDetailsMap.put("PASSENGERDETAILS.PASSENGERTYPE", "passengerType");
		passDetailsMap = Collections.unmodifiableMap(passengerDetailsMap);
	}

	static final Set<String> PASSENGER_DETAILS_SET;
	static {
		HashSet<String> passengerDetailsSet = new HashSet<String>() {
		};
		passengerDetailsSet.add("AGE");
		passengerDetailsSet.add("DOB");
		passengerDetailsSet.add("GENDER");
		passengerDetailsSet.add("FIRSTNAME");
		passengerDetailsSet.add("LASTNAME");
		passengerDetailsSet.add("MIDDLENAME");
		passengerDetailsSet.add("RESIDENCYCOUNTRY");
		passengerDetailsSet.add("NATIONALITY");
		passengerDetailsSet.add("PASSENGERTYPE");
		PASSENGER_DETAILS_SET = Collections.unmodifiableSet(passengerDetailsSet);
	}

	static final String PASSENGER_DETAILS_NAME = "PassengerDetails";

	// CASE MATTERS -> LINKS TO FRONT END RULE CREATION - Doing a "toUpper" on
	// comparison.
	// Field Names coming from the query builder have different format than needed
	// for rule builder, this map syncs the naming conventions
	static final Map<String, String> passTripDetailsMap;
	static {
		HashMap<String, String> passengerTripDetailsMap = new HashMap<String, String>() {
		};
		passengerTripDetailsMap.put("PASSENGERTRIPDETAILS.EMBARKATION", "embarkation");
		passengerTripDetailsMap.put("PASSENGERTRIPDETAILS.DEBARKATION", "debarkation");
		passengerTripDetailsMap.put("PASSENGERTRIPDETAILS.TRAVELFREQUENCY", "travelFrequency");
		passengerTripDetailsMap.put("PASSENGERTRIPDETAILS.EMBARKCOUNTRY", "embarkCountry");
		passengerTripDetailsMap.put("PASSENGERTRIPDETAILS.DEBARKCOUNTRY", "debarkCountry");
		passengerTripDetailsMap.put("PASSENGERTRIPDETAILS.COTRAVELERCOUNT", "coTravelerCount");
		passengerTripDetailsMap.put("PASSENGERTRIPDETAILS.HOURSBEFORETAKEOFF", "hoursBeforeTakeOff");
		passTripDetailsMap = Collections.unmodifiableMap(passengerTripDetailsMap);
	}

	static final Set<String> FLIGHT_MUTABLE_DETAILS;
	static {
		HashSet<String> flightDetailsSet = new HashSet<String>() {
		};
		flightDetailsSet.add("ETADATE");
		FLIGHT_MUTABLE_DETAILS = Collections.unmodifiableSet(flightDetailsSet);
	}

	static final Map<String, String> flightMutableDetailsMap;
	static {
		HashMap<String, String> flightDetailsMap = new HashMap<String, String>() {
		};
		flightDetailsMap.put("MUTABLEFLIGHTDETAILS.ETA", "etadate");
		flightMutableDetailsMap = Collections.unmodifiableMap(flightDetailsMap);
	}

	static final String PASSENGER_TRIP_DETAILS_NAME = "PassengerTripDetails";

	static final String PASSENGER_VARIABLE_NAME = "$p";
	static final String PASSENGER_DETAILS_VARIABLE_NAME = "$pcb";
	static final String PASSENGER_TRIP_VARIABLE_NAME = "$ptcb";
	public static final String DOCUMENT_VARIABLE_NAME = "$d";
	static final String FLIGHT_VARIABLE_NAME = "$f";
	public static final String ADDRESS_VARIABLE_NAME = "$addr";
	public static final String PHONE_VARIABLE_NAME = "$ph";
	public static final String EMAIL_VARIABLE_NAME = "$e";
	public static final String CREDIT_CARD_VARIABLE_NAME = "$cc";
	public static final String TRAVEL_AGENCY_VARIABLE_NAME = "$ta";
	public static final String FREQUENT_FLYER_VARIABLE_NAME = "$ff";
	static final String PNR_VARIABLE_NAME = "$pnr";
	static final String SEAT_VARIABLE_NAME = "$seat";
	public static final String PNR_SEAT = "$pnr_seat";
	public static final String DWELL_TIME_VARIABLE_NAME = "$dwell";
	static final String FLIGHT_PAX_VARIABLE_NAME = "$fp";
	public static final String BAG_VARIABLE_NAME = "$b";
	public static final String LINK_VARIABLE_SUFFIX = "link";
	public static final String LINK_PNR_ID = "pnrId";
	public static final String LINK_ATTRIBUTE_ID = "linkAttributeId";
	public static final String BOOKING_DETAIL_VARIABLE_NAME = "$bl";
	public static final String PAYMENT_FORM_VARIABLE_NAME = "$pf";
	static final String PAYMENT_FORM_ENTITY_NAME = "PaymentForm";
	public static final String PAYMENT_TYPE_ATTRIBUTE_NAME = "paymentType";
	public static final String PAYMENT_FORM_FIELD_ALIAS = "paymentForms";
	public static final String PAYMENT_FORM_FIELD_ALIAS_LOWERCASE = "paymentforms";

	private RuleTemplateConstants() {
		// to prevent instantiation.
	}

	public static final char COLON_CHAR = ':';
	static final char DOUBLE_QUOTE_CHAR = '"';
	public static final char SINGLE_QUOTE_CHAR = '\'';
	public static final char LEFT_PAREN_CHAR = '(';
	static final char RIGHT_PAREN_CHAR = ')';
	static final char SPACE_CHAR = ' ';
	static final char COMMA_CHAR = ',';

	static final String REGEX_WILDCARD = ".*";

	public static final String NEW_LINE = "\n";
	static final String TRUE_STRING = "true";
	static final String FALSE_STRING = "false";

	static final String RULE_PACKAGE_NAME = "package gov.gtas.rule;\n";
	static final String IMPORT_PREFIX = "import ";
	static final String GLOBAL_RESULT_DECLARATION = "global java.util.List "
			+ RuleServiceConstants.RULE_RESULT_LIST_NAME + ";\n\n";

}
