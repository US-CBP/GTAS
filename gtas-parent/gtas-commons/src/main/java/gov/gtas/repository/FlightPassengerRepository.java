package gov.gtas.repository;

import gov.gtas.model.FlightPassenger;
import gov.gtas.model.Passenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlightPassengerRepository extends CrudRepository<FlightPassenger, Long> {

    @Query("SELECT fp.passenger FROM FlightPassenger fp " +
            "WHERE UPPER(fp.passenger.firstName) = UPPER(:firstName) " +
            "AND UPPER(fp.passenger.lastName) = UPPER(:lastName) " +
            "AND fp.flightId = :flightId")
    List<Passenger> returnAPassengerFromParameters(@Param("flightId") Long flightId, @Param("firstName")String firstName, @Param("lastName")String lastName);
}
