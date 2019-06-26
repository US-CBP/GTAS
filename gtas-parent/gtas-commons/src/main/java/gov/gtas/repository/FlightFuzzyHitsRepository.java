/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.repository;

import gov.gtas.model.FlightHitsFuzzy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FlightFuzzyHitsRepository extends
        CrudRepository<FlightHitsFuzzy, Long> {


    @Transactional
    @Query("Select count(pwlts) from PassengerWLTimestamp pwlts where pwlts.passenger.flight.id = :flightId and pwlts.hitCount > 0")
    Integer fuzzyCount(@Param("flightId") Long flightId);
}
