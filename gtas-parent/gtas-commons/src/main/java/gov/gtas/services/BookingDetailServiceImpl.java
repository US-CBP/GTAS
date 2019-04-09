/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.repository.BookingDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class BookingDetailServiceImpl implements BookingDetailService{

    private static final Logger logger = LoggerFactory.getLogger(BookingDetailServiceImpl.class);

    private final BookingDetailRepository bookingDetailRepository;

    @Autowired
    public BookingDetailServiceImpl(BookingDetailRepository bookingDetailRepository) {
        this.bookingDetailRepository = bookingDetailRepository;
    }

    @Override
    public List<BookingDetail> getBookingDetailsByProcessedFlag() {
        return null;
    }

    @Override
    public List<BookingDetail> getBookingDetailsByPassengers(Long pax_id) {
        List<BookingDetail> _bd;
        _bd = bookingDetailRepository.getBookingDetailsByPassengers(pax_id);
        return _bd;
    }

    public BookingDetail saveAndGetBookingDetail(BookingDetail bookingDetail) {
        bookingDetail = bookingDetailRepository.save(bookingDetail);
        return bookingDetail;
    }

    @Override
    @Transactional
    public List<BookingDetail> deDuplicateBookingDetails(List<BookingDetail> listContainingDuplicates) {
        List<BookingDetail> uniqueBookingDetails = new ArrayList<>();
        for (BookingDetail bd : listContainingDuplicates) {
            //Check to see if we already have a unique booking detail.
            BookingDetail uniqueBookingDetail = uniqueBookingDetails.stream()
                    .filter(bd1 -> bd1.equals(bd))
                    .findFirst().orElse(null);

            //If no booking detail exists in the list add current as a unique BD.
            if (null == uniqueBookingDetail) {
                uniqueBookingDetails.add(bd);
            } else {
                //If booking detail exists in list transfer information from duplicate bd to existing unique BD.
                try {
                    this.mergeBookingDetails(uniqueBookingDetail, bd);
                } catch (Exception ignored) {
                    logger.error("failed booking a booking detail with error: " + ignored.getMessage() );
                }
            }
        }
        return uniqueBookingDetails;
    }

    @Override
    @Transactional
    public BookingDetail mergeBookingDetails(BookingDetail newBD, BookingDetail oldBD) {
        //Save to get the booking details managed by hibernate.
        BookingDetail nbd = bookingDetailRepository.save(newBD);
        BookingDetail obd = bookingDetailRepository.save(oldBD);


        Set<Passenger> oldBookingDetailsPassenger = obd.getPassengers();
        Set<FlightLeg> oldBookingDetailsFlightLeg = obd.getFlightLegs();
        Set<Pnr> oldBookingDetailsPnr = obd.getPnrs();
        Set<Bag> oldBookingDetailsBags = obd.getBags();

        oldBookingDetailsPnr.forEach(bd -> {
            bd.getBookingDetails().add(nbd);
            bd.getBookingDetails().remove(obd);
        });

        oldBookingDetailsBags.forEach( bd -> {
           bd.getBookingDetail().add(nbd);
           bd.getBookingDetail().remove(obd);
        });

        oldBookingDetailsFlightLeg.forEach(fl ->
                fl.setBookingDetail(nbd)
        );

        oldBD.setPassengers(null);
        oldBD.setPnrs(null);
        oldBD.setFlightLegs(null);
        oldBD.setBags(null);
        nbd.getPassengers().addAll(oldBookingDetailsPassenger);
        nbd.getPnrs().addAll(oldBookingDetailsPnr);
        nbd.getFlightLegs().addAll(oldBookingDetailsFlightLeg);
        nbd.getBags().addAll(oldBookingDetailsBags);
        bookingDetailRepository.save(nbd);
        bookingDetailRepository.delete(obd);
        return nbd;
    }
}
