/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Airport;
import gov.gtas.repository.AirportRepository;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AirportServiceImpl implements AirportService{

    @Resource
    private AirportRepository airportRespository;
    
    @Override
    @Transactional
    public Airport create(Airport port) {
        
        return airportRespository.save(port);
    }

    @Override
    @Transactional
    public Airport delete(Long id) {
        Airport port = this.findById(id);
        if(port != null){
            airportRespository.delete(port);
        }
        return port;
    }

    @Override
    @Transactional
    public List<Airport> findAll() {
        // TODO Auto-generated method stub
        return (List<Airport>)airportRespository.findAll();
    }

    @Override
    @Transactional
    public Airport update(Airport port) {
        Airport airportToUpdate = this.findById(port.getId());
        if(airportToUpdate != null){
            //airportToUpdate not available.make airport mutable to update
        }
        return null;
        
    }

    @Override
    @Transactional
    public Airport findById(Long id) {
        
        return airportRespository.findOne(id);
    }

    @Override
    @Transactional
    @Cacheable(value = "airportCache", key = "#airportCode")
    public Airport getAirportByThreeLetterCode(String airportCode) {
        Airport airport = null;
        List<Airport> airports =airportRespository.getAirportByThreeLetterCode(airportCode);
        if(airports != null && airports.size() >0)
        airport = airports.get(0);
        return airport;
    }

    @Override
    @Transactional
    @Cacheable(value = "airportCache", key = "#airportCode")
    public Airport getAirportByFourLetterCode(String airportCode) {
        Airport airport = null;
        List<Airport> airports =airportRespository.getAirportByFourLetterCode(airportCode);
        if(airports != null && airports.size() >0)
        airport = airports.get(0);
        return airport;
    }

}
