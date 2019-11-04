package gov.gtas.util;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.model.Flight;
import gov.gtas.model.MutableFlightDetails;
import gov.gtas.vo.passenger.FlightVo;


public class PaxDetailVoUtilTest {

	@Before
	public void before() {

	}

	@Test
	public void populateFlightVoWithFlightDetailTest() {
		Flight flight = new Flight();
		flight.setId(200L);
		flight.setFlightNumber("501");
		flight.setFullFlightNumber("ET501");
		flight.setOrigin("IAD");
		flight.setDestination("ADD");
		MutableFlightDetails mfd = new MutableFlightDetails();
		mfd.setEta(Calendar.getInstance().getTime());
		mfd.setEtaDate(Calendar.getInstance().getTime());
		mfd.setEtd(Calendar.getInstance().getTime());
		flight.setMutableFlightDetails(mfd);
		FlightVo flightVo = new FlightVo();

		PaxDetailVoUtil.populateFlightVoWithFlightDetail(flight, flightVo);
		Assert.assertEquals("501", flightVo.getFlightNumber());
		Assert.assertEquals("IAD", flight.getOrigin());
		Assert.assertEquals("ADD", flight.getDestination());
		Assert.assertEquals("ET501", flight.getFullFlightNumber());
	}

}
