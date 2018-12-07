package gov.gtas.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
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
@Rollback(true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JmsSenderTest {

	private final static Logger logger = LoggerFactory.getLogger(JmsSenderTest.class);

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
		logger.info("-------------------SENDING--------");
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
	
	//@Test
	public void testJmsLoaderFileSend() {
		Path dInputDir = Paths.get("C:\\MESSAGEJMS").normalize();
		DirectoryStream.Filter<Path> filter = entry -> {
			File f = entry.toFile();
			return !f.isHidden() && f.isFile();
		};
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(
				dInputDir, filter)) {

			final Iterator<Path> iterator = stream.iterator();
			List<File> files = new ArrayList<>();
			for (int i = 0; iterator.hasNext() && i < 10; i++) {
				files.add(iterator.next().toFile());
			}
			Collections.sort(files,
					LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			files.stream().forEach(
					f -> {
						logger.info("==============LOADER SEND==============");
						Path path = Paths.get(f.getAbsolutePath());
						byte raw[] = null;
						try {
							raw = Files.readAllBytes(path);
						} catch (Exception e) {
							logger.error("unable to read file bytes", e);
						}
						String tmp = new String(raw, StandardCharsets.US_ASCII);
						sender.sendLoaderFileContent(tmp, f.getName());
					});
			stream.close();
		} catch (IOException ex) {
			logger.error("error!", ex);
		}
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
