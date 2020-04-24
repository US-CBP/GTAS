package gov.gtas.services;

import gov.gtas.model.Passenger;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.search.FlightPassengerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ApisControllerServiceImpl implements ApisControllerService {

	@Autowired
	ApisMessageRepository apisMessageRepository;

	@Transactional
	public List<FlightPassengerVo> generateFlightPassengerList(String ref, long flightId) {
		Set<Passenger> fpList = apisMessageRepository.findPassengerByApisRef(ref, flightId);
		List<FlightPassengerVo> flightPassengerVos = new ArrayList<>();
		for (Passenger p : fpList) {
			FlightPassengerVo fpVo = new FlightPassengerVo();
			fpVo.setFirstName(p.getPassengerDetails().getFirstName());
			fpVo.setLastName(p.getPassengerDetails().getLastName());
			fpVo.setMiddleName(p.getPassengerDetails().getMiddleName());
			fpVo.setEmbarkation(p.getPassengerTripDetails().getEmbarkation());
			fpVo.setDebarkation(p.getPassengerTripDetails().getDebarkation());
			fpVo.setPortOfFirstArrival(p.getFlight().getDestination());
			fpVo.setResidencyCountry(p.getPassengerDetails().getNationality());
			fpVo.setPassengerType(p.getPassengerDetails().getPassengerType());
			fpVo.setNationality(p.getPassengerDetails().getNationality());
			fpVo.setResRefNumber(p.getPassengerTripDetails().getReservationReferenceNumber());
			fpVo.setFlightId(p.getFlight().getId());
			fpVo.setPassengerId(p.getId());
			flightPassengerVos.add(fpVo);
		}
		return flightPassengerVos;
	}
}
