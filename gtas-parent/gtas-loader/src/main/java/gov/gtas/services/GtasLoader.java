package gov.gtas.services;

import gov.gtas.PaxProcessingDto;
import gov.gtas.model.*;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.*;

import java.util.List;
import java.util.Set;

public interface GtasLoader {
    void checkHashCode(String hash) throws LoaderException;
    public void processReportingParties(ApisMessage apisMessage, List<ReportingPartyVo> parties);
    public void processPnr(Pnr pnr, PnrVo vo) throws ParseException;
    public Flight processFlightsAndPassengers(List<FlightVo> flights,
                                              Set<Flight> messageFlights,
                                              List<FlightLeg> flightLegs,
                                                        String[] primeFlightKey,
                                              Set<BookingDetail> bookingDetails) throws ParseException;
    public void makeNewPassengers(Flight primeFlight,
                                  List<PassengerVo> passengers,
                                  Set<Passenger> messagePassengers,
                                  Set<BookingDetail> bookingDetails,
                                  Message message) throws ParseException;
    public void saveAndLinkNewPassengers(PaxProcessingDto paxProcessingDto,List<BookingDetail> bookingDetails, Message message);
    public void createSeatAssignment(List<SeatVo> seatAssignments, Passenger p, Flight f);
    public void createBags(List<String> bagIds, Passenger p, Flight f);
    public void createBagsFromPnrVo(PnrVo pvo,Pnr pnr);
    public void createFormPfPayments(PnrVo vo,Pnr pnr);
    public void updatePassenger(Passenger existingPassenger, PassengerVo pvo) throws ParseException;
    public Passenger findPassengerOnFlight(Flight f, PassengerVo pvo);
    }
