package gov.gtas;

import gov.gtas.model.*;
import gov.gtas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class IntegrationTestBuilder {
    @Autowired
    ApisMessageRepository apisMessageRepository;

    @Autowired
    PnrRepository pnrRepository;

    @Autowired
    FlightPaxRepository flightPaxRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    MessageRepository messageRepository;

    public enum MessageTypeGenerated {
        BOTH,
        PNR,
        APIS
    }

    public void reset() {
        flight = null;
        passenger = null;
        flightPaxApis = null;
        flightPaxPnr = null;
        pnrMessage = null;
        apisMessage = null;
        passengerSetPnr = null;
        passengerSetApis = null;
    }

    private Flight flight;
    private Passenger passenger;
    private FlightPax flightPaxApis;
    private FlightPax flightPaxPnr;
    private Pnr pnrMessage;
    private ApisMessage apisMessage;
    private Set<Passenger> passengerSetPnr;
    private Set<Passenger> passengerSetApis;
    private MessageTypeGenerated messageTypeGenerated = MessageTypeGenerated.BOTH;
    private static final Integer BAG_COUNT_PNR = 1;
    private static final Integer BAG_COUNT_APIS = 3;
    private static final double BAG_WEIGHT_PNR = 15;
    private static final double BAG_WEIGHT_APIS = 187;
    private static final double BAG_AVERAGE_WEIGHT_PNR = 5;
    private static final double BAG_AVERAGE_WEIGHT_APIS = 123;
    private static final String FIRST_NAME = "john";
    private static final String LAST_NAME = "doe";


    public IntegrationTestData build() {
        generateDataIfNeeded();
        passenger = passengerRepository.save(passenger);
        Set<FlightPax> flightPaxSet;
        switch (messageTypeGenerated) {
            case BOTH:
            default:
                savePnrData();
                saveApisData();
                flightPaxSet = new HashSet<>();
                flightPaxSet.add(flightPaxApis);
                flightPaxSet.add(flightPaxPnr);
                passenger.setFlightPaxList(flightPaxSet);
                passengerRepository.save(passenger);
                break;
            case PNR:
                savePnrData();
                flightPaxSet = new HashSet<>();
                flightPaxSet.add(flightPaxPnr);
                passenger.setFlightPaxList(flightPaxSet);
                passengerRepository.save(passenger);
                break;
            case APIS:
                saveApisData();
                flightPaxSet = new HashSet<>();
                flightPaxSet.add(flightPaxApis);
                passenger.setFlightPaxList(flightPaxSet);
                passengerRepository.save(passenger);
                break;
        }
        return new IntegrationTestData(this);
    }

    Flight getFlight() {
        return flight;
    }

    Passenger getPassenger() {
        return passenger;
    }

    FlightPax getFlightPaxApis() {
        return flightPaxApis;
    }

    FlightPax getFlightPaxPnr() {
        return flightPaxPnr;
    }

    Pnr getPnr() {
        return pnrMessage;
    }

    ApisMessage getApisMessage() {
        return apisMessage;
    }

    IntegrationTestBuilder flight(Flight flight) {
        this.flight = flight;
        return this;
    }

    public IntegrationTestBuilder passenger(Passenger passenger) {
        this.passenger = passenger;
        return this;
    }

    public IntegrationTestBuilder flightPaxApis(FlightPax flightPaxApis) {
        this.flightPaxApis = flightPaxApis;
        return this;
    }

    public IntegrationTestBuilder flightPaxPnr(FlightPax flightPaxPnr) {
        this.flightPaxPnr = flightPaxPnr;
        return this;
    }

    public IntegrationTestBuilder pnrMessage(Pnr pnrMessage) {
        this.pnrMessage = pnrMessage;
        return this;
    }

    public IntegrationTestBuilder apisMessage(ApisMessage apisMessage) {
        this.apisMessage = apisMessage;
        return this;
    }

    public IntegrationTestBuilder testDataType(MessageTypeGenerated messageTypeGenerated) {
        this.messageTypeGenerated = messageTypeGenerated;
        return this;
    }

    public IntegrationTestBuilder passengersOnPnr(Set<Passenger> passengerSetPnr) {
        this.passengerSetPnr = passengerSetPnr;
        return this;
    }

    public IntegrationTestBuilder passengersOnApis(Set<Passenger> passengerSetApis) {
        this.passengerSetApis = passengerSetApis;
        return this;
    }
    public IntegrationTestBuilder messageType(MessageTypeGenerated messageTypeGenerated) {
        this.messageTypeGenerated = messageTypeGenerated;
        return this;
    }

    private void saveApisData() {
        apisMessage.setPassengers(passengerSetApis);
        apisMessage = apisMessageRepository.save(apisMessage);
        flightPaxApis.setPassenger(passenger);
        flightPaxApis.setFlight(flight);
        Set<ApisMessage> apisMessageSet = new HashSet<>();
        apisMessageSet.add(apisMessage);
        flightPaxApis.setApisMessage(apisMessageSet);
        flightPaxApis = flightPaxRepository.save(flightPaxApis);
    }

    private void savePnrData() {
        pnrMessage.setPassengers(passengerSetPnr);
        pnrMessage.setPassengerCount(passengerSetPnr.size());
        List<FlightLeg> flightLegs = new ArrayList<>();
        FlightLeg leg = new FlightLeg();
        leg.setFlight(flight);
        leg.setLegNumber(1);
        flightLegs.add(leg);
        pnrMessage.setFlightLegs(flightLegs);
        Set<Flight> flightSet = new HashSet<>();
        flightSet.add(flight);
        pnrMessage.setFlights(flightSet);
        pnrMessage = pnrRepository.save(pnrMessage);
        flight = flightRepository.save(flight);
        flightPaxPnr.setFlight(flight);
        flightPaxPnr.setPassenger(passenger);
        flightPaxRepository.save(flightPaxPnr);
        flightPaxPnr = flightPaxRepository.save(flightPaxPnr);
    }

    private void generateDataIfNeeded() {
        if (flight == null) {
            flight = defaultFlight();
        }

        if (passenger == null) {
            passenger = defaultPassenger();
        }

        switch (messageTypeGenerated) {
            case PNR:
                apisMessage = null;
                flightPaxApis = null;
                generatePnrDataIfNeeded();
                break;
            case APIS:
                pnrMessage = null;
                flightPaxPnr = null;
                generateApisDataIfNeeded();
                break;
            case BOTH:
            default:
                generatePnrDataIfNeeded();
                generateApisDataIfNeeded();
                break;
        }
    }

    private void generateApisDataIfNeeded() {
        if (flightPaxApis == null) {
            flightPaxApis = defaultPaxApis();
        }
        if (apisMessage == null) {
            apisMessage = defaultApisMessage();
        }
        Set<Flight> flightSet = new HashSet<>();
        flightSet.add(flight);
        Set<FlightPax> flightPaxSet = new HashSet<>();
        flightPaxSet.add(flightPaxApis);
        apisMessage.setFlights(flightSet);
        apisMessage.setFlightPaxList(flightPaxSet);
        if (passengerSetApis == null) {
            passengerSetApis = defaultPassengerSet();
        }
    }

    private FlightPax defaultPaxApis() {
        FlightPax flightPaxAPIS = new FlightPax();
        flightPaxAPIS.setReservationReferenceNumber("ReserverMe");
        flightPaxAPIS.setBagCount(BAG_COUNT_APIS);
        flightPaxAPIS.setPassenger(passenger);
        flightPaxAPIS.setFlight(flight);
        flightPaxAPIS.setBagWeight(BAG_WEIGHT_APIS);
        flightPaxAPIS.setAverageBagWeight(BAG_AVERAGE_WEIGHT_APIS);
        flightPaxAPIS.setMessageSource("APIS");
        return flightPaxAPIS;

    }

    private ApisMessage defaultApisMessage() {
        ApisMessage apisMessage = new ApisMessage();
        apisMessage.setCreateDate(new Date());
        apisMessage.setFilePath("Test");
        EdifactMessage edifactMessage = new EdifactMessage();
        edifactMessage.setTransmissionDate(new Date());
        apisMessage.setEdifactMessage(edifactMessage);
        return apisMessage;
    }

    private void generatePnrDataIfNeeded() {
        if (pnrMessage == null) {
            pnrMessage = defaultPnrMessage();
        }
        if (flightPaxPnr == null) {
            flightPaxPnr = defaultPaxPnr();
        }
        if (passengerSetPnr == null) {
            passengerSetPnr = defaultPassengerSet();
        }
    }

    private Set<Passenger> defaultPassengerSet() {
        Set<Passenger> passengerSet = new HashSet<>();
        passengerSet.add(passenger);
        return passengerSet;
    }

    private FlightPax defaultPaxPnr() {
        FlightPax flightPaxPNR = new FlightPax();
        flightPaxPNR.setBagCount(BAG_COUNT_PNR);
        flightPaxPNR.setPassenger(passenger);
        flightPaxPNR.setFlight(flight);
        flightPaxPNR.setBagWeight(BAG_WEIGHT_PNR);
        flightPaxPNR.setAverageBagWeight(BAG_AVERAGE_WEIGHT_PNR);
        flightPaxPNR.setMessageSource("PNR");
        return flightPaxPNR;
    }

    private Pnr defaultPnrMessage() {
        Pnr pnrMessage = new Pnr();
        pnrMessage.setCreateDate(new Date());
        pnrMessage.setFilePath("Test");
        pnrMessage.setBagCount(BAG_COUNT_PNR);
        return pnrMessage;

    }

    private Passenger defaultPassenger() {
        passenger = new Passenger();
        passenger.getPassengerDetails().setPassengerType("P");
        passenger.getPassengerDetails().setFirstName(FIRST_NAME);
        passenger.getPassengerDetails().setLastName(LAST_NAME);
        return passenger;
    }

    private Flight defaultFlight() {
        flight = new Flight();
        flight.setCarrier("DL");
        flight.setDirection("O");
        flight.setFlightDate(new Date());
        flight.setFlightNumber("0012");
        flight.setOrigin("LAX");
        flight.setDestination("IAD");
        return flight;
    }

}
