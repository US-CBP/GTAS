/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.tamr.model.TamrPassengerSendObject;

@Service
public interface TamrConversionService {

	public List<TamrPassengerSendObject> convertGTASFlightToTamrMessage(Flight flight);

	public List<TamrPassengerSendObject> convertGTASFlightToTamrMessage(Set<Flight> flights);

	public void sendPassengersToTamr(Set<Flight> flights, Set<Passenger> passengers);
}
