/*
 *  All Application code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  Please see LICENSE.txt for details.
 */

package gov.gtas.parsers.omni;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.omni.model.OmniPassenger;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface OmniAdapter {
	List<OmniPassenger> convertPassengers(Flight flight, Set<Passenger> passengers);
	OmniPassenger convertPassengerToOmniRawProfile(Flight flight, Passenger passenger);
}
