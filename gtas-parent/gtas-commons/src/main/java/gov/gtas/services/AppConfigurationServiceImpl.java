/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.repository.AppConfigurationRepository;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 *
 * @author gbays
 */
@Service
public class AppConfigurationServiceImpl implements AppConfigurationService
{

    @Resource
    private AppConfigurationRepository appConfigurationRepository; 
    
    @Override
    public AppConfiguration findByOption(String option)
    {
        AppConfiguration appConfig = appConfigurationRepository.findByOption(option);
        
        return appConfig;
        
    }
    
    @Override
    public AppConfiguration save(AppConfiguration appConfig)
    {
       appConfigurationRepository.save(appConfig);
       return appConfig; 
    }
    
}
