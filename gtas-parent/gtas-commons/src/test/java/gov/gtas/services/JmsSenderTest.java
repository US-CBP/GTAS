package gov.gtas.services;

import java.util.Date;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.jms.config.JmsConfiguration;
import gov.gtas.jms.config.MessagingListnerConfiguration;
import gov.gtas.jms.services.MessageReceiver;
import gov.gtas.jms.services.MessageSender;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;

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
		Passenger p=new Passenger();
		p.setId(202l);
		p.setCreatedAt(new Date());
		p.setDebarkation("IAD");
		p.setCreatedBy("GTAS");
		p.setDob(new Date());
		p.setFirstName("Tester1");
		p.setLastName("Gtas1");
		p.setGender("F");
		p.setPassengerType("P");
		Flight f=getFlight();
		p.getFlights().add(f);
		sender.sendMessage(p);

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
}
