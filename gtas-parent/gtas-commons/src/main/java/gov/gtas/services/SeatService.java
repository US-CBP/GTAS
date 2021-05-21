package gov.gtas.services;

import java.util.List;

public interface SeatService {

	public List<String> findSeatNumberByFlightIdAndPassengerId(Long flightId, Long paxId);

}
