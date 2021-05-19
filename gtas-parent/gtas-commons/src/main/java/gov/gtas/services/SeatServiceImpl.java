package gov.gtas.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.Seat;
import gov.gtas.repository.SeatRepository;

@Service
public class SeatServiceImpl implements SeatService {

	private final SeatRepository seatRepository;

	@Autowired
	public SeatServiceImpl(SeatRepository seatRepository) {
		this.seatRepository = seatRepository;
	}

	public List<String> findSeatNumberByFlightIdAndPassengerId(Long flightId, Long paxId) {
		List<Seat> seatList = seatRepository.findByFlightIdAndPassengerId(flightId, paxId);
		List<String> seatNumber = null;
		if (CollectionUtils.isNotEmpty(seatList)) {
			//We check for APIS here as they are a priority return.
			//If we have APIS seats, we assume these are the most up to date seats.
			boolean hasApisSeat = false;
			boolean hasPnrSeat = false;
			Set<Seat> seatSet = new HashSet<>();
			for(Seat seat: seatList){
				if(seat.getApis()) { //If the seat is APIS add it to set
					hasApisSeat = true;
					if(hasPnrSeat){ //If the set has PNR seats in it, empty the set and add the APIS instead
						seatSet.clear(); //This will only logically be done once, if at all.
					}
					seatSet.add(seat);
				} else if(!hasApisSeat) { //Logically implies is not an Apis Seat.
					hasPnrSeat = true;
					seatSet.add(seat); //Only possible to add if not ApisSeat
				}
			}
			seatNumber = seatSet.stream().map(Seat::getNumber).distinct()
					.collect(Collectors.toList());
		}

		return seatNumber;
	}

}
