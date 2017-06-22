/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Airport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ServiceUtil {
    
    @Autowired
    private  AirportService airportService;
    
    public String getCountry(String airport){
        
        Airport a = airportService.getAirportByThreeLetterCode(airport);
        if(a == null ){
            return "USA";
        }
        return a.getCountry();
        
    }

    public AirportService getAirportService() {
        return airportService;
    }

    public void setAirportService(AirportService airportService) {
        this.airportService = airportService;
    }

}
