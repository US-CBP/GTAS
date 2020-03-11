/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.passenger.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestData {

	public static List<CreditCardVo> getCreditCardVos() {
		List<CreditCardVo> creditCardVos = new ArrayList<>();
		CreditCardVo creditCardVo = new CreditCardVo();
		creditCardVo.setAccountHolder("Wally");
		creditCardVo.setCardType("SZ");
		creditCardVo.setExpiration(new Date());
		creditCardVo.setNumber("8675309");
		creditCardVos.add(creditCardVo);
		return creditCardVos;
	}

	public static List<PhoneVo> getPhoneVos() {
		List<PhoneVo> phoneVos = new ArrayList<>();
		PhoneVo phoneVo = new PhoneVo();
		phoneVo.setNumber("8675309");
		phoneVo.setCity("Smallville");
		return phoneVos;
	}

	public static List<EmailVo> getEmailVos() {
		List<EmailVo> emailVos = new ArrayList<>();
		EmailVo emailVo = new EmailVo();
		emailVo.setAddress("Google");
		emailVo.setDomain("GMAIL.COM");
		emailVos.add(emailVo);
		return emailVos;
	}

	public static List<FlightVoForFlightHistory> getFlightVoForFlightHistories() {
		List<FlightVoForFlightHistory> flightHistoryVos = new ArrayList<>();
		FlightVoForFlightHistory flightHistoryVo = new FlightVoForFlightHistory();
		flightHistoryVo.setBookingDetail(true);
		flightHistoryVo.setPassId("1");
		flightHistoryVo.setDestination("YZA");
		flightHistoryVo.setFlightId("FFA123");
		return flightHistoryVos;
	}

	public static DocumentVo getDocumentVo() {
		DocumentVo fakeDoc = new DocumentVo();
		fakeDoc.setDocumentNumber("12341234");
		fakeDoc.setDocumentType("P");
		fakeDoc.setExpirationDate(new Date());
		fakeDoc.setIssuanceCountry("USA");
		fakeDoc.setFirstName("Wally");
		fakeDoc.setLastName("Hund");
		return fakeDoc;
	}

	public static HitDetailVo getHitDetailVo() {
		HitDetailVo hitDetailVo = new HitDetailVo();
		hitDetailVo.setRuleTitle("Test Rule");
		hitDetailVo.setCategory("Testing");
		hitDetailVo.setFlightDate(new Date());
		hitDetailVo.setRuleConditions("Was in unit test");
		hitDetailVo.setRuleAuthor("Wally");
		hitDetailVo.setPassengerDocNumber("12341234");
		return hitDetailVo;
	}

	public static PassengerVo getPassengerVo() {
		PassengerVo wally = new PassengerVo();
		wally.setFirstName("Wally");
		wally.setLastName("Hund");
		wally.setAge(5);
		wally.setFlightDestination("JFK");
		wally.setFlightOrigin("FRA");
		wally.setGender("M");
		wally.setPaxId("1");
		wally.setCarrier("AA");
		wally.setFlightNumber("AA132");
		return wally;
	}
}
