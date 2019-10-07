package gov.gtas.services;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.vo.PassengerVo;

public interface LoaderServices {
	Passenger findPassengerOnFlight(Flight f, PassengerVo pvo);
}
