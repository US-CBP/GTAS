/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertNotNull;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Flight;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Passenger;
//import gov.gtas.model.Pax;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.lookup.PassengerTypeCode;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.LookUpRepository;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PnrServiceIT {
	@Autowired
	private PnrService pnrService;

	@Autowired
	private FlightService testTarget;

	@Autowired
	private AirportService aService;

	@Autowired
	private CountryService cService;

	@Autowired
	private CarrierService crService;

	@Autowired
	private LookUpRepository lookupDao;

	@Autowired
	private ApisMessageRepository apisMessageRepository;

	@Autowired
	private PassengerService passengerService;
	
	@Test
	@Transactional
	public void testPnrSave() {
		Flight f = new Flight();
		prepareFlightData(f);
		Passenger passengerToUpdate = new Passenger();
		preparePassengerData(passengerToUpdate);
		Pnr pnr = new Pnr();
		preparePnr(pnr);
		passengerToUpdate.getPnrs().add(pnr);
		//passengerToUpdate.getFlights().add(f);
		pnr.getPassengers().add(passengerToUpdate);
		//f.getPassengers().add(passengerToUpdate);
		pnr.getFlights().add(f);
		pnrService.create(pnr);
		assertNotNull(pnr.getId());
		//pnrService.delete(pnr.getId());
	}

	private void preparePnr(Pnr pnr) {
		pnr.setCreateDate(new Date("7/7/2015"));
		pnr.setFilePath("toHeaven");
		pnr.setStatus(MessageStatus.LOADED);
		pnr.setBagCount(2);
		pnr.setDateBooked(new Date("7/7/2015"));
		pnr.setDateReceived(new Date("7/7/2015"));
		String cr = "AA";
		pnr.setCarrier(cr);
		pnr.setDaysBookedBeforeTravel(30);
		pnr.setDepartureDate(new Date());
		pnr.setFormOfPayment("CC");
		String a = "IAD";
		String c = "US";
		pnr.setOrigin(a);
		pnr.setOriginCountry(c);
		pnr.setPassengerCount(1);
		CreditCard cc = new CreditCard();
		cc.setExpiration(new Date("4/17/2015"));
		cc.setAccountHolder("Srinivasarao Vempati");
		cc.setNumber("6666-3333-9999-5555");
		cc.setCardType("VISA");
		pnr.addCreditCard(cc);
		Address add = new Address();
		add.setCity("ALDIE");
		add.setCountry("USA");
		add.setLine1("41000 Zirocn dr");
		add.setPostalCode("20105");
		add.setState("VA");
		add.setCreationDate();
		add.setCreatedBy("JUNIT");
		pnr.addAddress(add);
		Set adds = new HashSet<Address>();
		adds.add(add);
		pnr.setAddresses(adds);
		Phone p = new Phone();
		p.setNumber("555555555");
		pnr.addPhone(p);
		p.setCreationDate();
		p.setCreatedBy("JUNIT");
		Set phones = new HashSet();
		phones.add(p);
		pnr.setPhones(phones);
		Agency ag = new Agency();
		ag.setLocation("STERLING");
		ag.setCountry("USA");
		ag.setIdentifier("123456C");
		ag.setName("Some Test Agency");
		ag.setCreatedAt(new Date());
		FrequentFlyer ff = new FrequentFlyer();
		ff.setNumber("7777");
		ff.setCarrier("AA");
		ff.setCreatedAt(new Date());
		ff.setCreatedBy("JUNIT");
		pnr.addFrequentFlyer(ff);
	}

	private void prepareFlightData(Flight f) {
		f.setDirection("I");
		f.setCreatedAt(new Date());
		f.setCreatedBy("JUNIT");
		String a = "IAD";
		System.out.println(a);
		f.setOrigin(a);
		String b = "JFK";
		f.setDestination(b);
		f.setEta(new Date());
		f.setEtd(new Date("7/31/2015"));
		f.setFlightDate(new Date());
		f.setFlightNumber("528");
		String c = "US";
		f.setDestinationCountry(c);
		f.setOriginCountry(c);
		String cr = "AA";
		f.setCarrier(cr);
		f.setUpdatedAt(new Date());
		f.setUpdatedBy("TEST");
	}

	private void preparePassengerData(Passenger passengerToUpdate) {
		passengerToUpdate.setPassengerType(PassengerTypeCode.P.name());
		passengerToUpdate.setAge(30);
		String c = "US";
		String b = "JFK";
		passengerToUpdate.setCitizenshipCountry(c);
		passengerToUpdate.setDebarkation(b);
		passengerToUpdate.setDebarkCountry(c);
		passengerToUpdate.setDob(new Date("04/06/1966"));
		passengerToUpdate.setEmbarkation(b);
		passengerToUpdate.setEmbarkCountry(c);
		passengerToUpdate.setFirstName("Srinivas");
		passengerToUpdate.setLastName("Test");
		passengerToUpdate.setCreatedBy("JUNIT");
		passengerToUpdate.setCreationDate();
		passengerToUpdate.setGender("M");
		passengerToUpdate.setSuffix("Jr");
		passengerToUpdate.setTitle("Mr");
		passengerToUpdate.setResidencyCountry(c);
	}
}
