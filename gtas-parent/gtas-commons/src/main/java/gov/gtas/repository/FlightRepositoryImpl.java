/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import static gov.gtas.constant.FlightCategoryConstants.ALL_FLIGHTS;
import static gov.gtas.constant.FlightCategoryConstants.DOMESTIC_FLIGHTS;
import static gov.gtas.constant.FlightCategoryConstants.INTERNATIONAL_FLIGHTS;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

import gov.gtas.model.*;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.services.dto.SortOptionsDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * The Class FlightRepositoryImpl.
 */
@Repository
public class FlightRepositoryImpl implements FlightRepositoryCustom {
	private static final Logger logger = LoggerFactory.getLogger(FlightRepositoryImpl.class);

	@PersistenceContext
	private EntityManager em;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.repository.FlightRepositoryCustom#findByCriteria(gov.gtas.services
	 * .dto.FlightsRequestDto)
	 */
	@Override
	@Transactional
	public Pair<Long, List<Flight>> findByCriteria(FlightsRequestDto dto) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Flight> q = cb.createQuery(Flight.class);
		Root<Flight> root = q.from(Flight.class);
		List<Predicate> predicates = new ArrayList<>();

		Join<Flight, FlightHitsRule> ruleHits = root.join("flightHitsRule", JoinType.LEFT);
		Join<Flight, FlightHitsWatchlist> watchlistHits = root.join("flightHitsWatchlist", JoinType.LEFT);
		Join<Flight, FlightHitsWatchlist> fuzzyHits = root.join("flightHitsFuzzy", JoinType.LEFT);
		Join<Flight, FlightHitsWatchlist> graphHits = root.join("flightHitsGraph", JoinType.LEFT);
		Join<Flight, FlightHitsExternal> externalHits = root.join("flightHitsExternal", JoinType.LEFT);
		Join<Flight, FlightPassengerCount> passengerCountJoin = root.join("flightPassengerCount", JoinType.LEFT);
		Join<Flight, MutableFlightDetails> mutableFlightDetailsJoin = root.join("mutableFlightDetails", JoinType.LEFT);
		Join<Flight, FlightCountDownView> countDownViewJoin = root.join("flightCountDownView", JoinType.LEFT);
		Predicate etaCondition = getETAPredicate(dto, cb, root, mutableFlightDetailsJoin);

		// sorting
		if (dto.getSort() != null) {
			List<Order> orderList = new ArrayList<>();
			for (SortOptionsDto sort : dto.getSort()) {
				List<Expression<?>> orderByItem = new ArrayList<>();
				if (sort.getColumn().equalsIgnoreCase("ruleHitCount")) {
					orderByItem.add(ruleHits.get("hitCount"));
				} else if (sort.getColumn().equalsIgnoreCase("listHitCount")) {
					orderByItem.add(watchlistHits.get("hitCount"));
				} else if ("graphHitCount".equalsIgnoreCase(sort.getColumn())) {
					orderByItem.add(graphHits.get("hitCount"));
				} else if ("fuzzyHitCount".equalsIgnoreCase(sort.getColumn())) {
					orderByItem.add(fuzzyHits.get("hitCount"));
				} else if ("externalHitCount".equalsIgnoreCase(sort.getColumn())) {
					orderByItem.add(externalHits.get("hitCount"));
			    } else if (sort.getColumn().equalsIgnoreCase("passengerCount")) {
					orderByItem.add(passengerCountJoin.get("passengerCount"));
				} else if (sort.getColumn().equalsIgnoreCase("eta") || sort.getColumn().equalsIgnoreCase("etd")) {
					orderByItem.add(mutableFlightDetailsJoin.get(sort.getColumn()));
				} else if (sort.getColumn().equalsIgnoreCase("countDownTimer")) {
					orderByItem.add(countDownViewJoin.get(sort.getColumn()));
				} else {
					orderByItem.add(root.get(sort.getColumn()));
				}
				if ("desc".equalsIgnoreCase(sort.getDir())) {
					for (Expression<?> e : orderByItem) {
						if ("fuzzyHitCount".equalsIgnoreCase(sort.getColumn())
								|| "graphHitCount".equalsIgnoreCase(sort.getColumn())
								|| "ruleHitCount".equalsIgnoreCase(sort.getColumn())
								|| "listHitCount".equalsIgnoreCase(sort.getColumn())) {
							orderList.add(cb.desc(cb.coalesce(e, 0)));
						} else {
							orderList.add(cb.desc(e));
						}
					}
				} else {
					for (Expression<?> e : orderByItem) {
						if ("fuzzyHitCount".equalsIgnoreCase(sort.getColumn())
								|| "graphHitCount".equalsIgnoreCase(sort.getColumn())
								|| "ruleHitCount".equalsIgnoreCase(sort.getColumn())
								|| "listHitCount".equalsIgnoreCase(sort.getColumn())) {
							orderList.add(cb.asc(cb.coalesce(e, 0)));
						} else {
							orderList.add(cb.asc(e));
						}
					}
				}
			}
			q.orderBy(orderList);
		}

		// filters
		generateFilters(dto, cb, predicates, root.get("origin"), root.get("destination"));

		if (StringUtils.isNotBlank(dto.getFlightNumber())) {
			String likeString = String.format("%%%s%%", dto.getFlightNumber());
			predicates.add(cb.like(root.<String>get("fullFlightNumber"), likeString));
		}
		/*
		 * hack: javascript sends the empty string represented by the 'all' dropdown
		 * value as 'A', so we check for that here to mean 'any direction'
		 */
		if (StringUtils.isNotBlank(dto.getDirection()) && !"A".equals(dto.getDirection())) {
			predicates.add(cb.equal(root.<String>get("direction"), dto.getDirection()));
		}

		// filter flights - international, domestic, all
		Expression<String> origCountryExp = root.<String>get("originCountry");
		Expression<String> desCountryExp = root.<String>get("destinationCountry");
		Predicate origNotEPredicate = cb.notEqual(origCountryExp, "USA");
		Predicate destNotEPredicate = cb.notEqual(desCountryExp, "USA");
		Predicate origEPredicate = cb.equal(origCountryExp, "USA");
		Predicate destEPredicate = cb.equal(desCountryExp, "USA");
		if (dto.getFlightCategory() != null) {
			if (dto.getFlightCategory().equalsIgnoreCase(INTERNATIONAL_FLIGHTS)) {
				logger.info("User selects International Flights.");
				Predicate interPredicatePart1 = cb.and(origNotEPredicate, destEPredicate);
				Predicate interPredicatePart2 = cb.and(origEPredicate, destNotEPredicate);
				Predicate interPredicate = cb.or(interPredicatePart1, interPredicatePart2);
				predicates.add(interPredicate);
			} else if (dto.getFlightCategory().equalsIgnoreCase(DOMESTIC_FLIGHTS)) {
				logger.info("User selects Domestic Flights.");
				Predicate domesticPredicate = cb.and(origEPredicate, destEPredicate);
				predicates.add(domesticPredicate);

			} else if (dto.getFlightCategory().equalsIgnoreCase(ALL_FLIGHTS)) {
				logger.info("User selects All Flights.");
			}
		}

		List<Predicate> countPredicates = new ArrayList<>(predicates);
		if (etaCondition != null) {
			predicates.add(etaCondition);
		}
		q.select(root).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Flight> typedQuery = em.createQuery(q);

		// pagination
		int pageNumber = dto.getPageNumber();
		int pageSize = dto.getPageSize();
		int firstResultIndex = (pageNumber - 1) * pageSize;
		typedQuery.setFirstResult(firstResultIndex);
		typedQuery.setMaxResults(dto.getPageSize());

		// Runs exact query above without limits to get a count.
		long count = getCountOfQuery(dto, cb, countPredicates);

		logger.debug(typedQuery.unwrap(org.hibernate.Query.class).getQueryString());
		List<Flight> results = typedQuery.getResultList();

		return new ImmutablePair<>(count, results);
	}

	static void generateFilters(FlightsRequestDto dto, CriteriaBuilder cb, List<Predicate> predicates,
			Path<String> origin, Path<String> destination) {
		if (!CollectionUtils.isEmpty(dto.getOriginAirports())) {
			Predicate originPredicate = origin.in(dto.getOriginAirports());
			Predicate originAirportsPredicate = cb.and(originPredicate);
			predicates.add(originAirportsPredicate);
		}

		if (!CollectionUtils.isEmpty(dto.getDestinationAirports())) {
			Predicate destPredicate = destination.in(dto.getDestinationAirports());
			Predicate destAirportsPredicate = cb.and(destPredicate);
			predicates.add(destAirportsPredicate);
		}
	}

	private long getCountOfQuery(FlightsRequestDto dto, CriteriaBuilder cb, List<Predicate> countPredicates) {
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Flight> countRoot = countQuery.from(Flight.class);
		Join<Flight, MutableFlightDetails> countMutableFlightsInfoJoin = countRoot.join("mutableFlightDetails",
				JoinType.LEFT);
		Predicate countEtaCondition = getETAPredicate(dto, cb, countRoot, countMutableFlightsInfoJoin);

		if (countEtaCondition != null) {
			countPredicates.add(countEtaCondition);
		}
		countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[] {}));
		TypedQuery countQuert = em.createQuery(countQuery);
		Optional countResult = countQuert.getResultList().stream().findFirst();
		return countResult.isPresent() ? (Long) countResult.get() : 0L;
	}

	private Predicate getETAPredicate(FlightsRequestDto dto, CriteriaBuilder cb, Root<Flight> flightRoot,
			Join<Flight, MutableFlightDetails> mutableFlightDetailsJoin) {
		Predicate relevantDateExpression = null;
		if (dto.getEtaStart() != null && dto.getEtaEnd() != null) {
			Expression<Date> relevantDate = cb.selectCase(flightRoot.get("direction"))
					.when("O", mutableFlightDetailsJoin.get("etd")).when("I", mutableFlightDetailsJoin.get("eta"))
					.otherwise(mutableFlightDetailsJoin.get("eta")).as(Date.class);
			Predicate startPredicate = cb.greaterThanOrEqualTo(relevantDate, dto.getEtaStart());
			Predicate endPredicate = cb.lessThanOrEqualTo(relevantDate, dto.getEtaEnd());
			relevantDateExpression = cb.and(startPredicate, endPredicate);
		}
		return relevantDateExpression;
	}
}
