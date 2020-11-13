/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.svc.request.builder;

import gov.gtas.TestData;
import gov.gtas.model.*;
import gov.gtas.repository.*;
import gov.gtas.services.PnrService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;

@RunWith(MockitoJUnitRunner.class)
public class RuleEngineRequestBuilderTest {

	@Mock
	private PnrRepository pnrRepository;

	@Mock
	private PnrService pnrService;

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
	public void addApis() {
		ApisMessage apisMessage = TestData.getApisMessage();
		apisMessage.setId(1L);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyApisBagsOnNullBag", true);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyPnrBagsOnNullBag", true);
		Mockito.when(apisMessageRepository.getPassengerWithFlightInfo(any())).thenReturn(Collections.singleton(1L));
		ruleEngineRequestBuilder.addApisMessage(Collections.singletonList(apisMessage));
	}

	@Test
	public void addApisEarlyRelease() {
		ApisMessage apisMessage = TestData.getApisMessage();
		apisMessage.setId(1L);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyApisBagsOnNullBag", true);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyPnrBagsOnNullBag", true);
		ruleEngineRequestBuilder.addApisMessage(Collections.singletonList(apisMessage));
	}

	@Test
	public void addPnrEarlyRelease() throws SQLException {
		Pnr pnr = TestData.getPnrMessage();
		pnr.setId(1L);
		pnr.setPassengers(new HashSet<>());
		ruleEngineRequestBuilder.addPnr(Collections.singletonList(pnr));
	}
	@Test
	public void addApisWithBags() {
		ApisMessage apisMessage = TestData.getApisMessage();
		apisMessage.setId(1L);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyApisBagsOnNullBag", true);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyPnrBagsOnNullBag", true);
		Mockito.when(passengerRepository.getPassengersWithFlightDetails(anySet()))
				.thenReturn(Collections.singleton(TestData.getPassenger()));
		Mockito.when(apisMessageRepository.getPassengerWithFlightInfo(any())).thenReturn(Collections.singleton(1L));
		ruleEngineRequestBuilder.addApisMessage(Collections.singletonList(apisMessage));
	}

	@Test
	public void buildIt() {
		ruleEngineRequestBuilder.build();
	}

	@Test
	public void addPnrWithBags() throws SQLException {
		Pnr pnr = TestData.getPnrMessage();
		pnr.setId(1L);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyApisBagsOnNullBag", true);
		ReflectionTestUtils.setField(ruleEngineRequestBuilder, "makeEmptyPnrBagsOnNullBag", true);

		Object[] addrPnr = { 1L, TestData.getAddress() };
		Object[] ffPnr = { 1L, TestData.getFrequentFlyer() };
		Object[] creditCardPnr = { 1L, TestData.getCreditCard() };
		Object[] phonePnr = { 1L, TestData.getPhone() };
		Object[] addBookingDetail = { 1L, TestData.getBookingDetail() };
		Object[] agencyPnr = { 1L, TestData.getAgency() };
		Object[] dwellPnr = { 1L, TestData.getDwell() };
		Object[] getPassengerPnr = { 1L, TestData.getPassenger() };
		Object[] paymentPnr = { 1L, TestData.getPayment() };
		Object[] emailPnr = { 1L, TestData.getEmail() };

		List<Object[]> addressPnrList = new ArrayList<>();
		addressPnrList.add(addrPnr);

		List<Object[]> frequentFlyerPnrList = new ArrayList<>();
		frequentFlyerPnrList.add(ffPnr);

		List<Object[]> creditCardPnrList = new ArrayList<>();
		creditCardPnrList.add(creditCardPnr);

		List<Object[]> phonePnrList = new ArrayList<>();
		phonePnrList.add(phonePnr);

		List<Object[]> bookingDetailPnrList = new ArrayList<>();
		bookingDetailPnrList.add(addBookingDetail);

		List<Object[]> agencyPnrList = new ArrayList<>();
		agencyPnrList.add(agencyPnr);

		List<Object[]> dwellTimePnrList = new ArrayList<>();
		dwellTimePnrList.add(dwellPnr);

		List<Object[]> passengerPnrList = new ArrayList<>();
		passengerPnrList.add(getPassengerPnr);

		List<Object[]> paymentPnrList = new ArrayList<>();
		paymentPnrList.add(paymentPnr);

		List<Object[]> emailPnrList = new ArrayList<>();
		emailPnrList.add(emailPnr);

		Mockito.when(pnrRepository.getPassengersWithFlight(any()))
				.thenReturn(Collections.singleton(TestData.getPassenger()));

		ruleEngineRequestBuilder.addPnr(Collections.singletonList(pnr));
	}

	@Test
	public void testEmptyBags() {
		Flight f = new Flight();
		f.setId(1L);
		Passenger p = new Passenger();
		p.setFlight(f);
		Bag b = ruleEngineRequestBuilder.makeEmptyBag(p, "PNR", false);
		Assert.assertNotNull(b);
		Assert.assertNotNull(b.getFlight());
		Assert.assertEquals(1L, b.getFlight().getId().longValue());
		Assert.assertNotNull(b.getBagMeasurements());
		Assert.assertEquals(0, b.getBagMeasurements().getBagCount().intValue());
		Assert.assertEquals(0D, b.getBagMeasurements().getWeight(), 0D);
		Assert.assertEquals(p, b.getPassenger());
	}

}
