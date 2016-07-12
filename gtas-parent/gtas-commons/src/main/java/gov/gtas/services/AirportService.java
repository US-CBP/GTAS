/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Airport;

import java.util.List;

public interface AirportService {
    
    public Airport create(Airport port);
    public Airport delete(Long id);
    public List<Airport> findAll();
    public Airport update(Airport port) ;
    public Airport findById(Long id);
    public Airport getAirportByThreeLetterCode(String airportCode);
    public Airport getAirportByFourLetterCode(String airportCode);

}
