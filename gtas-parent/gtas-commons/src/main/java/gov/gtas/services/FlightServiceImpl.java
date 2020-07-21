/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.SeatRepository;
import gov.gtas.services.dto.FlightsPageDto;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.vo.passenger.CountDownVo;
import gov.gtas.vo.passenger.SeatVo;
import gov.gtas.vo.passenger.CodeShareVo;
import gov.gtas.vo.passenger.FlightVo;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class FlightServiceImpl.
 */
@Service
public class FlightServiceImpl implements FlightService {

	private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);

	@Autowired
	private FlightRepository flightRespository;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SeatRepository seatRespository;

	@Autowired
	private AppConfigurationService appConfigurationService;

	@Autowired
	private AppConfigurationRepository appConfigurationRepository;

	@Override
	@Transactional
	public Flight create(Flight flight) {
		return flightRespository.save(flight);
	}

	@Override
	@Transactional
	public FlightsPageDto findAll(FlightsRequestDto dto) {
		Pair<Long, List<Flight>> tuple = flightRespository.findByCriteria(dto);
		List<Flight> flights = tuple.getRight();
		List<FlightVo> vos = convertFlightToFlightVo(flights);
		return new FlightsPageDto(vos, tuple.getLeft());
	}

	@Override
	public List<FlightVo> convertFlightToFlightVo(List<Flight> flights) {
		List<FlightVo> flightVos = new ArrayList<>();
		CountDownCalculator countDownCalculator = new CountDownCalculator(new Date());
		for (Flight f : flights) {
			FlightVo vo = new FlightVo();
			Date countDownToDate = f.getFlightCountDownView().getCountDownTimer();

			CountDownVo countDownVo;
			if (countDownToDate == null) {
				// This should not happen, but if it somehow does we need to return a vo instead
				// of throwing a null pointer.
				logger.error("count down is null, returning generic countdown vo!");
				countDownVo = new CountDownVo("Error, no date provided (it is null)!", false, Long.MIN_VALUE);
			} else {
				countDownVo = countDownCalculator.getCountDownFromDate(countDownToDate);
			}
			vo.setCountDown(countDownVo);
			vo.setDirection(f.getDirection());
			List<CodeShareVo> codeshareList = new ArrayList<>();
			BeanUtils.copyProperties(f, vo);
			BeanUtils.copyProperties(f.getMutableFlightDetails(), vo);

			Integer fuzzyHits = 0;

			if (f.getFlightHitsFuzzy() != null) {
				fuzzyHits = f.getFlightHitsFuzzy().getHitCount();
				vo.setFuzzyHitCount(fuzzyHits);
			}
			if (f.getFlightHitsGraph() != null) {
				vo.setGraphHitCount(f.getFlightHitsGraph().getHitCount());
			}
			if (f.getFlightHitsWatchlist() != null) {
				vo.setListHitCount(f.getFlightHitsWatchlist().getHitCount());// + fuzzyHits);
			}
			if (f.getFlightHitsRule() != null) {
				vo.setRuleHitCount(f.getFlightHitsRule().getHitCount());
			}
			if (f.getFlightHitsExternal() != null) {
				vo.setExternalHitCount(f.getFlightHitsExternal().getHitCount());
			}
			if (f.getFlightPassengerCount() != null) {
				vo.setPassengerCount(f.getFlightPassengerCount().getPassengerCount());
			}
			vo.setPaxWatchlistLinkHits(fuzzyHits.longValue());
			List<CodeShareFlight> csl = flightRespository.getCodeSharesForFlight(f.getId()); // get codeshare list
			for (CodeShareFlight cs : csl) { // grab all codeshares from it
				CodeShareVo codeshare = new CodeShareVo(); // Convert to Vo for transfer
				BeanUtils.copyProperties(cs, codeshare);
				codeshareList.add(codeshare); // Add csVo to list
			}
			vo.setCodeshares(codeshareList); // add csVOlist to flightvo
			flightVos.add(vo);
		}
		return flightVos;
	}

	@Override
	@Transactional
	public HashMap<Document, List<Flight>> getFlightsByPassengerNameAndDocument(String firstName, String lastName,
			Set<Document> documents) {

		HashMap<Document, List<Flight>> _tempMap = new HashMap<Document, List<Flight>>();

		try {
			for (Document document : documents) {
				_tempMap.put(document, flightRespository.getFlightsByPassengerNameAndDocument(firstName, lastName,
						document.getDocumentNumber()));
			}
		} catch (Exception ex) {
			logger.warn("The getFlightsByPassengerNameAndDocument error stack trace -  " + ex.getStackTrace());
		}
		return _tempMap;
	}

	@Override
	@Transactional
	public Flight update(Flight flight) {
		Flight flightToUpdate = this.findById(flight.getId());
		if (flightToUpdate != null) {
			flightToUpdate.setCarrier(flight.getCarrier());
			flightToUpdate.setChangeDate();
			flightToUpdate.setDestination(flight.getDestination());
			flightToUpdate.setDestinationCountry(flight.getDestinationCountry());
			flightToUpdate.setFlightNumber(flight.getFlightNumber());
			flightToUpdate.setOrigin(flight.getOrigin());
			flightToUpdate.setOriginCountry(flight.getOriginCountry());
			// flightToUpdate.setPassengers(flight.getPassengers());
			flightToUpdate.setUpdatedAt(new Date());
			// TODO replace with logged in user id
			flightToUpdate.setUpdatedBy(flight.getUpdatedBy());
			/*
			 * if (flight.getPassengers() != null && flight.getPassengers().size() > 0) {
			 * Iterator it = flight.getPassengers().iterator(); while (it.hasNext()) {
			 * Passenger p = (Passenger) it.next(); // flightToUpdate.addPassenger(p); } }
			 */
		}
		return flightToUpdate;
	}

	@Override
	@Transactional
	public Flight findById(Long id) {
		Flight flight = flightRespository.findOne(id);
		return flight;
	}

	@Override
	public Flight getUniqueFlightByCriteria(String carrier, String flightNumber, String origin, String destination,
			Date flightDate) {
		return flightRespository.getFlightByCriteria(carrier, flightNumber, origin, destination, flightDate);
	}

	@Override
	@Transactional
	public List<Flight> getFlightByPaxId(Long paxId) {
		return flightRespository.getFlightByPaxId(paxId);
	}

	@Override
	@Transactional
	public Set<Flight> getFlightByPaxIds(Set<Long> passengerIds) {
		return flightRespository.getFlightByPassengerIds(passengerIds);
	}


	@Override
	@Transactional
	public List<Flight> getFlightsByDates(Date startDate, Date endDate) {
		return flightRespository.getFlightsByDates(startDate, endDate);
	}

	@Override
	@Transactional
	public List<Flight> getFlightsThreeDaysForwardInbound() {
		Date now = new Date();
		Date threeDays = getThreeDaysForward();
		return flightRespository.getFlightsInboundByDateRange(now, threeDays);
	}

	@Override
	@Transactional
	public List<Flight> getFlightsThreeDaysForwardOutbound() {
		Date now = new Date();
		Date threeDays = getThreeDaysForward();
		return flightRespository.getFlightsOutBoundByDateRange(now, threeDays);
	}

	private Date getThreeDaysForward() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 3);
		return cal.getTime();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Passenger> getAllPassengers(Long id) {
		String sqlStr = "SELECT p.* FROM flight_passenger fp JOIN passenger p ON (fp.passenger_id = p.id) WHERE fp.flight_id="
				+ id;
		List<Passenger> resultList = em.createNativeQuery(sqlStr, Passenger.class).getResultList();
		Set<Passenger> resultSet = null;
		if (resultList != null && resultList.size() > 0) {
			resultSet = new HashSet<Passenger>(resultList);
		}
		return resultSet;
	}

	@Override
	public void setAllPassengers(Set<Passenger> passengers, Long flightId) {
		String sqlStr = "";
		for (Passenger p : passengers) {
			sqlStr += "INSERT INTO flight_passenger(flight_id, passenger_id) VALUES(" + flightId + "," + p.getId()
					+ ")";
		}
		em.createNativeQuery(sqlStr).executeUpdate();
	}

	@Override
	@Transactional
	@Modifying
	public void setSinglePassenger(Long passengerId, Long flightId) {
		String sqlStr = "INSERT INTO flight_passenger(flight_id,passenger_id) VALUES(" + flightId + "," + passengerId
				+ ")";
		em.createNativeQuery(sqlStr).executeUpdate();
	}

	@Override
	public int getPassengerCount(Flight f) {
		String sqlStr = "SELECT COUNT(*) FROM flight_passenger fp JOIN passenger p ON (fp.passenger_id = p.id) where flight_id = "
				+ f.getId();
		List<Integer> rList = em.createNativeQuery(sqlStr).getResultList();
		int tempInt = rList.get(0).intValue();
		return tempInt;
	}

	@Override
	public List<SeatVo> getSeatsByFlightId(Long flightId) {

		Flight flight = flightRespository.getFlightPassengerAndSeatById(flightId);

		List<SeatVo> seatVos = new ArrayList<>();
		for (Passenger passenger : flight.getPassengers()) {
			for (Seat seat : passenger.getSeatAssignments()) {
				SeatVo vo = new SeatVo();
				vo.setNumber(seat.getNumber());
				vo.setFlightId(flight.getId());
				vo.setPaxId(passenger.getId());
				vo.setFirstName(passenger.getPassengerDetails().getFirstName());
				vo.setLastName(passenger.getPassengerDetails().getLastName());
				vo.setMiddleInitial(passenger.getPassengerDetails().getMiddleName());
				vo.setFlightNumber(flight.getFlightNumber());
				vo.setRefNumber(passenger.getPassengerTripDetails().getReservationReferenceNumber());
				vo.setHasHits(passenger.getHits() != null && passenger.getHits().hasHits());

				if (passenger.getDataRetentionStatus().requiresDeletedAPIS() && seat.getApis()) {
					vo.deletePII();
				} else if (passenger.getDataRetentionStatus().requiresMaskedAPIS() && seat.getApis()) {
					vo.maskPII();
				} else if (passenger.getDataRetentionStatus().requiresDeletedPNR() && !seat.getApis()) {
					vo.deletePII();
				} else if (passenger.getDataRetentionStatus().requiresMaskedPNR() && !seat.getApis()) {
					vo.maskPII();
				}
				seatVos.add(vo);
			}
		}

		seatVos.forEach(parentSeatVo -> {
			if (parentSeatVo.getRefNumber() == null) {
				parentSeatVo.setCoTravellers(new String[0]);
			} else {
				parentSeatVo.setCoTravellers(seatVos.stream().filter(isCoTravelerSeat(parentSeatVo))
						.collect(Collectors.toList()).stream().map(SeatVo::getNumber).toArray(String[]::new));
			}
		});


		return seatVos;
	}

	private Predicate<SeatVo> isCoTravelerSeat(SeatVo parentSeatVo) {
		return childSeatVo -> childSeatVo.getRefNumber() != null
				& !parentSeatVo.getNumber().equals(childSeatVo.getNumber())
				& parentSeatVo.getRefNumber().equals(childSeatVo.getRefNumber());
	}
}
