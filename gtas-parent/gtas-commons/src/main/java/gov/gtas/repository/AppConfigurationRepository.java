/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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

    public AppConfiguration findByOption(String option);
}
