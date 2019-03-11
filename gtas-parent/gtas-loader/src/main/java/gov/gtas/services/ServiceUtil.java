/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Flight;
import gov.gtas.model.FlightPassenger;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.Airport;

import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceUtil implements LoaderServices {

    @Autowired
    private AirportService airportService;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    FlightPassengerRepository flightPassengerRepository;

    public String getCountry(String airport) {

        Airport a = airportService.getAirportByThreeLetterCode(airport);
        if (a == null) {
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

    @Override
    @Transactional()
    public Passenger findPassengerOnFlight(Flight f, PassengerVo pvo) {
        if (f.getId() == null) {
            return null;
        }

        List<FlightPassenger> pax = flightPassengerRepository.returnAPassengerFromParameters(f.getId(),
                pvo.getFirstName(), pvo.getLastName());
    if (pax != null && pax.size() >= 1) {
            return pax.get(0).getPassenger();
        } else {
            return null;
        }
    }
}

