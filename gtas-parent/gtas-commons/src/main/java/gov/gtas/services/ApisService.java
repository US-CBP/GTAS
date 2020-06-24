package gov.gtas.services;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Passenger;
import gov.gtas.services.search.FlightPassengerVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ApisService {

	List<FlightPassengerVo> generateFlightPassengerList(String ref, long flightId);
	Set<ApisMessage> apisMessageWithFlightInfo(Set<Long>passengerIds, Set<Long> apisIds, Long flightId);
	Map<Long, Set<Passenger>> getPassengersOnApis(Set<Long> pids, Set<Long> hitApisIds, Long flightId);
}
