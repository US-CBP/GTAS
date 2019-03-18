/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.lookup.AppConfiguration;

public interface AppConfigurationRepository extends CrudRepository<AppConfiguration, Long> {
    public static String HOME_COUNTRY = "HOME_COUNTRY";
    public static String QUEUE = "QUEUE";
    public static String QUEUE_OUT ="QUEUE_OUT";
    public static String QUEUE_IN ="QUEUE_IN";
    public static String ELASTIC_HOSTNAME = "ELASTIC_HOSTNAME";
    public static String ELASTIC_PORT = "ELASTIC_PORT";
    public static String UPLOAD_DIR = "UPLOAD_DIR";
    public static String DASHBOARD_AIRPORT = "DASHBOARD_AIRPORT";
    public static String SMS_TOPIC_ARN = "SMS_TOPIC_ARN";
    public static String MATCHING_THRESHOLD = "MATCHING_THRESHOLD";
    public static String MAX_PASSENGER_QUERY_RESULT = "MAX_PASSENGER_QUERY_RESULT";
    public static String MAX_FLIGHT_QUERY_RESULT = "MAX_FLIGHT_QUERY_RESULT";
    public static String FLIGHT_RANGE = "FLIGHT_RANGE";
    public static String REDIS_KEYS_TTL = "REDIS_KEYS_TTL";
    public static String REDIS_KEYS_TTL_TIME_UNIT = "REDIS_KEYS_TTL_TIME_UNIT";
    //public static String CASE_COUNTDOWN_LABEL = "CASE_COUNTDOWN_LABEL";
    public static String APIS_ONLY_FLAG = "APIS_ONLY_FLAG";
    public static String APIS_VERSION = "APIS_VERSION";
    public static String BOOKING_COMPRESSION_AMOUNT = "BOOKING_COMPRESSION_AMOUNT";
    public static String MAX_PASSENGERS_PER_RULE_RUN = "MAX_PASSENGERS_PER_RULE_RUN";
    public static String MAX_PASSENGERS_PER_FUZZY_MATCH = "MAX_PASSENGERS_PER_FUZZY_MATCH";
    public static String MAX_MESSAGES_PER_RULE_RUN = "MAX_MESSAGES_PER_RULE_RUN";
    public static String MAX_FLIGHTS_SAVED_PER_BATCH = "MAX_FLIGHTS_PER_BATCH";

    public AppConfiguration findByOption(String option);
    public AppConfiguration save(AppConfiguration appConfig);
    
}
