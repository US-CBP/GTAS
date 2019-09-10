package gov.gtas.repository;

import gov.gtas.model.FlightPassenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

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
            "LEFT JOIN FETCH pax.passengerDetails " +
            "LEFT JOIN FETCH pax.passengerTripDetails " +
            "WHERE (UPPER(fp.passenger.passengerDetails.firstName) = UPPER(:firstName) " +
            "AND UPPER(fp.passenger.passengerDetails.lastName) = UPPER(:lastName)) " +
            "AND fp.flightId = :flightId")
    List<FlightPassenger> returnAPassengerFromParameters(@Param("flightId") Long flightId, @Param("firstName")String firstName, @Param("lastName")String lastName);
    
     
     @Query("SELECT fp FROM FlightPassenger fp " +
             "JOIN FETCH fp.passenger pax " +
             "LEFT JOIN FETCH pax.documents " +
             "LEFT JOIN FETCH pax.seatAssignments " +
             "LEFT JOIN FETCH pax.bags " +
             "LEFT JOIN FETCH pax.flightPaxList " +
             "LEFT JOIN FETCH pax.tickets " +
             "LEFT JOIN FETCH pax.bookingDetails " +
             "LEFT JOIN FETCH pax.passengerDetails " +
             "LEFT JOIN FETCH pax.passengerTripDetails " +
             "WHERE fp.flightId = :flightId " +
             "AND :recordLocator IN (SELECT pnr.recordLocator FROM fp.flight.pnrs pnr) " +
             "AND fp.passenger.passengerTripDetails.pnrReservationReferenceNumber = :pnrReservationReferenceNumber ")
     List<FlightPassenger> getPassengerUsingREF(@NonNull @Param("flightId") Long flightId, 
    		 @NonNull @Param("pnrReservationReferenceNumber")String pnrReservationReferenceNumber,
    		 @NonNull @Param("recordLocator")String recordLocator);
 
}
