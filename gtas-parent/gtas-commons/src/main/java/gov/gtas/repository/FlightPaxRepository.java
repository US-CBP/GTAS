package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.FlightPax;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FlightPaxRepository extends CrudRepository<FlightPax, Long>{

    default FlightPax findOne(Long flightPaxId)
    {
    	return findById(flightPaxId).orElse(null);
    }

    @Query("SELECT fps FROM FlightPax fps WHERE fps.passenger.id IN :pidList")
    List<FlightPax> findFlightFromPassIdList(@Param("pidList") List<Long> pidList);

}
