/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.enumtype.HitViewStatusEnum;
import gov.gtas.model.*;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.services.dto.PriorityVettingListRequest;
import gov.gtas.services.dto.RuleCatFilterCheckbox;
import gov.gtas.services.dto.SortOptionsDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static java.time.ZoneOffset.UTC;

@Component
public class PassengerRepositoryImpl implements PassengerRepositoryCustom {
	private static final Logger logger = LoggerFactory.getLogger(PassengerRepositoryImpl.class);

	@PersistenceContext
	private EntityManager em;

	//Performance increase by offsetting hit creation date is substantial.
	@Value("${pvl.hitDetails.createdAtDaysOffset}")
	private Integer pvlHitCreationOffset;

	@SuppressWarnings("DuplicatedCode")
	@Override
	@Transactional
	public Pair<Long, List<Passenger>> priorityVettingListQuery(PriorityVettingListRequest dto,
			Set<UserGroup> userGroupSet, String userId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		// ROOT QUERY
		CriteriaQuery<Passenger> q = cb.createQuery(Passenger.class);
		Root<Passenger> pax = q.from(Passenger.class);
		List<Predicate> rootQueryPredicate = joinAndCreateHitViewPredicates(dto, userGroupSet, cb, q, pax, userId);
		q.select(pax).where(rootQueryPredicate.toArray(new Predicate[] {})).groupBy(pax.get("id"));
		TypedQuery<Passenger> typedQuery = addPagination(q, dto.getPageNumber(), dto.getPageSize(), false);
		List<Passenger> results = typedQuery.getResultList();

		// COUNT QUERY - a version of root query without pagination and a count distinct
		// on pax id
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Passenger> paxCount = countQuery.from(Passenger.class);
		List<Predicate> countQueryPredicate = joinAndCreateHitViewPredicates(dto, userGroupSet, cb, countQuery,
				paxCount, userId);
		countQuery.select(cb.countDistinct(paxCount.get("id"))).where(countQueryPredicate.toArray(new Predicate[] {}));
		TypedQuery<Long> typedCountQuery = em.createQuery(countQuery);
		Optional<Long> countResult = typedCountQuery.getResultList().stream().findFirst();
		Long passengerCount = countResult.orElse(0L);

		return new ImmutablePair<>(passengerCount, results);
	}

	private <T> List<Predicate> joinAndCreateHitViewPredicates(PriorityVettingListRequest dto,
			Set<UserGroup> userGroupSet, CriteriaBuilder cb, CriteriaQuery<T> q, Root<Passenger> pax, String userId) {
		// ROOT QUERY JOINS
		Join<Passenger, Flight> flight = pax.join("flight", JoinType.INNER);
		Join<Flight, MutableFlightDetails> mutableFlightDetailsJoin = flight.join("mutableFlightDetails",
				JoinType.INNER);
		Join<Passenger, HitsSummary> hits = pax.join("hits", JoinType.INNER);
		Join<Flight, FlightCountDownView> flightCountDownViewJoin = flight.join("flightCountDownView", JoinType.INNER);
		Join<Passenger, PassengerDetails> paxDetailsJoin = pax.join("passengerDetails", JoinType.INNER);
		Join<Passenger, HitDetail> hitDetails = pax.join("hitDetails", JoinType.INNER);
		Join<HitDetail, HitViewStatus> hitViewJoin = hitDetails.join("hitViewStatus", JoinType.INNER);
		Join<HitDetail, HitMaker> hitMakerJoin = hitDetails.join("hitMaker", JoinType.INNER);
		Join<HitMaker, HitCategory> hitCategoryJoin = hitMakerJoin.join("hitCategory", JoinType.INNER);
		Join<HitMaker, User> hitMakerUserJoin = hitMakerJoin.join("author", JoinType.INNER);
		// **** PREDICATES ****
		List<Predicate> queryPredicates = new ArrayList<>();
		// TIME UP TO -30 MINUTE PREDICATE
		/*
		 * if (dto.getWithTimeLeft() != null && dto.getWithTimeLeft()) { LocalDateTime
		 * ldt = LocalDateTime.now(ZoneOffset.UTC); ldt = ldt.minusMinutes(30L); Date
		 * oneHourAgo = Date.from(ldt.toInstant(ZoneOffset.UTC)); Predicate
		 * countDownPredicate = cb
		 * .and(cb.greaterThanOrEqualTo(flightCountDownViewJoin.get("countDownTimer"),
		 * oneHourAgo)); queryPredicates.add(countDownPredicate); }
		 */

		Set<HitViewStatusEnum> hitViewStatusEnumSet = new HashSet<>();
		if (dto.getDisplayStatusCheckBoxes() != null) {
			if (dto.getDisplayStatusCheckBoxes().getNewItems() != null
					&& dto.getDisplayStatusCheckBoxes().getNewItems()) {
				hitViewStatusEnumSet.add(HitViewStatusEnum.NEW);
			}
			if (dto.getDisplayStatusCheckBoxes().getReviewed() != null
					&& dto.getDisplayStatusCheckBoxes().getReviewed()) {
				hitViewStatusEnumSet.add(HitViewStatusEnum.REVIEWED);
			}
			if (dto.getDisplayStatusCheckBoxes().getReOpened() != null
					&& dto.getDisplayStatusCheckBoxes().getReOpened()) {
				hitViewStatusEnumSet.add(HitViewStatusEnum.RE_OPENED);
			}
		}
		// Special case. Unused value to give no results.
		if (hitViewStatusEnumSet.isEmpty()) {
			hitViewStatusEnumSet.add(HitViewStatusEnum.NOT_USED);
		}

		Predicate hitViewStatus = cb.in(hitViewJoin.get("hitViewStatusEnum")).value(hitViewStatusEnumSet);
		queryPredicates.add(hitViewStatus);

		if (dto.getMyRulesOnly() != null && dto.getMyRulesOnly()) {
			Predicate authorOnly = cb.equal(hitMakerUserJoin.get("userId"), userId);
			queryPredicates.add(authorOnly);
		}

		if (dto.getRuleCatFilter() != null && !dto.getRuleCatFilter().isEmpty()) {
			Set<String> categoriesToConsider = new HashSet<>();
			for (RuleCatFilterCheckbox rcfc : dto.getRuleCatFilter()) {
				if (rcfc.getValue()) {
					categoriesToConsider.add(rcfc.getName());
				}
			}
			Predicate hitCatsIn = cb.in(hitCategoryJoin.get("name")).value(categoriesToConsider);
			queryPredicates.add(hitCatsIn);
		}
		if (dto.getPriorityVettingListRuleTypes() != null) {
			Set<HitTypeEnum> hitTypeEnums = dto.getPriorityVettingListRuleTypes().hitTypeEnums();
			Predicate hitTypeEnumsIn = cb.in(hitDetails.get("hitEnum")).value(hitTypeEnums);
			queryPredicates.add(hitTypeEnumsIn);
		}
		// LAST NAME PREDICATE
		if (StringUtils.isNotBlank(dto.getLastName())) {
			String likeString = String.format("%%%s%%", dto.getLastName().toUpperCase());
			queryPredicates.add(cb.like(paxDetailsJoin.get("lastName"), likeString));
		}

		// USER GROUP PREDICATE
		Predicate userGroupFilter = cb.and(cb.in(hitCategoryJoin.joinSet("userGroups")).value(userGroupSet));
		queryPredicates.add(userGroupFilter);

		// FLIGHT PREDICATES
		if (!CollectionUtils.isEmpty(dto.getOriginAirports())) {
			Predicate originPredicate = flight.get("origin").in(dto.getOriginAirports());
			Predicate originAirportsPredicate = cb.and(originPredicate);
			queryPredicates.add(originAirportsPredicate);
		}

		if (!CollectionUtils.isEmpty(dto.getDestinationAirports())) {
			Predicate destPredicate = flight.get("destination").in(dto.getDestinationAirports());
			Predicate destAirportsPredicate = cb.and(destPredicate);
			queryPredicates.add(destAirportsPredicate);
		}
		if (StringUtils.isNotBlank(dto.getFlightNumber())) {
			String likeString = String.format("%%%s%%", dto.getFlightNumber().toUpperCase());
			queryPredicates.add(cb.like(flight.get("fullFlightNumber"), likeString));
		}
		/*
		 * hack: javascript sends the empty string represented by the 'all' dropdown
		 * value as '0', so we check for that here to mean 'any direction'
		 */
		if (StringUtils.isNotBlank(dto.getDirection()) && !"A".equals(dto.getDirection())) {
			queryPredicates.add(cb.equal(flight.get("direction"), dto.getDirection()));
		}

		// ETA / ETD PREDICATE - REQUIRED!!
		if (dto.getEtaEnd() == null || dto.getEtaStart() == null) {
			throw new RuntimeException("Flight dates required!");
		} else {
			Expression<Date> relevantDate = cb.selectCase(flight.get("direction"))
					.when("O", mutableFlightDetailsJoin.get("etd")).when("I", mutableFlightDetailsJoin.get("eta"))
					.otherwise(mutableFlightDetailsJoin.get("eta")).as(Date.class);
			Predicate startPredicate = cb.greaterThanOrEqualTo(relevantDate, dto.getEtaStart());
			Predicate endPredicate = cb.lessThanOrEqualTo(relevantDate, dto.getEtaEnd());
			Predicate relevantDateExpression = cb.and(startPredicate, endPredicate);
			queryPredicates.add(relevantDateExpression);


			LocalDateTime ldt = LocalDateTime.ofInstant(dto.getEtaStart().toInstant(), UTC);
			ldt = ldt.minusDays(pvlHitCreationOffset);
			Date etaMinusFour = Date.from(ldt.atZone(UTC).toInstant());

			//HIT DETAIL PREDICATE FOR PERMANCE
			Predicate hitDetailPredicate = cb.and(cb.greaterThan(hitDetails.get("createdDate").as(Date.class), etaMinusFour));
			queryPredicates.add(hitDetailPredicate);

		}

		// SORTING
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
				} else if ("countdown".equalsIgnoreCase(column)) {
					orderByItem.add(flightCountDownViewJoin.get("countDownTimer"));
				} else if ("highPriorityRuleCatId".equalsIgnoreCase(column)) {
					orderByItem.add(hitCategoryJoin.get("severity"));
				} else if ("flightNumber".equalsIgnoreCase(column)) {
					orderByItem.add(flight.get("flightNumber"));
				} else if ("status".equalsIgnoreCase(column) || "action".equalsIgnoreCase(column)) {
					orderByItem.add(hitViewJoin.get("hitViewStatusEnum"));
				} else if (!"documentNumber".equalsIgnoreCase(column)) {
					orderByItem.add(paxDetailsJoin.get(column));
				}
				if (sort.getDir().equals("desc")) {
					for (Expression<?> e : orderByItem) {
						if ("onWatchList".equalsIgnoreCase(column) || "onRuleHitList".equalsIgnoreCase(column)) {
							// The fuzzy matching can occur when the hits summary is null. Coalesce these
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

		return queryPredicates;
	}

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
					orderByItem.add(hits.get("externalHitCount"));
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
		TypedQuery<Passenger> typedQuery = addPagination(q, dto.getPageNumber(), dto.getPageSize(), true);

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

	private <T> TypedQuery<T> addPagination(CriteriaQuery<T> q, int pageNumber, int pageSize, boolean extraResults) {
		int offset = (pageNumber - 1) * pageSize;
		TypedQuery<T> typedQuery = em.createQuery(q);
		typedQuery.setFirstResult(offset);

		/*
		 * complete hack: we're returning more results than the pagesize b/c the service
		 * will potentially throw some of them away. This is all b/c the left join on
		 * hitssummary will not work correctly if we have to check both flight id and
		 * passenger id.
		 */
		if (extraResults) {
			typedQuery.setMaxResults(pageSize * 3);
		} else {
			typedQuery.setMaxResults(pageSize);
		}
		return typedQuery;
	}

	private Set<String> flightColumns = new HashSet<>(Arrays.asList("fullFlightNumber", "etd"));

	private boolean isFlightColumn(String c) {
		return flightColumns.contains(c);
	}

}
