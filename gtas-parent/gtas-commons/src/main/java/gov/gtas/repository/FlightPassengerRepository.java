package gov.gtas.repository;

import gov.gtas.model.FlightPassenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlightPassengerRepository extends CrudRepository<FlightPassenger, Long> {

     @Query("SELECT fp FROM FlightPassenger fp " +
            "JOIN FETCH fp.passenger pax " +
            "LEFT JOIN FETCH pax.documents " +
            "LEFT JOIN FETCH pax.seatAssignments " +
            "LEFT JOIN FETCH pax.bags " +
            "LEFT JOIN FETCH pax.flightPaxList " +
            "LEFT JOIN FETCH pax.tickets " +
            "LEFT JOIN FETCH pax.bookingDetails " +
            "WHERE UPPER(fp.passenger.passengerDetails.firstName) = UPPER(:firstName) " +
            "AND UPPER(fp.passenger.passengerDetails.lastName) = UPPER(:lastName) " +
            "AND fp.flightId = :flightId")
    List<FlightPassenger> returnAPassengerFromParameters(@Param("flightId") Long flightId, @Param("firstName")String firstName, @Param("lastName")String lastName);
}
