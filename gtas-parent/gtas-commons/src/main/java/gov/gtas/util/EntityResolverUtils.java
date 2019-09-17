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

	public static String makeHashForPassenger(String firstName, String lastName, String gender, Date dob)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String input = String.join("", Arrays.asList(firstName.toUpperCase(), lastName.toUpperCase(),
				gender.toUpperCase(), new SimpleDateFormat("MM/dd/yyyy").format(dob)));

		return makeSHA1Hash(input);
	}

	public static String makeHashForPassenger(Passenger pax)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {

		String firstName = pax.getPassengerDetails().getFirstName();
		String lastName = pax.getPassengerDetails().getLastName();
		String gender = pax.getPassengerDetails().getGender();
		Date dob = pax.getPassengerDetails().getDob();

		return makeHashForPassenger(firstName, lastName, gender, dob);
	}

	public static String makeDocIdHashForPassenger(Passenger passenger)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String hashString = null;

		String passportNumber = "";
		String issuanceCountry = "";
		Date expirationDate = null;
		Date now = new Date();

		// make sure that we have a passport and that it is not expired.
		// People with dual citizenship and two passports will be considered two people.
		for (Document doc : passenger.getDocuments()) {
			String docType = doc.getDocumentType();
			Date docExpirationDate = doc.getExpirationDate();
			if (docType != null && docExpirationDate != null && docType.equals(DocumentTypeCode.P.toString())
					&& docExpirationDate.compareTo(now) > 0) {
				passportNumber = doc.getDocumentNumber();
				issuanceCountry = doc.getIssuanceCountry();
				expirationDate = doc.getExpirationDate();
				break;
			}
		}

		if (!passportNumber.isEmpty()) {
			hashString = makeSHA1Hash(String.join("", passportNumber, issuanceCountry, expirationDate.toString()));
		}

		return hashString;
	}
}
