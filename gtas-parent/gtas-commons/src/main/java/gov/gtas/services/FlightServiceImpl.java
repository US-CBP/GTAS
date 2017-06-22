/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.services;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.Passenger;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.HitsSummaryRepository;
import gov.gtas.services.dto.FlightsPageDto;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.vo.passenger.FlightVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Class FlightServiceImpl.
 */
@Service
public class FlightServiceImpl implements FlightService {
	private static final Logger logger = LoggerFactory
			.getLogger(FlightServiceImpl.class);
	@Autowired
	private FlightRepository flightRespository;

	@Autowired
	private HitsSummaryRepository hitsSummaryRepository;

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional
	public Flight create(Flight flight) {
		return flightRespository.save(flight);
	}

    @Override
    @Transactional
    public FlightsPageDto findAll(FlightsRequestDto dto) {
        List<FlightVo> vos = new ArrayList<>();
        Pair<Long, List<Flight>> tuple = flightRespository.findByCriteria(dto);
        for (Flight f : tuple.getRight()) {
            Long fId = f.getId();
            int rCount = 0;
            int wCount = 0;
            List<HitsSummary> hList = hitsSummaryRepository.findHitsByFlightId(fId);
            for (HitsSummary hs : hList) {
                rCount += hs.getRuleHitCount();
                wCount += hs.getWatchListHitCount();
            }
            f.setListHitCount(wCount);
            f.setRuleHitCount(rCount);
        }
        flightRespository.flush();

        Pair<Long, List<Flight>> tuple2 = flightRespository.findByCriteria(dto);
        for (Flight f : tuple2.getRight()) {
            FlightVo vo = new FlightVo();
            BeanUtils.copyProperties(f, vo);
            vo.setListHitCount(f.getListHitCount());
            vo.setRuleHitCount(f.getRuleHitCount());
            vos.add(vo);
        }

        return new FlightsPageDto(vos, tuple.getLeft());
    }

	@Override
	@Transactional
	public HashMap<Document, List<Flight>> getFlightsByPassengerNameAndDocument(
			String firstName, String lastName, Set<Document> documents) {

		HashMap<Document, List<Flight>> _tempMap = new HashMap<Document, List<Flight>>();

		try {
			for (Document document : documents) {
				_tempMap.put(document, flightRespository
						.getFlightsByPassengerNameAndDocument(firstName,
								lastName, document.getDocumentNumber()));
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
			flightToUpdate
					.setDestinationCountry(flight.getDestinationCountry());
			flightToUpdate.setEta(flight.getEta());
			flightToUpdate.setEtd(flight.getEtd());
			flightToUpdate.setFlightDate(flight.getFlightDate());
			flightToUpdate.setFlightNumber(flight.getFlightNumber());
			flightToUpdate.setOrigin(flight.getOrigin());
			flightToUpdate.setOriginCountry(flight.getOriginCountry());
			flightToUpdate.setPassengers(flight.getPassengers());
			flightToUpdate.setUpdatedAt(new Date());
			// TODO replace with logged in user id
			flightToUpdate.setUpdatedBy(flight.getUpdatedBy());
			if (flight.getPassengers() != null
					&& flight.getPassengers().size() > 0) {
				Iterator it = flight.getPassengers().iterator();
				while (it.hasNext()) {
					Passenger p = (Passenger) it.next();
					flightToUpdate.addPassenger(p);
				}
			}
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
	public Flight getUniqueFlightByCriteria(String carrier,
			String flightNumber, String origin, String destination,
			Date flightDate) {
		return flightRespository.getFlightByCriteria(carrier,
				flightNumber, origin, destination, flightDate);
	}

	@Override
	@Transactional
	public List<Flight> getFlightByPaxId(Long paxId) {
		return flightRespository.getFlightByPaxId(paxId);
	}

	@Override
	@Transactional
	public List<Flight> getFlightsByDates(Date startDate, Date endDate) {
		return flightRespository.getFlightsByDates(startDate, endDate);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Flight> getFlightsThreeDaysForward() {
		String sqlStr = "SELECT * FROM flight WHERE eta BETWEEN NOW() AND NOW() + INTERVAL 3 DAY";
		return (List<Flight>) em.createNativeQuery(sqlStr, Flight.class)
				.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Flight> getFlightsThreeDaysForwardInbound() {
		String sqlStr = "SELECT * FROM flight WHERE eta BETWEEN NOW() AND NOW() + INTERVAL 3 DAY AND direction = 'I'";
		String sqlStrForCodeShare = "SELECT * FROM flight fl JOIN code_share_flight csfl WHERE fl.eta BETWEEN NOW() AND NOW() + INTERVAL 3 DAY AND fl.direction IN ('I') AND csfl.operating_flight_id = fl.id";
		String codeShareQueryFix = "SELECT * FROM flight WHERE eta BETWEEN NOW() AND NOW() + INTERVAL 3 DAY AND direction = 'I' AND ((marketing_flight = FALSE AND operating_flight = FALSE) OR operating_flight = TRUE)";
		return (List<Flight>) em.createNativeQuery(codeShareQueryFix, Flight.class)
				.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Flight> getFlightsThreeDaysForwardOutbound() {
		String sqlStr = "SELECT * FROM flight WHERE eta BETWEEN NOW() AND NOW() + INTERVAL 3 DAY  AND direction = 'O'";
		String sqlStrForCodeShare = "SELECT * FROM flight fl JOIN code_share_flight csfl WHERE fl.eta BETWEEN NOW() AND NOW() + INTERVAL 3 DAY AND fl.direction IN ('O') AND csfl.operating_flight_id = fl.id";
		String codeShareQueryFix = "SELECT * FROM flight WHERE eta BETWEEN NOW() AND NOW() + INTERVAL 3 DAY AND direction = 'O' AND ((marketing_flight = FALSE AND operating_flight = FALSE) OR operating_flight = TRUE)";
		return (List<Flight>) em.createNativeQuery(codeShareQueryFix, Flight.class)
				.getResultList();
	}
}
