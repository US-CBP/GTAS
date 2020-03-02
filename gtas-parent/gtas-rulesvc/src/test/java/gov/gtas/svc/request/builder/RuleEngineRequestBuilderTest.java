/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.svc.request.builder;

import gov.gtas.model.Bag;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.repository.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RuleEngineRequestBuilderTest {

	@Mock
	private PnrRepository pnrRepository;

	@Mock
	private ApisMessageRepository apisMessageRepository;

	@Mock
	private PassengerRepository passengerRepository;

	@Mock
	private SeatRepository seatRepository;

	@Mock
	private BagRepository bagRepository;

	@Mock
	private FlightPaxRepository flightPaxRepository;

	@Mock
	private DocumentRepository documentRepository;

	@InjectMocks
	private RuleEngineRequestBuilder ruleEngineRequestBuilder;

	@Test
	public void testEmptyBags() {
		Flight f = new Flight();
		f.setId(1L);
		Passenger p = new Passenger();
		p.setFlight(f);
		Bag b = ruleEngineRequestBuilder.makeEmptyBag(p, false);
		Assert.assertNotNull(b);
		Assert.assertNotNull(b.getFlight());
		Assert.assertEquals(1L, b.getFlight().getId().longValue());
		Assert.assertNotNull(b.getBagMeasurements());
		Assert.assertEquals(0, b.getBagMeasurements().getBagCount().intValue());
		Assert.assertEquals(0D, b.getBagMeasurements().getWeight(), 0D);
		Assert.assertEquals(p, b.getPassenger());
	}

}
