/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.*;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.services.dto.SortOptionsDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PassengerRepositoryImpl implements PassengerRepositoryCustom {
	private static final Logger logger = LoggerFactory.getLogger(PassengerRepositoryImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Override
	public Pair<Long, List<Passenger>> findByCriteria(Long flightId, PassengersRequestDto dto) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Passenger> q = cb.createQuery(Passenger.class);
		Root<Passenger> pax = q.from(Passenger.class);

		// joins
		Join<Passenger, Flight> flight = pax.join("flight");
		Join<Passenger, HitsSummary> hits = pax.join("hits", JoinType.LEFT);
		Join<Flight, MutableFlightDetails> mutableFlightDetailsJoin = flight.join("mutableFlightDetails",
				JoinType.LEFT);
		Join<Passenger, PassengerDetails> paxDetailsJoin = pax.join("passengerDetails", JoinType.LEFT);

		List<Predicate> predicates = new ArrayList<>();
		if (StringUtils.isNotBlank(dto.getLastName())) {
			String likeString = String.format("%%%s%%", dto.getLastName().toUpperCase());
			predicates.add(cb.like(paxDetailsJoin.get("lastName"), likeString));
		}

		if (flightId == null) {
			predicates.addAll(createPredicates(cb, dto, flight));
		} else {
			hits.on(cb.equal(hits.get("flight").get("id"), cb.parameter(Long.class, "flightId")));
			predicates.add(cb.equal(flight.<Long>get("id"), flightId));
		}

		if (dto.getSort() != null) {
			List<Order> orderList = new ArrayList<>();
			for (SortOptionsDto sort : dto.getSort()) {
				List<Expression<?>> orderByItem = new ArrayList<>();
				String column = sort.getColumn();
				if (isFlightColumn(column)) {
					orderByItem.add(flight.get(column));
				} else if (column.equals("onRuleHitList")) {
					orderByItem.add(hits.get("ruleHitCount"));
					orderByItem.add(hits.get("graphHitCount"));
					orderByItem.add(hits.get("manualHitCount"));
				} else if (column.equals("onWatchList")) {
					orderByItem.add(hits.get("watchListHitCount"));
					orderByItem.add(hits.get("partialHitCount"));
				} else if ("eta".equalsIgnoreCase(column)) {
					orderByItem.add(mutableFlightDetailsJoin.get("eta"));
					// !!!!! THIS COVERS THE ELSE STATEMENT !!!!!
				} else if (!"documentNumber".equalsIgnoreCase(column)) {
					orderByItem.add(paxDetailsJoin.get(column));
				}
				if (sort.getDir().equals("desc")) {
					for (Expression<?> e : orderByItem) {
						if ("onWatchList".equalsIgnoreCase(column) || "onRuleHitList".equalsIgnoreCase(column)) {
							// The fuzzy matching can occure when the hits summary is null. Coalesce these
							// values to a 0
							// in order to have fuzzy matching show up in ordered form.
							orderList.add(cb.desc(cb.coalesce(e, 0)));
						} else {
							orderList.add(cb.desc(e));
						}
					}
				} else {
					for (Expression<?> e : orderByItem) {
						if ("onWatchList".equalsIgnoreCase(column) || "onRuleHitList".equalsIgnoreCase(column)) {
							orderList.add(cb.asc(cb.coalesce(e, 0)));
						} else {
							orderList.add(cb.asc(e));
						}
					}
				}
			}
			q.orderBy(orderList);
		}

		q.select(pax).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Passenger> typedQuery = addPagination(q, dto.getPageNumber(), dto.getPageSize());

		// total count: does not require joining on hitssummary
		CriteriaQuery<Long> cnt = cb.createQuery(Long.class);
		Root<Passenger> cntPax = cnt.from(Passenger.class);
		Join<Passenger, Flight> cntFlight = cntPax.join("flight");
		List<Predicate> cntPred = new ArrayList<>();
		if (flightId == null) {
			cntPred.addAll(createPredicates(cb, dto, cntFlight));
		} else {
			cntPred.add(cb.equal(cntFlight.<Long>get("id"), flightId));
		}
		cnt.select(cb.count(cntFlight)).where(cntPred.toArray(new Predicate[] {}));
		Long count = em.createQuery(cnt).getSingleResult();

		if (flightId != null) {
			typedQuery.setParameter("flightId", flightId);
		}

		logger.debug(typedQuery.unwrap(org.hibernate.Query.class).getQueryString());
		// System.out.println(typedQuery.unwrap(org.hibernate.Query.class).getQueryString());
		List<Passenger> results = typedQuery.getResultList();

		return new ImmutablePair<>(count, results);
	}

	private List<Predicate> createPredicates(CriteriaBuilder cb, PassengersRequestDto dto,
			Join<Passenger, Flight> flight) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		FlightRepositoryImpl.generateFilters(dto, cb, predicates, flight.get("origin"), flight.get("destination"));

		if (StringUtils.isNotBlank(dto.getFlightNumber())) {
			String likeString = String.format("%%%s%%", dto.getFlightNumber().toUpperCase());
			predicates.add(cb.like(flight.<String>get("fullFlightNumber"), likeString));
		}
		/*
		 * hack: javascript sends the empty string represented by the 'all' dropdown
		 * value as '0', so we check for that here to mean 'any direction'
		 */
		if (StringUtils.isNotBlank(dto.getDirection()) && !"A".equals(dto.getDirection())) {
			predicates.add(cb.equal(flight.<String>get("direction"), dto.getDirection()));
		}
		return predicates;
	}

	private <T> TypedQuery<T> addPagination(CriteriaQuery<T> q, int pageNumber, int pageSize) {
		int offset = (pageNumber - 1) * pageSize;
		TypedQuery<T> typedQuery = em.createQuery(q);
		typedQuery.setFirstResult(offset);

		/*
		 * complete hack: we're returning more results than the pagesize b/c the service
		 * will potentially throw some of them away. This is all b/c the left join on
		 * hitssummary will not work correctly if we have to check both flight id and
		 * passenger id.
		 */
		typedQuery.setMaxResults(pageSize * 3);
		return typedQuery;
	}

	private Set<String> flightColumns = new HashSet<>(Arrays.asList("fullFlightNumber", "etd"));

	private boolean isFlightColumn(String c) {
		return flightColumns.contains(c);
	}

}
