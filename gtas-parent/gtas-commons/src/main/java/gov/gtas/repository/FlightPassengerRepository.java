package gov.gtas.repository;

import gov.gtas.model.FlightPassenger;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FlightPassengerRepository extends CrudRepository<FlightPassenger, Long> {
    @Query("SELECT fp FROM FlightPassenger fp WHERE fp.passengerId IN :ids")
    Iterable<FlightPassenger> findAllByPassengerIds(@Param("ids") Iterable<Long> ids);
}
