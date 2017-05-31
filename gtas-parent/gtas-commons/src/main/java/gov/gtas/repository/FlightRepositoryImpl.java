/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import static gov.gtas.constant.FlightCategoryConstants.ALL_FLIGHTS;
import static gov.gtas.constant.FlightCategoryConstants.DOMESTIC_FLIGHTS;
import static gov.gtas.constant.FlightCategoryConstants.INTERNATIONAL_FLIGHTS;
import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;
import gov.gtas.model.Flight;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.services.dto.SortOptionsDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
	private static final Logger logger = LoggerFactory
			.getLogger(FlightRepositoryImpl.class);

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

		// dates
		Predicate etaCondition;
		if (dto.getEtaStart() != null && dto.getEtaEnd() != null) {
			Path<Date> eta = root.<Date> get("eta");
			Predicate startPredicate = cb.or(
					cb.isNull(eta),
					cb.greaterThanOrEqualTo(root.<Date> get("eta"),
							dto.getEtaStart()));
			Predicate endPredicate = cb.or(cb.isNull(eta),
					cb.lessThanOrEqualTo(eta, dto.getEtaEnd()));
			etaCondition = cb.and(startPredicate, endPredicate);
			predicates.add(etaCondition);
		}

		// sorting
		if (dto.getSort() != null) {
			List<Order> orders = new ArrayList<>();
			for (SortOptionsDto sort : dto.getSort()) {
				Expression<?> e = root.get(sort.getColumn());
				Order order;
				if ("desc".equalsIgnoreCase(sort.getDir())) {
					order = cb.desc(e);
				} else {
					order = cb.asc(e);
				}
				orders.add(order);
			}
			q.orderBy(orders);
		}

		// filters
		if (!CollectionUtils.isEmpty(dto.getOriginAirports())) {
			Expression<String> originExp = root.<String> get("origin");
			Predicate originPredicate = originExp.in(dto.getOriginAirports());
			Predicate originAirportsPredicate = cb.and(originPredicate);
			predicates.add(originAirportsPredicate);
		}

		if (!CollectionUtils.isEmpty(dto.getDestinationAirports())) {
			Expression<String> destExp = root.<String> get("destination");
			Predicate destPredicate = destExp.in(dto.getDestinationAirports());
			Predicate destAirportsPredicate = cb.and(destPredicate);
			predicates.add(destAirportsPredicate);
		}

		if (StringUtils.isNotBlank(dto.getFlightNumber())) {
			String likeString = String.format("%%%s%%", dto.getFlightNumber());
			predicates.add(cb.like(root.<String> get("fullFlightNumber"),
					likeString));
		}
		/*
		 * hack: javascript sends the empty string represented by the 'all'
		 * dropdown value as 'A', so we check for that here to mean 'any
		 * direction'
		 */
		if (StringUtils.isNotBlank(dto.getDirection())
				&& !"A".equals(dto.getDirection())) {
			predicates.add(cb.equal(root.<String> get("direction"),
					dto.getDirection()));
		}

		// filter flights - international, domestic, all
		Expression<String> origCountryExp = root.<String> get("originCountry");
		Expression<String> desCountryExp = root
				.<String> get("destinationCountry");
		Predicate origNotEPredicate = cb.notEqual(origCountryExp, "USA");
		Predicate destNotEPredicate = cb.notEqual(desCountryExp, "USA");
		Predicate origEPredicate = cb.equal(origCountryExp, "USA");
		Predicate destEPredicate = cb.equal(desCountryExp, "USA");
		if (dto.getFlightCategory() != null) {
			if (dto.getFlightCategory().equalsIgnoreCase(INTERNATIONAL_FLIGHTS)) {
				logger.info("User selects International Flights.");
				Predicate interPredicatePart1 = cb.and(origNotEPredicate,
						destEPredicate);
				Predicate interPredicatePart2 = cb.and(origEPredicate,
						destNotEPredicate);
				Predicate interPredicate = cb.or(interPredicatePart1,
						interPredicatePart2);
				predicates.add(interPredicate);
			} else if (dto.getFlightCategory().equalsIgnoreCase(
					DOMESTIC_FLIGHTS)) {
				logger.info("User selects Domestic Flights.");
				Predicate domesticPredicate = cb.and(origEPredicate,
						destEPredicate);
				predicates.add(domesticPredicate);

			} else if (dto.getFlightCategory().equalsIgnoreCase(ALL_FLIGHTS)) {
				logger.info("User selects All Flights.");
			}
		}

		q.select(root).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Flight> typedQuery = em.createQuery(q);

		// total count
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		countQuery.select(cb.count(countQuery.from(Flight.class))).where(
				predicates.toArray(new Predicate[] {}));
		Long count = em.createQuery(countQuery).getSingleResult();

		// pagination
		int pageNumber = dto.getPageNumber();
		int pageSize = dto.getPageSize();
		int firstResultIndex = (pageNumber - 1) * pageSize;
		typedQuery.setFirstResult(firstResultIndex);
		typedQuery.setMaxResults(dto.getPageSize());

		logger.debug(typedQuery.unwrap(org.hibernate.Query.class)
				.getQueryString());
		List<Flight> results = typedQuery.getResultList();

		return new ImmutablePair<>(count, results);
	}

	@Override
	@Transactional
	@PreAuthorize(PRIVILEGE_ADMIN)
	public void deleteAllMessages() throws Exception {
		String[] sqlScript = { 
				"delete from disposition",							
				"delete from hit_detail", "delete from hits_summary",
				"delete from  document",				
				"delete from apis_message_passenger",
				"delete from flight_passenger", 
				"delete from apis_message_flight", "delete from flight_leg",
				"delete from seat", 
				"delete from apis_message_reporting_party",
				"delete from reporting_party",
				"delete from apis_message",			
				"delete from pnr_passenger", 
				"delete from pnr_flight", "delete from flight",
				"delete from pnr_agency", "delete from agency",
				"delete from pnr_credit_card", "delete from credit_card",
				"delete from pnr_frequent_flyer", "delete from frequent_flyer",
				"delete from pnr_phone", "delete from phone",
				"delete from pnr_email", "delete from email",
				"delete from pnr_address",				
				"delete from pnr_dwelltime",
				"delete from address", "delete from dwell_time",
				"delete from  pnr",
				"delete from  message",
				"delete from  passenger",				
				"delete from loader_audit_logs",
				"delete from error_detail",
				"delete from audit_log",
				"delete from dashboard_message_stats",
				"delete from bag"};

		Session session = em.unwrap(Session.class);
		for (String sql : sqlScript) {
			SQLQuery q = session.createSQLQuery(sql);
			q.executeUpdate();
		}
	}
}