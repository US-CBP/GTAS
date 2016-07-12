/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.model.lookup.Airport;
import gov.gtas.repository.AirportRepository;

@Repository
public class ElasticIndexer {
    @Autowired
    private AirportRepository airportRepo;

    private ObjectMapper mapper = new ObjectMapper();
    private PrintWriter writer = null;

    @Transactional
    public void createBulkIndexJson(String filePath, Pnr m) {
        try {
            writer = new PrintWriter(filePath, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();   
        }
        handleFlightsPax(m.getFlights(), m.getPassengers());
    }
    
    @Transactional
    public void createBulkIndexJson(String filePath, ApisMessage m) {
        try {
            writer = new PrintWriter(filePath, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();   
        }
        
        handleFlightsPax(m.getFlights(), m.getPassengers());
    }
    
    @Transactional
    public void handleFlightsPax(Set<Flight> flights, Set<Passenger> passengers) {
   
        String cmdFormat = "{ \"index\" : { \"_index\" : \"gtas\", \"_type\" : \"%s\" } }";
        for (Flight f : flights) {
            IndexedFlightVo vo = new IndexedFlightVo();
            BeanUtils.copyProperties(f, vo);
            vo.setOriginLocation(getLatLong(vo.getOrigin()));
            vo.setDestinationLocation(getLatLong(vo.getDestination()));
            
            writer.println(String.format(cmdFormat, "flight"));
            try {
                writer.println(mapper.writeValueAsString(vo));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        
        for (Passenger p : passengers) {
            IndexedPassengerVo vo = new IndexedPassengerVo();
            BeanUtils.copyProperties(p, vo);

            writer.println(String.format(cmdFormat, "passenger"));
            try {
                writer.println(mapper.writeValueAsString(vo));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }            
        }
        
        writer.close();
    }
    
    private String getLatLong(String airport) {
        if (StringUtils.isBlank(airport)) {
            return "";
        }
        
        List<Airport> airports = airportRepo.getAirportByThreeLetterCode(airport);
        if (CollectionUtils.isEmpty(airports)) {
            return "";
        }
        
        Airport a = airports.get(0);
        return String.format("%s, %s", a.getLatitude(), a.getLongitude());
        
    }
}
