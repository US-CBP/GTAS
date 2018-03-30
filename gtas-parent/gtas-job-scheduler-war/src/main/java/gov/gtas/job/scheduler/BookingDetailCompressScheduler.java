/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import com.sun.org.apache.xpath.internal.operations.Bool;
import gov.gtas.model.BookingDetail;
import gov.gtas.model.Passenger;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.repository.PassengerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingDetailCompressScheduler {

    private static final Logger logger = LoggerFactory
            .getLogger(BookingDetailCompressScheduler.class);

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    //@Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
    public void jobScheduling() throws IOException {

        ArrayList<Long> _idsToRemove = new ArrayList<Long>();
        List<BookingDetail> _tempList = bookingDetailRepository.getBookingDetailByProcessedFlag();
        logger.debug(String.valueOf(_tempList.size()));
        BookingDetail _tempBD = new BookingDetail();
        List<BookingDetail> _tempQueryResults = new ArrayList<BookingDetail>();

        _idsToRemove.addAll(sliceExistingBookingDetailListBySimilarity(_tempList));

        for(BookingDetail bd : _tempList){
            _tempQueryResults = bookingDetailRepository.getSpecificBookingDetail(bd.getFlightNumber(), bd.getOrigin(), bd.getDestination(),
                                                                                 bd.getEtaDate(), bd.getEtdDate(), Boolean.TRUE);
            if(_tempQueryResults.size()>0){
                // found existing Booking Detail, line up existing for deletion
                _idsToRemove.add(bd.getId());
            }else{
                // did not find one, make the first BookingDetail the pertinent processed one, line up the rest for deletion
                bd.setProcessed(Boolean.TRUE);
            }
        }



    } // end of job scheduling

    private List<Long> sliceExistingBookingDetailListBySimilarity(List<BookingDetail> _tempList){

        List<Long> _idsToRemove = new ArrayList<>();
        List<BookingDetail> _tempBDListNoDuplicates = new ArrayList<>();

       if(null != _tempList){
           _tempBDListNoDuplicates = _tempList.stream()
                   .distinct()
                   .collect(Collectors.toList());
       }
       for(BookingDetail bd:_tempList){

           if (((_tempBDListNoDuplicates.stream()
                   .filter(x -> x.equals(bd))
                   .findAny()
                   .orElse(null)) == null)) {
               _idsToRemove.add(bd.getId());
           }

       }

        return _idsToRemove;
    }



}
