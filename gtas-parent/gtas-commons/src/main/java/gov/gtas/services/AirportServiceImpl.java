/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.Airport;
import gov.gtas.repository.AirportRepository;
import gov.gtas.repository.AirportRepositoryCustom;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AirportServiceImpl implements AirportService{

    @Resource
    private AirportRepository airportRepo;
    @Resource
    private AirportRepositoryCustom airportRepoCust;

    @Override
    @Transactional
    public Airport create(Airport port) {
        return airportRepo.save(port);
    }

    @Override
    @Transactional
    public Airport delete(Long id) {
        Airport port = this.findById(id);
        if(port != null){
            airportRepo.delete(port);
        }
        return port;
    }

    @Override
    @Transactional
    public List<Airport> findAll() {
        // TODO Auto-generated method stub
        return (List<Airport>)airportRepo.findAll();
    }

    @Override
    @Transactional
    public Airport update(Airport port) {
      return airportRepo.save(port);
    }

    @Override
    @Transactional
    public Airport findById(Long id) {
        
        return airportRepo.findOne(id);
    }

    @Override
    @Transactional
    public Airport restore(Airport airport) {
        return airportRepoCust.restore(airport);
    }

    @Override
    @Transactional
    public int restoreAll() {
        return airportRepoCust.restoreAll();
    }

    @Override
    @Transactional
    @Cacheable(value = "airportCache", key = "#airportCode")
    public Airport getAirportByThreeLetterCode(String airportCode) {
        Airport airport = null;
        List<Airport> airports =airportRepo.getAirportByThreeLetterCode(airportCode);
        if(airports != null && airports.size() >0)
        airport = airports.get(0);
        return airport;
    }

    @Override
    @Transactional
    @Cacheable(value = "airportCache", key = "#airportCode")
    public Airport getAirportByFourLetterCode(String airportCode) {
        Airport airport = null;
        List<Airport> airports = airportRepo.getAirportByFourLetterCode(airportCode);
        if(airports != null && airports.size() >0)
        airport = airports.get(0);
        return airport;
    }

}
