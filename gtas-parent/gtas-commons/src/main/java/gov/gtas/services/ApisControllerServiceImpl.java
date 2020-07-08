package gov.gtas.services;

import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.search.FlightPassengerVo;
import gov.gtas.util.PaxDetailVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ApisControllerServiceImpl implements ApisControllerService {

	@Autowired
	ApisMessageRepository apisMessageRepository;

	public List<FlightPassengerVo> generateFlightPassengerList(String ref, long flightId) {
		Set<Passenger> fpList = apisMessageRepository.findPassengerByApisRef(ref, flightId);
		List<FlightPassengerVo> flightPassengerVos = new ArrayList<>();
		for (Passenger p : fpList) {
			PassengerDetails passengerDetails = PaxDetailVoUtil.filterOutMaskedAPISOrPnr(p);
			FlightPassengerVo fpVo = new FlightPassengerVo();
			fpVo.setFirstName(passengerDetails.getFirstName());
			fpVo.setLastName(passengerDetails.getLastName());
			fpVo.setMiddleName(passengerDetails.getMiddleName());
			fpVo.setEmbarkation(p.getPassengerTripDetails().getEmbarkation());
			fpVo.setDebarkation(p.getPassengerTripDetails().getDebarkation());
			fpVo.setPortOfFirstArrival(p.getFlight().getDestination());
			fpVo.setResidencyCountry(passengerDetails.getNationality());
			fpVo.setPassengerType(passengerDetails.getPassengerType());
			fpVo.setNationality(passengerDetails.getNationality());
			fpVo.setResRefNumber(p.getPassengerTripDetails().getReservationReferenceNumber());
			fpVo.setFlightId(p.getFlight().getId());
			fpVo.setPassengerId(p.getId());
			flightPassengerVos.add(fpVo);
			if (p.getDataRetentionStatus().requiresMaskedAPIS()) {
				fpVo.maskPII();
			}
			if (p.getDataRetentionStatus().requiresDeletedPnrAndApisMessage()) {
				fpVo.deletePII();
			}
		}
		return flightPassengerVos;
	}

}
