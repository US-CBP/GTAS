package gov.gtas.services;

public interface SeatService {
	
	public String findSeatNumberByFlightIdAndPassengerId(Long flightId, Long paxId);

}
