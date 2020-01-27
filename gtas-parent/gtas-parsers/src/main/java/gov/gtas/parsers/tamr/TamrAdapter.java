/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.tamr.model.TamrPassenger;

import java.util.List;
import java.util.Set;

public interface TamrAdapter {
	List<TamrPassenger> convert(Flight flight, Set<Passenger> passengers);
}
