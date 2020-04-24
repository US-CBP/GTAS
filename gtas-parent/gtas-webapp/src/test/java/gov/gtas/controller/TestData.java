/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.controller;

import java.sql.SQLException;
import java.util.*;

import gov.gtas.model.*;

import javax.sql.rowset.serial.SerialClob;

public class TestData {

	public static Set<Document> getDocumentSet() {
		Set<Document> documentSet = new HashSet<>();
		Document d = new Document();
		d.setIssuanceCountry("USA");
		d.setId(1L);
		d.setDocumentType("P");
		d.setNumberOfDaysValid(1);
		d.setPassengerId(1L);
		documentSet.add(d);
		return documentSet;
	}

	public static ApisMessage getApisMessage() {
		ApisMessage apisMessage = new ApisMessage();
		EdifactMessage edifactMessage = new EdifactMessage();
		edifactMessage.setTransmissionSource("test");
		edifactMessage.setVersion("02b");
		edifactMessage.setMessageType("APIS");
		edifactMessage.setTransmissionDate(new Date());
		apisMessage.setEdifactMessage(edifactMessage);

		FlightPax flightPax = getFlightPax();

		apisMessage.setFlightPaxList(Collections.singleton(flightPax));

		Phone phone = getPhone();

		apisMessage.setPhones(Collections.singleton(phone));

		Set<Passenger> passengerSet = new HashSet<>();
		passengerSet.add(getPassenger());
		apisMessage.setPassengers(passengerSet);
		return apisMessage;
	}

	public static Phone getPhone() {
		Phone phone = new Phone();
		phone.setNumber("12341234");
		phone.setFlightId(2L);
		phone.setId(1L);
		phone.setChangeDate();
		return phone;
	}

	public static FlightPax getFlightPax() {
		FlightPax flightPax = new FlightPax();
		flightPax.setBagWeight(123D);
		flightPax.setFlight(new Flight());
		flightPax.setBagCount(5);

		flightPax.setMessageSource("APIS");
		flightPax.setDebarkation("FOO");
		return flightPax;
	}

	public static Flight getFlight() {
		MutableFlightDetails mutableFlightDetails = new MutableFlightDetails();
		mutableFlightDetails.setFlightId(2L);
		Date changeMe = new Date();
		mutableFlightDetails.setEta(changeMe);
		mutableFlightDetails.setEtaDate(changeMe);
		mutableFlightDetails.setEtd(changeMe);
		mutableFlightDetails.setLocalEtdDate(changeMe);
		mutableFlightDetails.setLocalEtaDate(changeMe);
		Flight flight = new Flight();
		flight.setMutableFlightDetails(mutableFlightDetails);
		flight.setId(2L);
		flight.setDirection("I");
		flight.setFlightNumber("1234");
		flight.setCarrier("AA");
		flight.setOrigin("IAD");
		flight.setDestination("IAD");
		flight.setIdTag("12341234");
		return flight;
	}

	public static Passenger getPassenger() {
		Passenger wally = new Passenger();
		wally.setId(1L);

		PassengerDetails passengerDetails = new PassengerDetails();
		passengerDetails.setPassenger(wally);
		passengerDetails.setId(1L);
		passengerDetails.setPassengerId(1L);
		passengerDetails.setGender("M");
		passengerDetails.setAge(5);
		passengerDetails.setLastName("HUND");
		passengerDetails.setFirstName("WALLY");
		passengerDetails.setNationality("USA");
		passengerDetails.setDob(new Date());
		passengerDetails.setPassengerType("P");
		passengerDetails.setMiddleName("ZE");
		passengerDetails.setResidencyCountry("USA");
		passengerDetails.setSuffix("LITTLE");
		passengerDetails.setTitle("Awesome");

		PassengerTripDetails passengerTripDetails = new PassengerTripDetails();
		passengerTripDetails.setId(1L);
		passengerTripDetails.setPassengerId(1L);
		passengerTripDetails.setCoTravelerCount(1);
		passengerTripDetails.setEmbarkation("US");
		passengerTripDetails.setEmbarkCountry("USA");
		passengerTripDetails.setHoursBeforeTakeOff(1);
		passengerTripDetails.setBagNum("Foo");
		passengerTripDetails.setTotalBagWeight("123");
		PassengerIDTag passengerIDTag = new PassengerIDTag();
		passengerIDTag.setIdTag("fooey");
		wally.setPassengerIDTag(passengerIDTag);

		FlightPax flightPax = getFlightPax();
		flightPax.setMessageSource("PNR");
		flightPax.setPassenger(wally);
		flightPax.setFlight(getFlight());

		wally.setFlightPaxList(Collections.singleton(flightPax));
		wally.setPassengerDetails(passengerDetails);
		wally.setPassengerTripDetails(passengerTripDetails);

		Set<Document> documentSet = getDocumentSet();
		for (Document d : documentSet) {
			d.setPassenger(wally);
			d.setDocumentNumber("12341234");
		}

		Seat seat = makeSeat();
		seat.setPassenger(wally);
		seat.setPassengerId(2L);
		Set<Seat> seatList = new HashSet<>();
		seatList.add(seat);

		wally.setSeatAssignments(seatList);

		wally.setDocuments(documentSet);

		return wally;
	}

	public static List<Bag> getBags() {
		List<Bag> bagList = new ArrayList<>();
		Bag bag = new Bag();
		bag.setBagId("12341234");
		bag.setPassenger(getPassenger());
		bag.setPrimeFlight(true);
		bag.setData_source("APIS");
		bagList.add(bag);

		Bag bag2 = new Bag();
		bag2.setBagId("12341234");
		bag2.setPassenger(getPassenger());
		bag2.setPrimeFlight(true);
		bag2.setData_source("PNR");
		bagList.add(bag2);

		return bagList;

	}

	public static Seat makeSeat() {
		Seat seat = new Seat();
		seat.setNumber("123");
		seat.setApis(false);
		seat.setId(1L);
		return seat;
	}

	public static BookingDetail bookingDetail() {
		BookingDetail bookingDetails = new BookingDetail();
		bookingDetails.setId(22L);
		bookingDetails.setFlightNumber("501");
		bookingDetails.setOrigin("IAD");
		bookingDetails.setDestination("ADD");
		bookingDetails.setEtdDate(Calendar.getInstance().getTime());
		bookingDetails.setEtaDate(Calendar.getInstance().getTime());
		return bookingDetails;
	}

	public static List<Pnr> getPnrList() throws SQLException {
		List<Pnr> pnrList = new ArrayList<>();
		Pnr pnr = new Pnr();
		pnr.setId(1L);
		pnr.setCarrier("AA");
		pnr.setBagCount(1);
		pnr.setDaysBookedBeforeTravel(4);
		pnr.setRecordLocator("12341234");
		pnr.setOrigin("USA");
		pnr.setBaggageWeight(132D);
		pnr.setDateBooked(new Date());
		pnr.setDateReceived(new Date());
		pnr.setDepartureDate(new Date());
		pnr.setReservationCreateDate(new Date());
		pnr.setCreateDate(new Date());
		BookingDetail bd = bookingDetail();
		Set<BookingDetail> bookingDetailSet = new HashSet<>();
		bookingDetailSet.add(bd);
		pnr.setBookingDetails(bookingDetailSet);
		pnr.setRaw(new SerialClob(getSpecDocPage75().toCharArray()));

		Set<Passenger> passengerSet = new HashSet<>();
		passengerSet.add(getPassenger());
		pnr.setPassengers(passengerSet);

		pnr.setPhones(Collections.singleton(getPhone()));
		pnr.setAddresses(Collections.singleton(getAddress()));
		pnr.setEmails(Collections.singleton(getEmail()));
		pnr.setFrequentFlyers(Collections.singleton(getFrequentFlyer()));
		pnr.setAgencies(Collections.singleton(getAgency()));
		pnr.setCreditCards(Collections.singleton(getCreditCard()));

		List<FlightLeg> flightLegs = new ArrayList<>();
		flightLegs.add(getFlightLeg());
		FlightLeg flightLeg = getFlightLeg();
		flightLeg.setFlight(null);
		flightLeg.setBookingDetail(bookingDetail());
		flightLegs.add(flightLeg);
		pnr.setFlightLegs(flightLegs);

		EdifactMessage edifactMessage = new EdifactMessage();
		edifactMessage.setTransmissionDate(new Date());
		edifactMessage.setMessageType("PNR");
		edifactMessage.setVersion("123");
		edifactMessage.setTransmissionSource("TestData");
		pnr.setEdifactMessage(edifactMessage);

		pnrList.add(pnr);

		return pnrList;
	}

	public static Email getEmail() {
		Email email = new Email();
		email.setAddress("123");
		email.setDomain("@asdsad.com");
		email.setCreatedAt(new Date());
		return email;
	}

	// Frequent Flyer
	public static FrequentFlyer getFrequentFlyer() {
		FrequentFlyer frequentFlyer = new FrequentFlyer();
		frequentFlyer.setCarrier("AA");
		frequentFlyer.setNumber("123412341234");
		frequentFlyer.setId(2L);
		return frequentFlyer;
	}

	// Agency
	public static Agency getAgency() {
		Agency agency = new Agency();
		agency.setCountry("USA");
		agency.setCity("Foo");
		agency.setIdentifier("123");
		agency.setPhone("123123213");
		return agency;
	}

	// Credit Card
	public static CreditCard getCreditCard() {
		CreditCard creditCard = new CreditCard();
		creditCard.setNumber("12341234");
		creditCard.setAccountHolder("asdasd");
		creditCard.setCardType("VV");
		creditCard.setFlightId(1L);
		return creditCard;
	}

	// Flight Leg
	public static FlightLeg getFlightLeg() {
		FlightLeg flightLeg = new FlightLeg();
		flightLeg.setFlight(getFlight());
		flightLeg.setLegNumber(1);
		flightLeg.setId(1L);
		return flightLeg;
	}

	public static Address getAddress() {
		Address address = new Address();
		address.setCountry("USA");
		address.setCity("SPRINGFIELD");
		address.setLine1("123 JUMP STREET");
		address.setFlightId(2L);
		address.setPostalCode("12367");
		return address;
	}

	public static String getSpecDocPage75() {

		return "UNA:+.\\*'\n" + "UNB+IATA:1+1A+KRC+130527:0754+0003'\n" + "UNH+1+PNRGOV:11:1:IA+270513/0754/SQ/609'\n"
				+ "MSG+:22'\n" + "ORG+1A:MUC'\n" + "TVL+270513:1640:270513:2200+ICN+SIN+SQ+609'\n" + "EQN+2'\n"
				+ "SRC'\n" + "RCI+1A:2LS6KP::200513:0439+KE:EDP2RW'\n" + "DAT+700:270513:0718'\n"
				+ "ORG+1A:MUC+:HDQKE2400+NBE+KE:NBE+A+KR+GNPD+003956+94'\n" + "TIF+PARK:I+SEJOONMR::1'\n"
				+ "FTI+SQ:314655277:::::G'\n" + "IFT+4:63::SQ'\n" + "REF+:001435199A918A76'\n"
				+ "SSR+DOCS:HK:1:SQ:::::/P/KR/JR3364288/KR/10SEP72/M/05JUL16/PARK/SEJOON’\n"
				+ "TVL+270513:1640:270513:2200+ICN+SIN+KE:SQ+609:B'\n" + "RPI+1+HK'\n" + "APD+333'\n"
				+ "SSR+NSSA:HN:1:SQ:::ICN:SIN+::1'\n"
				+ "SSR+CKIN:HK:1:SQ:::ICN:SIN:10KG EXBG WAIVER AUTH BY MDPRM+::1'\n"
				+ "SSR+DOCS:HK:1:SQ:::ICN:SIN:/P/KOR/JR3364288/KOR/10SEP72/M/05JUL16/PARK/SE JOON/+::1'\n"
				+ "RCI+1A:2LS6KP::200513:0439+KE:EDP2RW'\n" + "TVL+++++303:Y'\n" + "DAT'\n" + "ORG+SQ++++A'\n"
				+ "TRI++ICN-188:::1'\n" + "TIF+PARK:I+SEJOONMR::1'\n" + "SSD+039G++++Y'\n"
				+ "TBD++2:34:700++MP+618:1026000001:2:MPM+618:1026000002:3:MPM'\n" + "LTS+0/O/NM/PARK/SEJOONMR'\n"
				+ "LTS+0/O/SS/SQ 609 E 27MAY 1 ICNSIN LK1 1640 2200/LK \\*1A/E\\*/KE/KR/C/I/CAB Y//2/0001//// /Y 621/B 3//AY 914/EY 908/ICNJNB/E'\n"
				+ "LTS+14/A/SR/SSR DOCSSQHK1\n" + "P/KR/JR3364288/KR/10SEP72/M/05JUL16/PARK/SE JOON/PARK/SEJOONMR'\n"
				+ "LTS+14/Z/SELRMKE 210921 CR-SEL RM KE 21MAY0921Z'\n"
				+ "LTS+47/Z//DCS-SYNCUS CR-ICNSQ00CS 00000000 PD 6017GN/DS-9CBABA8A27MAY0718Z'\n" + "SRC'\n"
				+ "RCI+1A:X49V9U::210113:0411+OZ:2OX5VV’\n" + "DAT+700:270513:0726'\n"
				+ "ORG+1A:MUC+32393340:SINSQ08AA+NCE+SQ:NCE+A+SG+HJGS+CFDEA9+9TIF+KIM:I+KONG CHUN MRS:A:3'\n"
				+ "FTI+SQ:223422444'\n" + "IFT+4:63::SQ'\n" + "REF+:0010350E7830159A'\n"
				+ "SSR+DOCS:HK:1:SQ:::::/P/KOR/M17072944/KOR/15FEB57/F/23MAR19/KIM/KONG CHUN/'\n"
				+ "TVL+270513:1640:270513:2200+ICN+SIN+OZ:SQ+609:Z'\n" + "RPI+1+HK'\n" + "APD+333'\n"
				+ "SSR+RQST:HK:1:SQ:::ICN:SIN+14A::3'\n"
				+ "SSR+DOCS:HK:1:SQ:::ICN:SIN:/P/KOR/M17072944/KOR/15FEB57/F/23MAR19/KIM/KONG CHUN/+::3'\n"
				+ "RCI+1A:X49V9U::210113:0411+OZ:2OX5VV’\n" + "TVL+++++752:D'\n" + "DAT'\n" + "ORG+SQ++++A'\n"
				+ "TRI++ICN-229:::3'\n" + "TIF+KIM:I+KONG CHUN MRS:A:3'\n" + "SSD+014A++++J'\n"
				+ "TBD++2:29:700++MP+618:0000290138:1:SIN+618: 0000290139:3:SIN'\n"
				+ "LTS+0/S/PARK/KWANG SOO MR(ADT) -YHD2DT'\n"
				+ "LTS+0/Z/PARK HYUNJU CR-SELSQ01G0 17381302 GS 5477EK/RO-9CB3A23DMUCPI2SQ1 00000000 21JAN0411Z'\n"
				+ "LTS+65/Z//DCS-SYNCUS CR-ICNSQ00CI 00000000 GS 6058HJ/DS-9CBAB94527MAY0726Z'\n" + "UNT+336+1'\n"
				+ "UNZ+1+0003'";

	}

}
