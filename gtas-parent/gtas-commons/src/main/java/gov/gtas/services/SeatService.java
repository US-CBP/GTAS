package gov.gtas.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.Seat;
import gov.gtas.repository.SeatRepository;

@Service
public class SeatService {
	
	@Autowired
	private SeatRepository seatRepository;
	
	@Transactional
	public String findSeatNumberByFlightIdAndPassengerId(Long flightId, Long paxId) {
		List<Seat> seatList = seatRepository.findByFlightIdAndPassengerId(flightId, paxId);
		String seatNumber = null;
		if (CollectionUtils.isNotEmpty(seatList)) {
			List<String> seats = seatList.stream().map(seat -> seat.getNumber()).distinct()
					.collect(Collectors.toList());
			if (seats.size() == 1) {
				 seatNumber = seats.get(0);
			}
		}
		
		return seatNumber;
	}

}
