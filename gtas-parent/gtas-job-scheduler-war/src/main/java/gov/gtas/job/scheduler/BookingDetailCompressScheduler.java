/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import gov.gtas.model.BookingDetail;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.services.BookingDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Comparator.comparing;

@Component
public class BookingDetailCompressScheduler {

    private static final Logger logger = LoggerFactory
            .getLogger(BookingDetailCompressScheduler.class);

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired private BookingDetailService bookingDetailService;

    @Scheduled(fixedDelayString = "${cleanup.fixedDelay.in.milliseconds}", initialDelayString = "${cleanup.initialDelay.in.milliseconds}")
    public void jobScheduling() {

        List<BookingDetail> unprocessedBookingDetails = bookingDetailRepository.getBookingDetailByProcessedFlag();
        logger.info("Booking Detail compress START , collection size -- " + unprocessedBookingDetails.size());
        long time = System.nanoTime();

        // We want to save the earliest booking detail, which we expect to have the lowest ID.
        unprocessedBookingDetails.sort(comparing(BookingDetail::getId));

        // Merge all booking details into a single booking detail list.
       List<BookingDetail> uniqueBookingDetails =  bookingDetailService.deDuplicateBookingDetails(unprocessedBookingDetails);

       // Search the database for duplicates of each booking detail. Merge when appropriate.
       List<BookingDetail> finalDBToSaveList = new ArrayList<>();
        for (BookingDetail bd : uniqueBookingDetails) {
            List<BookingDetail> checkAgainstDatabaseList = bookingDetailRepository.getSpecificBookingDetail(bd.getFlightNumber(), bd.getOrigin(), bd.getDestination(),
                    bd.getEtaDate(), bd.getEtdDate(), Boolean.TRUE);
            if (checkAgainstDatabaseList.isEmpty()) {
                finalDBToSaveList.add(bd);
            } else {
                checkAgainstDatabaseList.sort(comparing(BookingDetail::getId));
                try {
                    finalDBToSaveList.add(bookingDetailService.mergeBookingDetails(checkAgainstDatabaseList.get(0), bd));
                } catch (Exception e) {
                    logger.error("Error in Booking Detail compress:", e);
                }
            }
        }
        // calling database in loop intentionally. We do not care about performance gains of calling by list and
        // the locking created by updating can cause issues with the loader.
        for (BookingDetail bd : finalDBToSaveList) {
            if (!bd.getProcessed()) {
                bd.setProcessed(true);
                bookingDetailRepository.save(bd);
            }
        }

        if (finalDBToSaveList.isEmpty()) {
            logger.info("Nothing to process");
        } else {
            logger.info("Booking detail compress finished in " + (System.nanoTime() - time) / 1000000 + "m/s. Merged " + unprocessedBookingDetails.size()
                    + " into to " + finalDBToSaveList.size() +" distinct booking details.");
        }
    }
}
