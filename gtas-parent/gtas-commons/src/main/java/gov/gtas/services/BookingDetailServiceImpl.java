/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.BookingDetail;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.util.EntityResolverUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class BookingDetailServiceImpl implements BookingDetailService{

    private static final Logger logger = LoggerFactory.getLogger(BookingDetailServiceImpl.class);

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private PassengerService passengerService;

    @Override
    public List<BookingDetail> getBookingDetailsByProcessedFlag() {
        return null;
    }

    @Override
    public List<BookingDetail> getBookingDetailsByPassengers(Long pax_id) throws Exception {
        List<BookingDetail> _bd = new ArrayList<>();
        _bd = bookingDetailRepository.getBookingDetailsByPassengers(pax_id);
        return _bd;
    }

    /**
     *
     * @param pax_id
     */
    private void getHashFromPaxID(Long pax_id){
        Passenger _tempPax = passengerService.findById(pax_id);
        try {
            //String _paxHash = (new LoaderUtils()).getHashForPassenger(_tempPax);
        }catch (Exception ex){
            logger.error("Error getting Hash from PAXID", ex);
        }
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
            BookingDetail _tempBD = uniqueBookingDetails.stream()
                    .filter(bd1 -> bd1.equals(bd))
                    .findFirst().orElse(null);
            if (null == _tempBD) {
                uniqueBookingDetails.add(bd);
            } else {
                try {
                    transferBDData(_tempBD, bd);
                } catch (Exception ignored) {
                    logger.error("failed booking a booking detail with error: " + ignored.getMessage() );
                }
            }
        }
        return uniqueBookingDetails;
    }

    @Override
    @Transactional
    public BookingDetail transferBDData(BookingDetail newBD, BookingDetail oldBD) {
        BookingDetail nbd = bookingDetailRepository.save(newBD);
        BookingDetail obd = bookingDetailRepository.save(oldBD);


        Set<Passenger> oldBookingDetailsPassenger = obd.getPassengers();
        Set<FlightLeg> oldBookingDetailsFlightLeg = obd.getFlightLegs();
        Set<Pnr> oldBookingDetailsPnr = obd.getPnrs();

        oldBookingDetailsPnr.forEach(bd -> {
            bd.getBookingDetails().add(nbd);
            bd.getBookingDetails().remove(obd);
        });

        oldBookingDetailsFlightLeg.forEach(fl ->
                fl.setBookingDetail(nbd)
        );

        oldBD.setPassengers(null);
        oldBD.setPnrs(null);
        oldBD.setFlightLegs(null);
        nbd.getPassengers().addAll(oldBookingDetailsPassenger);
        nbd.getPnrs().addAll(oldBookingDetailsPnr);
        nbd.getFlightLegs().addAll(oldBookingDetailsFlightLeg);
        bookingDetailRepository.save(nbd);
        bookingDetailRepository.delete(obd);
        return nbd;
    }
    /**
     * Util method to hash passenger attributes
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
//        MessageDigest md = MessageDigest.getInstance("SHA1");
//        md.reset();
//        byte[] buffer = input.getBytes("UTF-8");
//        md.update(buffer);
//        byte[] digest = md.digest();
//
//        String hexStr = "";
//        for (int i = 0; i < digest.length; i++) {
//            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
//        }
//        return hexStr;
    	return EntityResolverUtils.makeSHA1Hash(input);
    }
}
