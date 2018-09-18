
package gov.gtas.services;

import gov.gtas.model.lookup.AppConfiguration;


public interface AppConfigurationService
{
   
    public AppConfiguration findByOption(String option);
    
    public AppConfiguration save(AppConfiguration appConfig);    

}