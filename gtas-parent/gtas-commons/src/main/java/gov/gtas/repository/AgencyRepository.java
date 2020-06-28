/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.Agency;

import java.util.List;

public interface AgencyRepository extends CrudRepository<Agency, Long> {
	List<Agency> findByNameAndLocationAndFlightId(String name, String location, Long flightId);
}
