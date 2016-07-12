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
    
    public AppConfiguration findByOption(String option);
}
