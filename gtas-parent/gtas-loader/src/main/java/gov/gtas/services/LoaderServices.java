package gov.gtas.services;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.summary.PassengerSummary;

import java.util.Date;
import java.util.Optional;

public interface LoaderServices {
	Passenger findPassengerOnFlight(Flight f, PassengerVo pvo);

	Optional<Passenger> findPassengerOnFlight(Flight f, PassengerSummary passengerSummary, String recordLocatorNumber);

	Integer getHoursBeforeTakeOff(Flight primeFlight, Date transmissionDate);

	}
