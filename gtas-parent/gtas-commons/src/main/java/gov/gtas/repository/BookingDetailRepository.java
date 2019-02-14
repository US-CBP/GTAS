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

    @Query(value = "SELECT bd.* FROM BookingDetail bd WHERE bd.processed = FALSE and " +
            "bd.id in (select pb.booking_detail_id from pax_booking pb) and " +
            "bd.id in (select fl.bookingDetail_id from flight_leg fl)" , nativeQuery = true)
    List<BookingDetail> getBookingDetailByProcessedFlag();

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

    @Query("SELECT pax FROM Passenger pax WHERE pax.id IN (" +
            "SELECT pxtag.pax_id FROM PassengerIDTag pxtag WHERE pxtag.idTag IN (SELECT p.idTag FROM PassengerIDTag p WHERE p.pax_id = (:pax_id) ))")
    public List<Passenger> getBookingDetailsByPassengerIdTag(@Param("pax_id") Long pax_id);

}
