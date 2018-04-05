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
import gov.gtas.services.BookingDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    @PersistenceContext
    private EntityManager entityManager;

    @Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
    public void jobScheduling() throws IOException {

        ArrayList<Long> _idsToRemove = new ArrayList<Long>();
        List<BookingDetail> _tempList = bookingDetailRepository.getBookingDetailByProcessedFlag();
        logger.debug("Booking Detail collection size -- "+String.valueOf(_tempList.size()));
        BookingDetail _tempBD = new BookingDetail();
        List<BookingDetail> _tempQueryResults = new ArrayList<BookingDetail>();
        List<BookingDetail> _tempBDListNoDuplicates = new ArrayList<>();
        List<BookingDetail> _tempTBDList = new ArrayList<>();
        List<BookingDetail> _tempTBSList = new ArrayList<>();

        //filter out duplicates from the un-processed records
        _tempBDListNoDuplicates = _tempList.stream()
                .distinct()
                .collect(Collectors.toList());

        for(BookingDetail bd : _tempBDListNoDuplicates){
            _tempQueryResults = bookingDetailRepository.getSpecificBookingDetail(bd.getFlightNumber(), bd.getOrigin(), bd.getDestination(),
                                                                                 bd.getEtaDate(), bd.getEtdDate(), Boolean.TRUE);
            if(_tempQueryResults.size()>0){
                // found existing Booking Detail, line up existing for deletion
                _idsToRemove.add(bd.getId());
                bd.setPassengers(null);
                bd.setPnrs(null);
                _tempTBDList.add(bd);


            }else{
                // did not find one, make the first BookingDetail the pertinent processed one, line up the rest for deletion
                bd.setProcessed(Boolean.TRUE);
                _tempTBSList.add(bd);
            }
        }

        try {
            processTBSBookingDetailList(_tempTBSList);
            processTBSBookingDetailList(_tempTBDList);
            processTBDBookingDetailList(_tempTBDList);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        logger.debug("Booking Detail processing complete -- ");

    } // end of job scheduling


    /**
     * This method interacts with the DB and removes TBD records from BookingDetail table
     * @param _tempList
     */
    protected void processTBDBookingDetailList(List<BookingDetail> _tempList) throws Exception{

        // Delete TBD records
        if(null != _tempList) {
            for(BookingDetail bd : _tempList) {
                try {
                    processTBDBookingDetail(bd);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        logger.debug("Booking Detail collection marked delete -- "+String.valueOf(_tempList.size()));

    }

    /**
     * This method interacts with the DB and removes TBD records from BookingDetail table
     * @param bd
     */
    protected void processTBDBookingDetail(BookingDetail bd) throws Exception{

        // Delete TBD records
        if(null != bd) {
            try {
                bookingDetailRepository.delete(bd);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method interacts with the DB and saves ToBeSaved records from BookingDetail table
     * @param _tempList
     */
    private void processTBSBookingDetailList(List<BookingDetail> _tempList) throws Exception{

        // Save TBD records
        if(null != _tempList) {
            bookingDetailRepository.save(_tempList);
        }
        //logger.debug("Booking Detail collection marked save -- "+String.valueOf(_tempList.size()));

    }

}
