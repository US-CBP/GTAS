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
}
