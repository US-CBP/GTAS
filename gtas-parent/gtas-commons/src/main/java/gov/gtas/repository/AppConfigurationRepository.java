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

    public static String ELASTIC_HOSTNAME = "ELASTIC_HOSTNAME";
    public static String ELASTIC_PORT = "ELASTIC_PORT";
    
    public static String UPLOAD_DIR = "UPLOAD_DIR";
    public static String DASHBOARD_AIRPORT = "DASHBOARD_AIRPORT";

    public AppConfiguration findByOption(String option);
}
