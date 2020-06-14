/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.HitDetail;

import gov.gtas.model.Passenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface HitDetailRepository extends CrudRepository<HitDetail, Long> {

	HitDetail findFirstByOrderByIdDesc();

	@Query("SELECT hd from HitDetail hd left join fetch hd.hitMaker l left join fetch l.hitCategory left join fetch hd.hitViewStatus left join fetch hd.flight f left join fetch f.mutableFlightDetails left join fetch hd.passenger where hd.passenger.id = :passengerId")
	Set<HitDetail> getSetFromPassengerId(@Param("passengerId") Long passengerId);

	Set<HitDetail> findFirst10ByPassengerInOrderByCreatedDateDesc(Set<Passenger> passenger);

	@Query("SELECT hd from HitDetail hd left join fetch hd.hitMaker l left join fetch l.countryGroup cg left join fetch cg.associatedCountries" +
			" where hd.id in :hdIds and hd.flightId in :fIds")
	Set<HitDetail> getHitDetailsWithCountryGroups(@Param("hdIds")Set<Long> hdIds, @Param("fIds") Set<Long> fIds);
}
