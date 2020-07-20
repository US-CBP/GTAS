package gov.gtas.services;

import gov.gtas.enumtype.TripTypeEnum;
import gov.gtas.model.*;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface GtasLoader {
	void checkHashCode(String hash) throws LoaderException;

	void processReportingParties(ApisMessage apisMessage, List<ReportingPartyVo> parties);

	void processPnr(Pnr pnr, PnrVo vo);

	Flight processFlightsAndBookingDetails(List<FlightVo> flights, Set<Flight> messageFlights,
			List<FlightLeg> flightLegs, String[] primeFlightKey, Set<BookingDetail> bookingDetails)
			throws ParseException, NoPrimeFlightException;

	PassengerInformationDTO makeNewPassengerObjects(Flight primeFlight, List<PassengerVo> passengers,
			Set<Passenger> messagePassengers, Set<BookingDetail> bookingDetails, Message message) throws ParseException;

	Map<UUID, BagMeasurements> saveBagMeasurements(Set<BagMeasurementsVo> bagMeasurementsToSave);

	void createFormPfPayments(PnrVo vo, Pnr pnr);

	void updatePassenger(Passenger existingPassenger, PassengerVo pvo, Message message) throws ParseException;

	int createPassengers(Set<Passenger> newPassengers, Set<Passenger> messagePassengers,
			Flight primeFlight, Set<BookingDetail> bookingDetails);

	void updateFlightPassengerCount(Flight primeFlight, int createdPassengers);

	void prepareAdditionalProcessing(MessageInformation messageInformation, Pnr pnr, String[] pflightKey, String rawMessage);

	TripTypeEnum calculateTripType(List<FlightLeg> flightLegList, Set<DwellTime> dwellTimeSet);

	void calculateDwellTimes(Pnr pnr);

	boolean createMessage(ApisMessage m);

	boolean createMessage(Pnr m);

	void prepareAdditionalProcessing(MessageInformation messageInformation, ApisMessage apis, String[] pflightKey, String rawMessage);
}
