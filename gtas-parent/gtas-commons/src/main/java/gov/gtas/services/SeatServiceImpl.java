package gov.gtas.services;

import java.util.List;
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
			List<String> seats = seatList.stream().map(seat -> seat.getNumber()).distinct()
					.collect(Collectors.toList());
			seatNumber = seats;
		}

		return seatNumber;
	}

}
