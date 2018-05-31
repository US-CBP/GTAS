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
    public static String FLIGHT_RANGE = "FLIGHT_RANGE";
    public static String REDIS_KEYS_TTL_IN_DAYS = "REDIS_KEYS_TTL_IN_DAYS";

    public AppConfiguration findByOption(String option);
    public AppConfiguration save(AppConfiguration appConfig);
    
}
