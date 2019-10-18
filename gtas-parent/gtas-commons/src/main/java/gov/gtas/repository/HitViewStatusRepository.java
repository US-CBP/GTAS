/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.repository;

import gov.gtas.model.HitViewStatus;
import gov.gtas.model.Passenger;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface HitViewStatusRepository extends CrudRepository<HitViewStatus, Long> {

	Set<HitViewStatus> findAllByPassenger(Passenger p);
}
