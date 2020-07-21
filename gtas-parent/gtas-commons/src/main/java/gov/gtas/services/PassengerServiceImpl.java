/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.aop.annotations.FlightAuditFirstArgFlightIdAsLong;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.enumtype.Status;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.model.*;
import gov.gtas.repository.*;
import gov.gtas.services.dto.PassengersPageDto;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.PassengerGridItemVo;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * The Class PassengerServiceImpl.
 */
@Service
public class PassengerServiceImpl implements PassengerService {

	private static final Logger logger = LoggerFactory.getLogger(PassengerServiceImpl.class);

	@Resource
	private PassengerRepository passengerRespository;

	@Resource
	private DocumentRepository documentRepository;

	@Autowired
	private AuditRecordRepository auditLogRepository;

	@Autowired
	private FlightRepository flightRespository;

	@Autowired
	private BookingDetailRepository bookingDetailRepository;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	PassengerRepository passengerRepository;

	@Autowired
	AppConfigurationService appConfigurationService;

	@Value("${tamr.enabled}")
	boolean tamrEnabled;
	@Value("${tamr.resolve_passenger_history}")
	boolean tamrResolvePassengerHistory;
	
	@Override
	@Transactional
	public Passenger create(Passenger passenger) {
		return passengerRespository.save(passenger);
	}

	@Override
	@Transactional
	@FlightAuditFirstArgFlightIdAsLong
	public PassengersPageDto getPassengersByCriteria(Long flightId, PassengersRequestDto request) {
		List<PassengerGridItemVo> rv = new ArrayList<>();
		Pair<Long, List<Passenger>> tuple = passengerRespository.findByCriteria(flightId, request);
		int count = 0;
		for (Passenger passenger : tuple.getRight()) {
			if (count == request.getPageSize()) {
				break;
			}
			PassengerGridItemVo vo = new PassengerGridItemVo();
			BeanUtils.copyProperties(passenger.getPassengerDetails(), vo);
			BeanUtils.copyProperties(passenger.getPassengerTripDetails(), vo);
			BeanUtils.copyProperties(passenger, vo);
			vo.setId(passenger.getId());

			for (Document d : passenger.getDocuments()) {
				DocumentVo docVo = DocumentVo.fromDocument(d);
				vo.addDocument(docVo);
			}

			for (HitDetail hd : passenger.getHitDetails()) {
				switch (hd.getHitEnum()) {
				case WATCHLIST_PASSENGER:
					vo.setOnWatchList(true);
					break;
				case WATCHLIST_DOCUMENT:
					vo.setOnWatchList(true);
					vo.setOnWatchListDoc(true);
					break;
				case PARTIAL_WATCHLIST:
					vo.setOnWatchListLink(true);
					break;
				case USER_DEFINED_RULE:
				case EXTERNAL_HIT:
				case GRAPH_HIT:
					vo.setOnRuleHitList(true);
					break;
				case MANUAL_HIT:
					break;
				}
			}

			// grab flight info
			Flight passengerFlight = passenger.getFlight();
			vo.setFlightId(passengerFlight.getId().toString());
			vo.setFlightNumber(passengerFlight.getFlightNumber());
			vo.setFullFlightNumber(passengerFlight.getFullFlightNumber());
			vo.setCarrier(passengerFlight.getCarrier());
			vo.setFlightOrigin(passengerFlight.getOrigin());
			vo.setFlightDestination(passengerFlight.getDestination());
			vo.setEtd(passengerFlight.getMutableFlightDetails().getEtd());
			vo.setEta(passengerFlight.getMutableFlightDetails().getEta());
			if (passenger.getDataRetentionStatus().requiresDeletedPnrAndApisMessage()) {
				vo.deletePII();
			} else if (passenger.getDataRetentionStatus().requiresMaskedPnrAndApisMessage()) {
				vo.maskPII();
			}
			rv.add(vo);

			count++;
		}
		return new PassengersPageDto(rv, tuple.getLeft());
	}

	@Override
	@Transactional
	public Passenger update(Passenger passenger) {
		Passenger passengerToUpdate = this.findById(passenger.getId());
		if (passengerToUpdate != null) {
			passengerToUpdate.getPassengerDetails().setAge(passenger.getPassengerDetails().getAge());
			passengerToUpdate.getPassengerDetails().setNationality(passenger.getPassengerDetails().getNationality());
			passengerToUpdate.getPassengerTripDetails()
					.setDebarkation(passenger.getPassengerTripDetails().getDebarkation());
			passengerToUpdate.getPassengerTripDetails()
					.setDebarkCountry(passenger.getPassengerTripDetails().getDebarkCountry());
			passengerToUpdate.getPassengerDetails().setDob(passenger.getPassengerDetails().getDob());
			passengerToUpdate.getPassengerTripDetails()
					.setEmbarkation(passenger.getPassengerTripDetails().getEmbarkation());
			passengerToUpdate.getPassengerTripDetails()
					.setEmbarkCountry(passenger.getPassengerTripDetails().getEmbarkCountry());
			passengerToUpdate.getPassengerDetails().setFirstName(passenger.getPassengerDetails().getFirstName());
			// passengerToUpdate.setFlights(passenger.getFlights()); TODO: UNCALLED METHOD,
			// CONSIDER REMOVAL
			passengerToUpdate.getPassengerDetails().setGender(passenger.getPassengerDetails().getGender());
			passengerToUpdate.getPassengerDetails().setLastName(passenger.getPassengerDetails().getLastName());
			passengerToUpdate.getPassengerDetails().setMiddleName(passenger.getPassengerDetails().getMiddleName());
			passengerToUpdate.getPassengerDetails()
					.setResidencyCountry(passenger.getPassengerDetails().getResidencyCountry());
			passengerToUpdate.setDocuments(passenger.getDocuments());
			passengerToUpdate.getPassengerDetails().setSuffix(passenger.getPassengerDetails().getSuffix());
			passengerToUpdate.getPassengerDetails().setTitle(passenger.getPassengerDetails().getTitle());
		}
		return passengerToUpdate;
	}

	/**
	 * Write audit log for disposition.
	 */
	private void writeAuditLogForDisposition(Long pId, User loggedinUser) {
		Passenger passenger = findById(pId);
		try {
			AuditActionTarget target = new AuditActionTarget(passenger);
			AuditActionData actionData = new AuditActionData();

			actionData.addProperty("Nationality", passenger.getPassengerDetails().getNationality());
			actionData.addProperty("PassengerType", passenger.getPassengerDetails().getPassengerType());
			//
			String message = "Disposition Status Change run on " + passenger.getCreatedAt();
			auditLogRepository.save(new GeneralAuditRecord(AuditActionType.DISPOSITION_STATUS_CHANGE, target.toString(),
					Status.SUCCESS, message, actionData.toString(), loggedinUser, new Date()));

		} catch (Exception ex) {
			logger.warn(ex.getMessage());
		}
	}

	@Override
	@Transactional
	public Passenger findById(Long id) {
		return passengerRespository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Passenger findByIdWithFlightAndDocumentsAndMessageDetails(Long paxId) {
		return passengerRepository.findByIdWithFlightAndDocumentsAndMessageDetails(paxId);
	}
	
	@Override
	@Transactional
	public Passenger findByIdWithFlightAndDocumentsAndHitDetails(Long paxId) {
		return passengerRepository.findByIdWithFlightAndDocumentsAndHitDetails(paxId);
	}

	@Override
	public List<Flight> getTravelHistoryNotByItinerary(Long paxId, Long pnrId, String pnrRef) {
		Optional<Passenger> p = passengerRepository.findById(paxId);
		return p.map(passenger -> Collections.singletonList(passenger.getFlight())).orElseGet(ArrayList::new);
	}

	@Override
	@Transactional
	public List<Passenger> getBookingDetailHistoryByPaxID(Long pId) {
	    List<Passenger> tamrIdMatches;
	    if (tamrEnabled && tamrResolvePassengerHistory) {
        	    tamrIdMatches = 
        	            bookingDetailRepository.getBookingDetailsByTamrId(pId);
	    } else {
	        tamrIdMatches = Collections.emptyList();
	    }

	    if (tamrIdMatches.size() > 0) {
	        return tamrIdMatches;
	    } else {
	        // If there are no tamrId matches, this means the tamrId must be
	        // NULL or Tamr history resolving is disabled. In that case, just
	        // do normal matching.
	        return bookingDetailRepository
	                .getBookingDetailsByPassengerIdTag(pId);
	    }
	}

	@Override
	public Set<Passenger> findPassengerFromPassengerIds(List<Long> passengerIdList) {
		return new HashSet<>(passengerRepository.getPassengersById(passengerIdList));
	}

	@Override
	public Set<Passenger> getPassengersForFuzzyMatching(List<MessageStatus> messageStatuses) {
		Set<Long> messageIds = messageStatuses.stream().map(MessageStatus::getMessageId).collect(toSet());
		Set<Long> flightIds = messageStatuses.stream().map(MessageStatus::getFlightId).collect(toSet());
		return passengerRepository.getPassengerMatchingInformation(messageIds, flightIds);
	}

	@Override
	public void setAllFlights(Set<Flight> flights, Long id) {
		String sqlStr = "";
		for (Flight f : flights) {
			sqlStr += "INSERT INTO flight_passenger(flight_id, passenger_id) VALUES(" + f.getId() + "," + id + ");";
		}
		em.createNativeQuery(sqlStr).executeUpdate();
	}

	@Override
	public Map<Long, Set<Document>> getDocumentMappedToPassengerIds(Set<Long> passengerIds) {
		Set<Document> docSet = documentRepository.getAllByPaxId(passengerIds);
		Map<Long, Set<Document>> mappedValues = new HashMap<>();
		for (Document document : docSet) {
			Long paxId = document.getPassengerId();
			if (mappedValues.containsKey(paxId)) {
				mappedValues.get(paxId).add(document);
			} else {
				Set<Document> documentSet = new HashSet<>();
				documentSet.add(document);
				mappedValues.put(paxId, documentSet);
			}
		}
		return mappedValues;
	}

	@Override
	public Set<Passenger> getPassengersWithHitDetails(Set<Long> passengerIds) {
		return passengerRepository.getPassengersWithHitDetails(passengerIds);
	}

	@Override
	public Set<Passenger> getPassengersForEmailMatching(Set<Passenger> passengers) {
		Set<Long> paxIds = passengers.stream().map(Passenger::getId).collect(toSet());
		return passengerRepository.getPassengersForEmailDto(paxIds);
	}

	@Override
	public Set<Passenger> getPassengersFromMessageIds(Set<Long> messageIds, Set<Long> flightIds) {
		if (messageIds.isEmpty()) {
			return new HashSet<>();
		} else {
			return passengerRepository.getPassengerIncludingHitsByMessageId(messageIds, flightIds);
		}
	}

	@Override
	public Set<Passenger> getFullPassengersFromMessageIds(Set<Long> messageIds, Set<Long> flightIds) {
		if (messageIds.isEmpty()) {
			return new HashSet<>();
		} else {
			return passengerRepository.getFullPassengerIncludingHitsByMessageId(messageIds, flightIds);
		}
	}

	@Override
	public 	Set<Document> getPassengerDocuments(Set<Long> passengerIds, Set<Long> flightIds) {
		if (passengerIds.isEmpty()) {
			return new HashSet<>();
		} else {
			return documentRepository.getDocumentsByPaxIdFlightId(passengerIds);
		}
	}


	@Override
	public 	Set<Passenger> getPassengersWithBags(Set<Long> passengerIds, Long flightId) {
		if (passengerIds.isEmpty()) {
			return new HashSet<>();
		} else {
			return passengerRepository.getDocumentsByPaxIdFlightId(passengerIds, flightId);
		}
	}
}
