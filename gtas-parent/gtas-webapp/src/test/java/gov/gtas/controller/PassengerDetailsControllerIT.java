/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.controller;

import gov.gtas.IntegrationTestBuilder;
import gov.gtas.IntegrationTestBuilder.MessageTypeGenerated;
import gov.gtas.IntegrationTestData;
import gov.gtas.common.WebAppConfig;
import gov.gtas.controller.config.TestMvcRestServiceWebConfig;
import gov.gtas.model.*;
import gov.gtas.vo.passenger.ApisMessageVo;
import gov.gtas.vo.passenger.BagVo;
import gov.gtas.vo.passenger.PassengerVo;
import gov.gtas.vo.passenger.PnrVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMvcRestServiceWebConfig.class, WebAppConfig.class })
@WebAppConfiguration
@Rollback(true)
public class PassengerDetailsControllerIT {

	@Autowired
	PassengerDetailsController passengerDetailsController;

	@Autowired
	IntegrationTestBuilder integrationTestBuilder;

	private static Integer BAG_COUNT_PNR;
	private static double BAG_WEIGHT_PNR;
	private static double BAG_AVERAGE_WEIGHT_PNR;
	private static String FIRST_NAME;
	private static String LAST_NAME;
	private Flight flight;
	private Passenger passenger;
	private MockMvc mockMvc;

	@Before
	@Transactional
	public void setUp() {
		IntegrationTestData integrationTestData = integrationTestBuilder.testDataType(MessageTypeGenerated.BOTH)
				.build();

		flight = integrationTestData.getFlight();
		passenger = integrationTestData.getPassenger();

		BAG_COUNT_PNR = integrationTestData.getFlightPaxPnr().getBagCount();
		BAG_WEIGHT_PNR = integrationTestData.getFlightPaxPnr().getBagWeight();
		BAG_AVERAGE_WEIGHT_PNR = integrationTestData.getFlightPaxPnr().getAverageBagWeight();
		FIRST_NAME = integrationTestData.getPassenger().getPassengerDetails().getFirstName();
		LAST_NAME = integrationTestData.getPassenger().getPassengerDetails().getLastName();

		mockMvc = MockMvcBuilders.standaloneSetup(passengerDetailsController)
				.defaultRequest(get("/").contextPath("/gtas").accept(MediaType.APPLICATION_JSON)).build();
	}

	@After
	@Transactional
	public void tearDown() {
		integrationTestBuilder.reset();
	}

	@Test
	@Transactional
	@WithUserDetails("admin")
	@Ignore
	// TODO: This failing unit test is related to a current issue. Related to 1351.
	public void getPassengerByPassengerAndFlightIdUrlTest() throws Exception {

		String url = "/gtas/passengers/passenger/" + passenger.getId() + "/details?flightId=" + flight.getId();
		mockMvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"));
	}

	@Test
	@Transactional
	@WithUserDetails("admin")
	@Ignore
	public void callPassengerWithoutFlightId() throws Exception {

		String url = "/gtas/passengers/passenger/" + passenger.getId() + "/details";
		mockMvc.perform(get(url)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"));
	}

	@Test
	@Transactional
	@WithUserDetails("admin")
	@Ignore
	// TODO: This failing unit test is related to a current issue. Related to 1351.
	public void passengerVoTest() {
		flight.setMutableFlightDetails(null);
		PassengerVo passengerVo = passengerDetailsController
				.getPassengerByPaxIdAndFlightId(passenger.getId().toString(), flight.getId().toString());
		ApisMessageVo apisMessageVo = passengerVo.getApisMessageVo();
		assertNotNull(apisMessageVo);
		PnrVo pnrVo = passengerVo.getPnrVo();
		assertEquals(pnrVo.getBags().size(), 1);
		BagVo bagVo = pnrVo.getBags().get(0);
		assertEquals(new Integer(bagVo.getBag_count()), BAG_COUNT_PNR);
		assertEquals(new Double(bagVo.getBag_weight()), new Double(BAG_WEIGHT_PNR));
		assertEquals(new Double(bagVo.getAverage_bag_weight()), new Double(BAG_AVERAGE_WEIGHT_PNR));
	}
}
