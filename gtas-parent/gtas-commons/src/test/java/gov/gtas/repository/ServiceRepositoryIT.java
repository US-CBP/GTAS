/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.MutableFlightDetails;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.Airport;
import gov.gtas.model.lookup.Carrier;
import gov.gtas.model.lookup.Country;
import gov.gtas.model.lookup.DocumentTypeCode;
import gov.gtas.model.lookup.PassengerTypeCode;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class,
        CachingConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceRepositoryIT {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRepositoryIT.class);

    @Autowired
    private FlightService testTarget;

    @Autowired
    private LookUpRepository lookupDao;

    @Autowired
    private ApisMessageRepository apisMessageRepository;
    
    @Autowired
    private PassengerService passengerService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    // @Test()
    public void testAddFlight() {
        Flight f = new Flight();
        f.setCreatedAt(new Date());
        f.setCreatedBy("JUNIT");
        // Airport a = new Airport(3616l,"Washington","IAD","KAID");
        String a = "IAD";
        logger.info(a);
        f.setOrigin(a);
        String b = "JFK";
        // Airport b = new Airport (3584l,"Atlanta","ATL","KATL");

        f.setDestination(b);
        MutableFlightDetails mutableFlightDetails = new MutableFlightDetails(f.getId());
        mutableFlightDetails.setEta(new Date());
        mutableFlightDetails.setEtd(new Date("6/20/2015"));
        f.setMutableFlightDetails(mutableFlightDetails);
        f.setFlightNumber("8002");

        String c = "US";
        f.setDestinationCountry(c);
        f.setOriginCountry(c);

        String cr = "AA";
        f.setCarrier(cr);
        f.setUpdatedAt(new Date());
        f.setUpdatedBy("TEST");

        Passenger passengerToUpdate = new Passenger();
        passengerToUpdate.getPassengerDetails().setPassengerType(PassengerTypeCode.P.name());
        passengerToUpdate.getPassengerDetails().setAge(30);
        passengerToUpdate.getPassengerDetails().setNationality(c);
        passengerToUpdate.getPassengerTripDetails().setDebarkation(b);
        passengerToUpdate.getPassengerTripDetails().setDebarkCountry(c);
        passengerToUpdate.getPassengerDetails().setDob(new Date("04/06/1980"));
        passengerToUpdate.getPassengerTripDetails().setEmbarkation(b);
        passengerToUpdate.getPassengerTripDetails().setEmbarkCountry(c);
        passengerToUpdate.getPassengerDetails().setFirstName("Mike");
        Set hs = new HashSet<Flight>();
        hs.add(f);
        passengerService.setAllFlights(hs, passengerToUpdate.getId());
        passengerToUpdate.getPassengerDetails().setGender("M");
        passengerToUpdate.getPassengerDetails().setLastName("Copenhafer");
        passengerToUpdate.getPassengerDetails().setResidencyCountry(c);
        // passengerToUpdate.setDocuments(passenger.getDocuments());
        passengerToUpdate.getPassengerDetails().setSuffix("Mr.");
        // passengerToUpdate.setTitle(passenger.getTitle());
        // passengerToUpdate.setType(PaxType.PAX);
        passengerToUpdate.setCreatedAt(new Date());
        passengerToUpdate.setCreatedBy("JUNIT TEST");

        Document d = new Document();
        d.setDocumentType(DocumentTypeCode.P.name());
        d.setDocumentNumber("T00123456");
        d.setExpirationDate(new Date("6/6/2020"));
        d.setIssuanceDate(new Date("6/6/1999"));
        d.setIssuanceCountry(c);
        d.setPassenger(passengerToUpdate);
        Set<Document> docs = new HashSet<>();
        docs.add(d);
        passengerToUpdate.setDocuments(docs);

        Set<Passenger> passengers = new HashSet<Passenger>();
        passengers.add(passengerToUpdate);
        f.setPassengers(passengers);

        testTarget.create(f);
        logger.info("********************************************************");
        logger.info("******************Saved Flight***********************"
                        + f.toString());
        logger.info("********************************************************");

    }

    // @Test()
    public void testSecondaryCahe() {
        logger.info("\n>>>>> testSecondaryCahe <<<<<\n");

        List<Airport> airports = lookupDao.getAllAirports();
        logger.info("***********************AIRPORTS*******************************"
                        + airports.size());
        List<Carrier> carriers = lookupDao.getAllCarriers();
        logger.info("***********************CARRIERS*******************************"
                        + carriers.size());
        List<Country> countries = lookupDao.getAllCountries();
        logger.info("*************COUNTRIES*******************************"
                        + countries.size());

        // next, get data from cache without going to db
        // verification: no sql execution on console output

        airports = lookupDao.getAllAirports();
        logger.info("***********************AIRPORTS*******************************"
                        + airports.size());
        carriers = lookupDao.getAllCarriers();
        logger.info("***********************CARRIERS*******************************"
                        + carriers.size());
        countries = lookupDao.getAllCountries();
        logger.info("*************COUNTRIES*******************************"
                        + countries.size());
    }

    // @Test()
    public void testEmptyCache() {
        logger.info("\n>>>>> testEmptyCache <<<<<\n");

        List<Carrier> carriers = lookupDao.getAllCarriers();
        logger.info("***********************CARRIERS- get from db*******************************"
                        + carriers.size());

        carriers = lookupDao.getAllCarriers();
        logger.info("***********************CARRIERS- get from cache *******************************"
                        + carriers.size());

        lookupDao.clearAllEntitiesCache();
        logger.info("***********************CARRIERS- empty cache *******************************");

        carriers = lookupDao.getAllCarriers();
        logger.info("*************CARRIERS- retrieve from db again*******************************"
                        + carriers.size());

        carriers = lookupDao.getAllCarriers();
        logger.info("***********************CARRIERS- get from cache again *******************************"
                        + carriers.size());

        lookupDao.clearAllEntitiesCache();
        logger.info("***********************CARRIERS- empty cache -final *******************************\n");

    }

    private Airport createAirport(final String airportName) {
        Airport airport = new Airport();
        ReflectionTestUtils.setField(airport, "name", airportName);
        return airport;
    }

    private Country createCountry() {
        Country country = new Country();
        ReflectionTestUtils.setField(country, "name", "Test123");
        ReflectionTestUtils.setField(country, "iso2", "AJ");
        ReflectionTestUtils.setField(country, "iso3", "AJT");

        return country;
    }

    // @Test
    public void testSingleEntityCache() {
        logger.info("\n>>>>> testSingleEntityCache <<<<<\n");

        Country country = createCountry();
        lookupDao.saveCountry(country);

        logger.info("*************save country*****************************"
                        + country.getName());

        lookupDao.getCountry(country.getName());
        logger.info("*************get country from db*****************************"
                        + country.getName());
        lookupDao.getCountry(country.getName());
        logger.info("*************get country from cache*****************************"
                        + country.getName());
        lookupDao.removeCountryCache(country.getName());
        logger.info("*************delete country and remove it from cache*****************************"
                        + country.getName());
        lookupDao.getCountry(country.getName());
        logger.info("*************get country again from db*****************************"
                        + country.getName());
        lookupDao.deleteCountryDb(country);
        logger.info("************* Done***********************\n");
    }


//    @Test
//    @Transactional
//    public void testGetApisMessageByHashcode() {
//        final String hash = "1122233";
//        TextUtils.getMd5Hash(payload, StandardCharsets.US_ASCII);
//        ApisMessage m = new ApisMessage();
//        m.setHashCode(hash);
//        m.setCreateDate(new Date());
//        m.setFilePath("/tmp/nothing.txt");
//        apisMessageRepository.save(m);
//
//        ApisMessage m2 = apisMessageRepository.findByHashCode(hash);
//        assertEquals(m, m2);
//    }
}
