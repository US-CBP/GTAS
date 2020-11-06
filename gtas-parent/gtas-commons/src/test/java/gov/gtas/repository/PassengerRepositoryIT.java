package gov.gtas.repository;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.HashSet;

import javax.transaction.Transactional;

import gov.gtas.model.PassengerDetails;
import gov.gtas.model.PassengerTripDetails;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.services.dto.FlightsRequestDto;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@Rollback(true)
public class PassengerRepositoryIT extends AbstractTransactionalJUnit4SpringContextTests {

  @Autowired
  private PassengerRepository paxrepo;

  @Autowired
  private FlightRepository flightrepo;

  Flight savedFlight;
  Passenger savedpax;

  @Before
  public void setUp() throws Exception {
    // DomesticFlight 1
    Flight f = new Flight();
    f.setCarrier("XX");
    f.setDirection("O");
    f.setFlightNumber("0012");
    f.setFullFlightNumber("XX0012");
    f.setOrigin("LAX");
    f.setDestination("JFK");
    f.setOriginCountry("USA");
    f.setDestinationCountry("USA");

    Passenger p = new Passenger();
    p.setPassengerDetails(new PassengerDetails(p));
    p.setPassengerTripDetails(new PassengerTripDetails(p));
    p.setDocuments(new HashSet<>());
    p.getPassengerDetails().setPassengerType("P");
    p.getPassengerDetails().setFirstName("john");
    p.getPassengerDetails().setLastName("doe");

    savedFlight = flightrepo.save(f);
    p.setFlight(savedFlight);

    savedpax = paxrepo.save(p);
  }

  @Test
  @Transactional
  public void testFindByFlightId() {
    List<Passenger> passengers = paxrepo.findByFlightId(savedFlight.getId());

    assertTrue(passengers.get(0).getId() == savedpax.getId());
    assertTrue(passengers.get(0).getPassengerDetails().getFirstName().equalsIgnoreCase("john"));
    assertTrue(passengers.get(0).getPassengerDetails().getLastName().equalsIgnoreCase("doe"));
  }
}
