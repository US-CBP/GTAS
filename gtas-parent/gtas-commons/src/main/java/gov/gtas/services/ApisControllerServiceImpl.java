package gov.gtas.services;

import gov.gtas.enumtype.MessageType;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetailFromMessage;
import gov.gtas.model.PassengerDetails;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.search.FlightPassengerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
			PassengerDetails passengerDetails = filterOutMaskedAPISOrPnr(p);
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
			if (p.getDataRetentionStatus().isMaskedAPIS()) {
				fpVo.maskPII();
			}
			if (p.getDataRetentionStatus().isDeletedAPIS()) {
				fpVo.deletePII();
			}
		}
		return flightPassengerVos;
	}
	private PassengerDetails filterOutMaskedAPISOrPnr(Passenger t) {
		PassengerDetails passengerDetails = t.getPassengerDetails();
		if (t.getDataRetentionStatus().isMaskedAPIS() || t.getDataRetentionStatus().isMaskedPNR() || t.getDataRetentionStatus().isDeletedAPIS() || t.getDataRetentionStatus().isDeletedPNR()) {
			if (!t.getDataRetentionStatus().isMaskedPNR() && !t.getDataRetentionStatus().isDeletedPNR() && t.getDataRetentionStatus().isHasPnrMessage()) {
				passengerDetails = getPassengerDetails(t, MessageType.PNR);
			} else if (!t.getDataRetentionStatus().isMaskedAPIS() && !t.getDataRetentionStatus().isDeletedAPIS() && t.getDataRetentionStatus().isHasApisMessage()) {
				passengerDetails = getPassengerDetails(t, MessageType.APIS);
			} else if ((t.getDataRetentionStatus().isHasApisMessage() && !t.getDataRetentionStatus().isDeletedAPIS())
					|| (t.getDataRetentionStatus().isHasPnrMessage() && !t.getDataRetentionStatus().isDeletedPNR())){
				passengerDetails.maskPII();
			} else {
				passengerDetails.deletePII();
			}
		} return passengerDetails;
	}

	private PassengerDetails getPassengerDetails(Passenger t, MessageType messageType) {
		return t
				.getPassengerDetailFromMessages()
				.stream()
				.filter(fs -> fs.getMessageType() == messageType)
				.sorted(Comparator.comparing(PassengerDetailFromMessage::getCreatedAt).reversed())
				.map(PassengerDetails::from)
				.findFirst()
				.orElse(new PassengerDetails());
	}
}
