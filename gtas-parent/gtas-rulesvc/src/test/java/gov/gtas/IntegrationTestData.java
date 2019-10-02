package gov.gtas;

import com.mchange.util.AssertException;
import gov.gtas.model.*;

public class IntegrationTestData {

	private Flight flight;
	private Passenger passenger;
	private FlightPax flightPaxApis;
	private FlightPax flightPaxPnr;
	private Pnr pnrMessage;
	private ApisMessage apisMessage;

	IntegrationTestData(IntegrationTestBuilder integrationTestBuilder) {
		this.flight = integrationTestBuilder.getFlight();
		this.passenger = integrationTestBuilder.getPassenger();
		this.flightPaxApis = integrationTestBuilder.getFlightPaxApis();
		this.flightPaxPnr = integrationTestBuilder.getFlightPaxPnr();
		this.pnrMessage = integrationTestBuilder.getPnr();
		this.apisMessage = integrationTestBuilder.getApisMessage();
	}

	private IntegrationTestData() {
		throw new AssertException("Use the builder to generate Integration Test Data");
	}

	public FlightPax getFlightPaxApis() {
		return flightPaxApis;
	}

	public Flight getFlight() {
		return flight;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public FlightPax getFlightPaxPnr() {
		return flightPaxPnr;
	}

	public Pnr getPnrMessage() {
		return pnrMessage;
	}

	public ApisMessage getApisMessage() {
		return apisMessage;
	}

}
