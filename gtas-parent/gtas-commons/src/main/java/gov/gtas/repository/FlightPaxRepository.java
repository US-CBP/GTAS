package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.FlightPax;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface FlightPaxRepository extends CrudRepository<FlightPax, Long>{

    @Query("SELECT fp FROM FlightPax fp WHERE fp.passenger.id IN :pidList")
    List<FlightPax> getFlightPaxByPassengerIdList(@Param("pidList") List<Long> pidList);

}
