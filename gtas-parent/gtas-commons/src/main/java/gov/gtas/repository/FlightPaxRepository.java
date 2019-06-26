package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.FlightPax;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Set;


public interface FlightPaxRepository extends CrudRepository<FlightPax, Long>{

    default FlightPax findOne(Long flightPaxId)
    {
    	return findById(flightPaxId).orElse(null);
    }

    @Transactional
    @Query("SELECT fps FROM FlightPax fps WHERE fps.passenger.id IN :pidList")
    Set<FlightPax> findFlightFromPassIdList(@Param("pidList") Collection<Long> pidList);

}
