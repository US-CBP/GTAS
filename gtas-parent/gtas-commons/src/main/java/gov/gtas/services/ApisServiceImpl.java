package gov.gtas.services;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.search.FlightPassengerVo;
import gov.gtas.util.PaxDetailVoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApisServiceImpl implements ApisService {

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

	@Override
	public Set<ApisMessage> apisMessageWithFlightInfo(Set<Long>passengerIds, Set<Long> apisIds, Long flightId) {
		return apisMessageRepository.apisMessageWithFlightInfo(passengerIds, apisIds, flightId);
	}

	@Override
	public Map<Long, Set<Passenger>> getPassengersOnApis(Set<Long> pids, Set<Long> hitApisIds, Long flightId) {
		Map<Long, Set<Passenger>> objectMap = new HashMap<>();
		List<Object[]> oList = apisMessageRepository.apisAndObject(pids, hitApisIds, flightId);
		for (Object[] answerKey : oList) {
			Long pnrId = (Long) answerKey[0];
			Passenger object = (Passenger) answerKey[1];
			processObject(object, objectMap, pnrId);
		}
		return objectMap;
	}
	private static <T> void processObject(T type, Map<Long, Set<T>> map, Long messageId) {
		if (map.containsKey(messageId)) {
			map.get(messageId).add(type);
		} else {
			Set<T> objectHashSet = new HashSet<>(map.values().size() * 50);
			objectHashSet.add(type);
			map.put(messageId, objectHashSet);
		}
	}

}
