/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.lookup.AppConfiguration;

public interface AppConfigurationRepository extends CrudRepository<AppConfiguration, Long> {
    String HOME_COUNTRY = "HOME_COUNTRY";
    String QUEUE = "QUEUE";
    String QUEUE_OUT = "QUEUE_OUT";
    String QUEUE_IN = "QUEUE_IN";
    String ELASTIC_HOSTNAME = "ELASTIC_HOSTNAME";
    String HOURLY_ADJ = "HOURLY_ADJ";
    String UTC_SERVER = "UTC_SERVER";
    String ELASTIC_PORT = "ELASTIC_PORT";
    String UPLOAD_DIR = "UPLOAD_DIR";
    String DASHBOARD_AIRPORT = "DASHBOARD_AIRPORT";
    String SMS_TOPIC_ARN = "SMS_TOPIC_ARN";
    String MATCHING_THRESHOLD = "MATCHING_THRESHOLD";
    String MAX_PASSENGER_QUERY_RESULT = "MAX_PASSENGER_QUERY_RESULT";
    String MAX_FLIGHT_QUERY_RESULT = "MAX_FLIGHT_QUERY_RESULT";
    String THREADS_ON_LOADER = "THREADS_ON_LOADER";
    String THREADS_ON_RULES = "THREADS_ON_RULES";
    String FLIGHT_RANGE = "FLIGHT_RANGE";
    String REDIS_KEYS_TTL = "REDIS_KEYS_TTL";
    String REDIS_KEYS_TTL_TIME_UNIT = "REDIS_KEYS_TTL_TIME_UNIT";
    //public static String CASE_COUNTDOWN_LABEL = "CASE_COUNTDOWN_LABEL";
    String APIS_ONLY_FLAG = "APIS_ONLY_FLAG";
    String APIS_VERSION = "APIS_VERSION";
    String BOOKING_COMPRESSION_AMOUNT = "BOOKING_COMPRESSION_AMOUNT";
    String MAX_PASSENGERS_PER_RULE_RUN = "MAX_PASSENGERS_PER_RULE_RUN";
    String MAX_PASSENGERS_PER_FUZZY_MATCH = "MAX_PASSENGERS_PER_FUZZY_MATCH";
    String MAX_MESSAGES_PER_RULE_RUN = "MAX_MESSAGES_PER_RULE_RUN";
    String MAX_FLIGHTS_SAVED_PER_BATCH = "MAX_FLIGHTS_PER_BATCH";
    String DATA_MANAGEMENT_TRUNC_TYPE_FLAG = "DATA_MANAGEMENT_TRUNC_TYPE_FLAG";
    String DATA_MANAGEMENT_CUT_OFF_TIME_SPAN = "DATA_MANAGEMENT_CUT_OFF_TIME_SPAN";
    String GRAPH_DB_URL = "GRAPH_DB_URL";
    String GRAPH_DB_TOGGLE = "GRAPH_DB_TOGGLE";
    String FUZZY_MATCHING = "FUZZY_MATCHING";
    String QUICKMATCH_DOB_YEAR_OFFSET ="QUICKMATCH_DOB_YEAR_OFFSET";

    AppConfiguration findByOption(String option);

    AppConfiguration save(AppConfiguration appConfig);
    
}
