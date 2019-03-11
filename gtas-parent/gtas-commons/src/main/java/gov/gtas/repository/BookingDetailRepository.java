/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Passenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.BookingDetail;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface BookingDetailRepository extends CrudRepository<BookingDetail, Long>{


    //Enforce booking detail processing on messages that have been analyzed or processed.
    @Query(value =
            "SELECT bd.* FROM BookingDetail bd " +
            "WHERE bd.processed = FALSE " +
            "AND bd.id IN (SELECT pnrbk.booking_detail_id FROM gtas.pnr_booking pnrbk " +
                "WHERE pnrbk.booking_detail_id = bd.id " +
                "AND pnrbk.pnr_id IN " +
                "   (SELECT ms.ms_message_id FROM message_status ms " +
                "        WHERE (ms.ms_message_id = pnrbk.pnr_id) " +
                "               AND (ms.ms_status = 'ANALYZED' " +
                "               OR ms.ms_status = 'PARTIAL_ANALYZE' " +
                "               OR ms.ms_status = 'FAILED_ANALYZE'" +
                    "           OR ms.ms_status = 'FAILED_LOAD'))) LIMIT :theLimit" , nativeQuery = true)
    List<BookingDetail> getBookingDetailByProcessedFlag(@Param("theLimit") Long theLimit);

    @Query("SELECT p FROM BookingDetail p WHERE p.flightNumber = (:flight_number) " +
            "AND UPPER(p.origin) = UPPER(:origin)" +
            "AND UPPER(p.destination) = UPPER(:destination)" +
            "AND p.etaDate = (:eta_date)" +
            "AND p.etdDate = (:etd_date)" +
            "AND p.processed = (:processed)")
    public List<BookingDetail> getSpecificBookingDetail(@Param("flight_number") String flight_number, @Param("origin") String origin,
                                                        @Param("destination") String destination, @Param("eta_date") Date eta_date,
                                                        @Param("etd_date") Date etd_date, @Param("processed") Boolean processed);

    @Query("SELECT bd FROM BookingDetail bd JOIN bd.passengers p WHERE p.id = (:pax_id)")
    public List<BookingDetail> getBookingDetailsByPassengers(@Param("pax_id") Long pax_id);


    @Query("SELECT p FROM BookingDetail p WHERE p.flightNumber = (:flight_number) " +
            "AND UPPER(p.origin) = UPPER(:origin)" +
            "AND UPPER(p.destination) = UPPER(:destination)" +
            "AND p.etaDate = (:eta_date)" +
            "AND p.etdDate = (:etd_date)")
    List<BookingDetail> getSpecificBookingDetailNoProcessedTag(@Param("flight_number") String flight_number, @Param("origin") String origin,
                                                        @Param("destination") String destination, @Param("eta_date") Date eta_date,
                                                        @Param("etd_date") Date etd_date);

    @Query("SELECT pax FROM Passenger pax " +
            "left join fetch pax.passengerDetails  " +
            "left join fetch pax.bookingDetails " +
            "left join fetch pax.flightPaxList pfpl " +
            "left join fetch pax.passengerTripDetails " +
            "left join fetch pfpl.flight " +
            "left join fetch pfpl.passenger  WHERE pax.id IN (" +
            "SELECT pxtag.pax_id FROM PassengerIDTag pxtag WHERE pxtag.idTag IN (SELECT p.idTag FROM PassengerIDTag p WHERE p.pax_id = (:pax_id) ))")
    public List<Passenger> getBookingDetailsByPassengerIdTag(@Param("pax_id") Long pax_id);

}
