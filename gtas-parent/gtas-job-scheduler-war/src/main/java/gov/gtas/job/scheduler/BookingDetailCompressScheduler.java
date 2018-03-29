/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.model.BookingDetail;
import gov.gtas.model.Passenger;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingDetailCompressScheduler {


    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    //@Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
    public void jobScheduling() throws IOException {

        List<BookingDetail> _tempList = bookingDetailRepository.getBookingDetailByProcessedFlag();
    }



}
