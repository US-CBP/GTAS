/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

import gov.gtas.model.Document;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.DocumentTypeCode;
import java.util.Date;

@Service
public class EntityResolverUtils {

	/**
	 * Util method to hash passenger attributes
	 * 
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String makeSHA1Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.reset();
		byte[] buffer = input.getBytes("UTF-8");
		md.update(buffer);
		byte[] digest = md.digest();

		String hexStr = "";
		for (int i = 0; i < digest.length; i++) {
			hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
		}
		return hexStr;
	}

	public static String makeHashForPassenger(Passenger pax) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String hash = "";

		hash = makeSHA1Hash(
				String.join("", Arrays.asList(pax.getPassengerDetails().getFirstName().toUpperCase(), pax.getPassengerDetails().getLastName().toUpperCase(),
						pax.getPassengerDetails().getGender().toUpperCase(), new SimpleDateFormat("MM/dd/yyyy").format(pax.getPassengerDetails().getDob()))));

		return hash;
	}
        
        public static String makeDocIdHashForPassenger(Passenger passenger) throws NoSuchAlgorithmException, UnsupportedEncodingException 
        {
            String hashString = null;
            
            String passportNumber = "";
            String issuanceCountry = "";
            Date expirationDate = null;
            Date now = new Date();
            
            // make sure that we have a passport and that it is not expired. 
            // People with dual citizenship and two passports will be considered two people.
            for (Document doc : passenger.getDocuments())
            {
                if (doc.getDocumentType().equals(DocumentTypeCode.P.toString()))
                {
                    if (doc.getExpirationDate().compareTo(now) > 0)
                    {
                      passportNumber = doc.getDocumentNumber();
                      issuanceCountry = doc.getIssuanceCountry();
                      expirationDate = doc.getExpirationDate();
                    }
                }
            }
            
            if (!passportNumber.isEmpty())
            {
               hashString =  makeSHA1Hash(String.join("", passportNumber,issuanceCountry,expirationDate.toString()));
            }
            
                    
            return hashString;
        }
}
