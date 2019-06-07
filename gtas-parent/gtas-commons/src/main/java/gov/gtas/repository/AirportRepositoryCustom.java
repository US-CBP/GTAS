/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.lookup.Airport;

public interface AirportRepositoryCustom {

  Airport restore(Airport airport);
  int restoreAll();

}
