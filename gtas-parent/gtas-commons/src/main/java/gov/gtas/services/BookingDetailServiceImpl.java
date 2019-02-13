/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.BookingDetail;
import gov.gtas.model.Passenger;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.util.EntityResolverUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
