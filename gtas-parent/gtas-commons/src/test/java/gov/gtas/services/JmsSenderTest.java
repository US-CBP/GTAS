package gov.gtas.services;

import java.util.Date;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.jms.config.JmsConfiguration;
import gov.gtas.jms.config.MessagingListnerConfiguration;
import gov.gtas.jms.services.MessageReceiver;
import gov.gtas.jms.services.MessageSender;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;

//Enable/ uncomment the below when the ActiveMQ is up and running.Otherwise it will brake the build.
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class,JmsConfiguration.class,MessagingListnerConfiguration.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JmsSenderTest {

	@Autowired
	private MessageSender sender;

	@Autowired
	private MessageReceiver receiver;

	//Enable/ uncomment the below when the ActiveMQ is up and running.Otherwise it will brake the build.
	//@Test
	public void testJmsSend() {
		Pnr pnr=getPnr();
		Passenger p=new Passenger();
		p.setId(202l);
		p.setCreatedAt(new Date());
		p.setDebarkation("IAD");
		p.setCreatedBy("GTAS");
		p.setDob(new Date());
		p.setFirstName("TESTER");
		p.setLastName("GTAS");
		p.setGender("F");
		p.setPassengerType("P");
		Document d= new Document();
		d.setDocumentNumber("A1234567");
		d.setDocumentType("P");
		d.setId(202l);
		p.getDocuments().add(d);
		Flight f=getFlight();
		//p.getFlights().add(f);
		f.getPassengers().add(p);
		pnr.getFlights().add(f);
		System.out.println("-------------------SENDING--------");
		sender.sendMessage(pnr);

	}

	//Enable/ uncomment the below when the ActiveMQ is up and running.Otherwise it will brake the build.
	//@Test
	public void testJmsFileSend() {
		sender.sendFileContent("UNB+IATA:1+AA+USAPNRGOV+170112:1105+17011205051137+PNRGOV'UNH+1+PNRGOV:11:1:IA+270513/0649/SQ/602'");
	}
	private Flight getFlight(){
		Flight f=new Flight();
		f.setCarrier("AA");
		f.setCreatedAt(new Date());
		f.setDestination("IAD");
		f.setEta(new Date());
		f.setEtd(new Date());
		f.setFlightNumber("0018");
		f.setDirection("I");
		f.setCreatedBy("GTAS");
		f.setOrigin("JFK");
		f.setDestinationCountry("USA");
		f.setOriginCountry("USA");
		f.setId(101l);
		return f;
	}
	
	private Pnr getPnr(){
		Pnr p = new Pnr();
		p.setBagCount(3);
		p.setBaggageUnit("KG");
		p.setBaggageWeight(55.0);
		p.setCarrier("AA");
		p.setCreateDate(new Date());
		p.setDateBooked(new Date());
		p.setDepartureDate(new Date());
		p.setFormOfPayment("CA");
		p.setOrigin("IAD");
		p.setRecordLocator("ABC293949");
		Address a = new Address();
		a.setId(101l);
		a.setLine1("TEST ST");
		a.setPostalCode("20195");
		a.setState("VA");
		a.setCountry("USA");
		a.setCreatedAt(new Date());
		Agency ag= new Agency();
		ag.setId(100l);
		ag.setCity("ALDIE");
		ag.setCountry("USA");
		ag.setLocation("IAD");
		ag.setName("TEST AGENCY");
		ag.setPhone("8889994545");
		p.getAddresses().add(a);
		p.getAgencies().add(ag);
		return p;
	}
}
