/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.BookingDetail;
import gov.gtas.model.Passenger;
import gov.gtas.repository.BookingDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BookingDetailServiceImpl implements BookingDetailService{

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

    private void getHashFromPaxID(Long pax_id){
        Passenger _tempPax = passengerService.findById(pax_id);

    }


    /**
     * Util method takes top 5 attributes for a Passenger and returns a hash
     * @param firstName
     * @param lastName
     * @param gender
     * @param DOB
     * @param ctz_country
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String getHashForPassenger(String firstName, String lastName, String gender, String DOB, String ctz_country) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        return makeSHA1Hash(String.join("", Arrays.asList(firstName.toUpperCase(), lastName.toUpperCase(), gender.toUpperCase(), DOB, ctz_country.toUpperCase())));
    }

    /**
     * Util method takes a Passenger object and return a hash for the top 5 attributes
     * @param pax
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String getHashForPassenger(Passenger pax) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        return makeSHA1Hash(String.join("", Arrays.asList(pax.getFirstName().toUpperCase(), pax.getLastName().toUpperCase(),
                pax.getGender().toUpperCase(), pax.getDob().toString(), pax.getCitizenshipCountry().toUpperCase())));
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
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.reset();
        byte[] buffer = input.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return hexStr;
    }
}
