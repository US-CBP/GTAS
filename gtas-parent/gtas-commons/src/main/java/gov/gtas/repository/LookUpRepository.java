/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.List;

import gov.gtas.model.FlightDirection;
import gov.gtas.model.lookup.Airport;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.model.lookup.Carrier;
import gov.gtas.model.lookup.Country;

public interface LookUpRepository {
    public List<Country> getAllCountries();

    public List<Carrier> getAllCarriers();

    public List<Airport> getAllAirports();
    
    public List<FlightDirection> getFlightDirections();

    public List<AppConfiguration> getAllAppConfiguration();

    public void clearAllEntitiesCache();

    public void clearEntityFromCache(Long id);

    public Country saveCountry(Country country);

    public Country getCountry(String countryName);

    public String getAppConfigOption(String option);

    public void removeCountryCache(String countryName);

    public void deleteCountryDb(Country country);
}
