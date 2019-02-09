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
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BookingDetailCompressScheduler {

    private static final Logger logger = LoggerFactory
            .getLogger(BookingDetailCompressScheduler.class);

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    @Scheduled(fixedDelayString = "10000000", initialDelayString = "10000000")
    public void jobScheduling() throws IOException {

        ArrayList<Long> _idsToRemove = new ArrayList<Long>();
        List<BookingDetail> _tempList = bookingDetailRepository.getBookingDetailByProcessedFlag();
        logger.info("Booking Detail compress START , collection size -- "+String.valueOf(_tempList.size()));
        BookingDetail _tempBD = new BookingDetail();
        List<BookingDetail> _tempQueryResults = new ArrayList<BookingDetail>();
        List<BookingDetail> tempBDList = new ArrayList<>();
        List<BookingDetail> tempSimilarBDList = new ArrayList<>();
        List<BookingDetail> _tempTBDList = new ArrayList<>();
        List<BookingDetail> _tempTBSList = new ArrayList<>();
        List<BookingDetail> listOfUniques = new ArrayList<>();
        List<BookingDetail> listOfDuplicates = new ArrayList<>();

        //shake off duplicates from unprocessed elements
        _tempList = _tempList.stream().sorted(Comparator.comparing(BookingDetail::getId)).collect(Collectors.toList());
        filterDuplicates(_tempList, listOfUniques, listOfDuplicates);
        _tempTBDList.addAll(listOfDuplicates);

        for(BookingDetail bd : listOfUniques){
            _tempQueryResults = bookingDetailRepository.getSpecificBookingDetail(bd.getFlightNumber(), bd.getOrigin(), bd.getDestination(),
                                                                                 bd.getEtaDate(), bd.getEtdDate(), Boolean.TRUE);
            if(_tempQueryResults.size()>0){
                // found existing Booking Detail, line up existing for deletion
               // _tempQueryResults.stream().filter()
                _tempQueryResults = _tempQueryResults.stream().sorted(Comparator.comparing(BookingDetail::getId)).collect(Collectors.toList());
                //apply this bookingdetail to Pax and PNR
                applyBDPaxPnr(_tempQueryResults.get(0), bd);

                _tempTBSList.add(_tempQueryResults.get(0));
                _tempTBDList.add(bd);

            } else{

                // tempSimilarBDList will contain all the similar records as this BD object
                // no match in processed entries,so make the first record in this sorted list the BD to stay around
                // mark the other similar ones to deletion after updating Pax and PNR join tables
                _tempTBSList.add(bd);
            }
        }

        try {
            //_tempTBSList =
            _tempTBSList.parallelStream().forEach(x -> x.getPnrs().stream().forEach(y -> y.getFlights()));
            _tempTBSList.parallelStream().forEach(x -> x.getFlightLegs());

            upsertDeleteRecords(_tempTBSList, _tempTBDList);

        }catch (Exception ex){
            logger.error("Error in Booking Detail compress:", ex);
        }
        logger.info("Booking Detail compress processing COMPLETE");

    } // end of job scheduling

    @Transactional
    public void upsertDeleteRecords(List<BookingDetail> _tempTBSList, List<BookingDetail>_tempTBDList) throws Exception{
        _tempTBSList.parallelStream().forEach(x->x.setProcessed(Boolean.TRUE));
        processTBSBookingDetailList(_tempTBSList);
        processTBDBookingDetailList(_tempTBDList);

    }

    /**
     *
     * @param listContainingDuplicates
     * @param listOfUniques
     * @param listOfDuplicates
     */
    private void filterDuplicates(List<BookingDetail> listContainingDuplicates, List<BookingDetail> listOfUniques, List<BookingDetail> listOfDuplicates) {

        final Set<BookingDetail> setToReturn = new HashSet<BookingDetail>();
        final List<BookingDetail> listBD = new ArrayList<>();

        for (BookingDetail bd : listContainingDuplicates) {
            BookingDetail _tempBD = listOfUniques.stream()
                    .filter(bd1 -> (bd1.getFlightNumber().equalsIgnoreCase(bd.getFlightNumber()))
                            && (bd1.getOrigin().equalsIgnoreCase(bd.getOrigin()))
                            && (bd1.getDestination().equalsIgnoreCase(bd.getDestination()))
                            && (bd1.getEtaDate().equals(bd.getEtaDate()))
                            && (bd1.getEtdDate().equals(bd.getEtdDate())))
                    .findFirst().orElse(null);

            if (null == _tempBD) {
                listOfUniques.add(bd);
            } else {
                listOfDuplicates.add(bd);
                //apply Pax and PNR mappings to unique bds
                applyBDPaxPnr(_tempBD, bd);
            }
        }
    }

    /**
     *
      * @param newBD
     * @param oldBD
     */
    private void applyBDPaxPnr(BookingDetail newBD, BookingDetail oldBD){

                newBD.getPassengers().addAll(oldBD.getPassengers());

                oldBD.getPnrs().stream().forEach(x->x.getBookingDetails().add(newBD));
                newBD.getPnrs().addAll(oldBD.getPnrs());
                oldBD.getFlightLegs().stream().forEach(x->x.setBookingDetail(newBD));
                newBD.getFlightLegs().addAll(oldBD.getFlightLegs());
                oldBD.getPnrs().stream().forEach(x->x.getBookingDetails().remove(oldBD));
                oldBD.setPassengers(null);
                oldBD.setPnrs(null);
    }


    /**
     * This method interacts with the DB and removes TBD records from BookingDetail table
     * @param _tempList
     */
    protected void processTBDBookingDetailList(List<BookingDetail> _tempList) throws Exception{

        // Delete TBD records
        if(null != _tempList) {
            //bookingDetailRepository.delete(_tempList);
            for(BookingDetail bd : _tempList) {
                try {
                    processTBDBookingDetail(bd);
                } catch (Exception ex) {
                    logger.error("Error processing TBD Booking Detail List", ex);
                }
            }
        }

        logger.debug("Booking Detail collection marked delete -- "+String.valueOf(_tempList.size()));

    }

    /**
     * This method interacts with the DB and removes TBD records from BookingDetail table
     * @param bd
     */
    @Transactional
    public void processTBDBookingDetail(BookingDetail bd) throws Exception{

        // Delete TBD records
        if(null != bd) {
            //try {
                bookingDetailRepository.delete(bd);
           // }catch (Exception ex){
           //     logger.error("error!", e);
           // }
        }

    }

    /**
     * This method interacts with the DB and saves ToBeSaved records from BookingDetail table
     * @param _tempList
     */
    @Transactional
    public void processTBSBookingDetailList(List<BookingDetail> _tempList) throws Exception{

        // Save TBD records
        if(null != _tempList) {
            bookingDetailRepository.save(_tempList);
            for(BookingDetail bd : _tempList) {
           //     try {
            //        bookingDetailRepository.save(bd);
            //    } catch (Exception ex) {
            //        logger.error("error!", e);
            //    }
            }
        }
        logger.debug("Booking Detail collection marked save -- "+String.valueOf(_tempList.size()));

    }

}
