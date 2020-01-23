package gov.gtas.repository;

import gov.gtas.model.PassengerTripDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Set;

public interface PassengerTripRepository extends CrudRepository<PassengerTripDetails, Long> {

	@Transactional
	@Query("Select ptd from PassengerTripDetails ptd where ptd.paxId in :paxIds ")
	Set<PassengerTripDetails> getTripDetailsByPaxId(@Param("paxIds") Set<Long> paxIds);

	@Query(value = "( select (case when :ref_number is not null then (select count(distinct pax_trip.ptd_id) "
			+ " from passenger_trip_details pax_trip "
			+ " where pax_trip.ptd_id in "
			+ " (select flightPax2.passenger_id from flight_passenger flightPax2 "
			+ " where  flightPax2.flight_id = "
			+ " (select flightPax1.flight_id from flight_passenger flightPax1 where "
			+ " flightPax1.passenger_id = :ptd_id)) and pax_trip.ref_number = :ref_number) - 1  else 0 end))", nativeQuery = true)
	int getCoTravelerCount(@Param("ptd_id") Long ptd_id, @Param("ref_number") String ref_number);
}
