/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.*;
import gov.gtas.services.PassengerService;
import gov.gtas.services.dto.PassengersRequestDto;
import gov.gtas.services.dto.SortOptionsDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public class PassengerRepositoryImpl implements PassengerRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(PassengerRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
	private PassengerService pService;
 
	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.repository.PassengerRepositoryCustom#
	 * findExistingPassengerByAttributes(java.lang.String, java.lang.String,
	 * java.lang.String, java.util.Date, java.lang.String)
	 */
	@Override
	@Transactional
	public boolean findExistingPassengerByAttributes(String firstName,
			String lastName, String middleName, String gender, Date dob,
			String passengerType) {
		boolean found = true;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Passenger> paxcq = cb.createQuery(Passenger.class);
		Root<Passenger> root = paxcq.from(Passenger.class);
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(firstName)) {
			String likeString = String
					.format("%%%s%%", firstName.toUpperCase());
			predicates.add(cb.like(root.<String> get("firstName"), likeString));
		}
		if (StringUtils.isNotBlank(middleName)) {
			String likeString = String.format("%%%s%%", middleName.toUpperCase());
			predicates.add(cb.like(root.<String> get("middleName"), likeString));
		}
		if (StringUtils.isNotBlank(lastName)) {
			String likeString = String.format("%%%s%%", lastName.toUpperCase());
			predicates.add(cb.like(root.<String> get("lastName"), likeString));
		}
		if (StringUtils.isNotBlank(gender)) {
			String likeString = String.format("%%%s%%", gender.toUpperCase());
			predicates.add(cb.like(root.<String> get("gender"), likeString));
		}
		if (dob != null) {
			predicates.add(cb.equal(root.<String> get("dob"), dob));
		}
		if (StringUtils.isNotBlank(passengerType)) {
			String likeString = String.format("%%%s%%",
					passengerType.toUpperCase());
			predicates.add(cb.like(root.<String> get("passengerType"),
					likeString));
		}
		paxcq.select(root).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Passenger> paxtq = em.createQuery(paxcq);
		if (CollectionUtils.isEmpty(paxtq.getResultList())) {
			found = false;
		}
		return found;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.repository.PassengerRepositoryCustom#findByAttributes(java.lang
	 * .Long)
	 */  
	@Override
	@Transactional
	public List<Passenger> findByAttributes(Long pId, String docNum,
			String docIssuCountry, Date docExpDate) {
		//NOTE: for perfect 7 
		Passenger pax = em.find(Passenger.class, pId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Passenger> paxcq = cb.createQuery(Passenger.class);
		Root<Passenger> root = paxcq.from(Passenger.class);
		List<Predicate> predicates = new ArrayList<>();
		// passenger-related
		predicates.add(cb.notEqual(root.<Long> get("id"), pax.getId()));
		if (StringUtils.isNotBlank(pax.getPassengerDetails().getFirstName())) {
			String likeString = String.format("%%%s%%", pax.getPassengerDetails().getFirstName()
					.toUpperCase());
			predicates.add(cb.like(root.<String> get("firstName"), likeString));
		}
		if (StringUtils.isNotBlank(pax.getPassengerDetails().getLastName())) {
			String likeString = String.format("%%%s%%", pax.getPassengerDetails().getLastName()
					.toUpperCase());
			predicates.add(cb.like(root.<String> get("lastName"), likeString));
		}
		if (StringUtils.isNotBlank(pax.getPassengerDetails().getGender())) {
			String likeString = String.format("%%%s%%", pax.getPassengerDetails().getGender()
					.toUpperCase());
			predicates.add(cb.like(root.<String> get("gender"), likeString));
		}
		if (pax.getPassengerDetails().getDob() != null) {
			predicates.add(cb.equal(root.<String> get("dob"), pax.getPassengerDetails().getDob()));
		}
		// document-related
		if (StringUtils.isNotBlank(docNum)) {
			String likeString = String.format("%%%s%%", docNum.toUpperCase());
			predicates.add(cb.like(
					root.join("documents").get("documentNumber"), likeString));
		}
		if (StringUtils.isNotBlank(docIssuCountry)) {
			String likeString = String.format("%%%s%%",
					docIssuCountry.toUpperCase());
			predicates.add(cb.like(root.join("documents")
					.get("issuanceCountry"), likeString));
		}
		if (docExpDate != null) {
			predicates.add(cb.equal(root.join("documents")
					.get("expirationDate"), docExpDate));
		}
		paxcq.select(root).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Passenger> paxtq = em.createQuery(paxcq);
		return paxtq.getResultList();
	}
	
    /**
     * This was an especially difficult query to construct mainly because of a
     * bug in hibernate. See https://hibernate.atlassian.net/browse/HHH-7321.
     * The problem is that the left join on hits requires a dual 'on' condition
     * (hits.pax_id = pax.id and hits.flight_id = flight.id). Constructing this
     * in JPA seems perfectly valid, and Hibernate converts this into a 'with'
     * statement with multiple conditions. I get the exception
     * "org.hibernate.hql.internal.ast.QuerySyntaxException: with-clause
     * referenced two different from-clause elements."
     */
    @Override    
    public Pair<Long, List<Object[]>> findByCriteria(Long flightId, PassengersRequestDto dto) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);
		Root<Passenger> pax = q.from(Passenger.class);

		//joins
		Join<Passenger, Flight> flight = pax.join("flight");
		Join<Passenger, HitsSummary> hits = pax.join("hits", JoinType.LEFT);
		Join<Passenger, PassengerWLTimestamp> fuzzyHits = pax.join("passengerWLTimestamp", JoinType.LEFT);
		Join<Flight, MutableFlightDetails> mutableFlightDetailsJoin = flight.join("mutableFlightDetails", JoinType.LEFT);
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
				} else if (column.equals("onWatchList")) {
					orderByItem.add(hits.get("watchListHitCount"));
					orderByItem.add(fuzzyHits.get("hitCount"));
				} else if ("eta".equalsIgnoreCase(column)) {
					orderByItem.add(mutableFlightDetailsJoin.get("eta"));
					//!!!!! THIS COVERS THE ELSE STATEMENT !!!!!
				} else if (!"documentNumber".equalsIgnoreCase(column)){
					orderByItem.add(paxDetailsJoin.get(column));
				}
				if (sort.getDir().equals("desc")) {
					for (Expression<?> e : orderByItem){
						if ("onWatchList".equalsIgnoreCase(column) || "onRuleHitList".equalsIgnoreCase(column) ) {
							// The fuzzy matching can occure when the hits summary is null. Coalesce these values to a 0
							// in order to have fuzzy matching show up in ordered form.
							orderList.add(cb.desc(cb.coalesce(e, 0)));
						} else {
							orderList.add(cb.desc(e));
						}
					}
				} else {
					for (Expression<?> e : orderByItem) {
						if ("onWatchList".equalsIgnoreCase(column) || "onRuleHitList".equalsIgnoreCase(column) ) {
							orderList.add(cb.asc(cb.coalesce(e, 0)));
						} else {
							orderList.add(cb.asc(e));
						}
					}
				}
			}
			q.orderBy(orderList);
		}

		q.multiselect(pax, flight, hits, fuzzyHits).where(predicates.toArray(new Predicate[]{}));
		TypedQuery<Object[]> typedQuery = addPagination(q, dto.getPageNumber(), dto.getPageSize());

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
		cnt.select(cb.count(cntFlight)).where(cntPred.toArray(new Predicate[]{}));
		Long count = em.createQuery(cnt).getSingleResult();

		if (flightId != null) {
			typedQuery.setParameter("flightId", flightId);
		}

		logger.debug(typedQuery.unwrap(org.hibernate.Query.class).getQueryString());
//        System.out.println(typedQuery.unwrap(org.hibernate.Query.class).getQueryString());
		List<Object[]> results = typedQuery.getResultList();

		return new ImmutablePair<>(count, results);
    }
    
    /**
     * Ended up using a native query here. The inner query finds the most recent
     * disposition in the history and uses this as the basis for finding all the
     * other information. Not particularly efficient. May consider having a
     * separate 'case' table that stores most recent disposition status.
     */
    @Override
    public List<Object[]> findAllDispositions() {
        String nativeQuery = 
                "select d1.passenger_id, d1.flight_id, p.first_name, p.last_name, p.middle_name, f.full_flight_number, d1.created_at, s.name"
                + " from disposition d1"
                + " join ("
                + "   select passenger_id, flight_id, max(created_at) maxdate"
                + "   from disposition"
                + "   group by passenger_id, flight_id"
                + " ) d2"
                + " on d1.created_at = d2.maxdate"
                + "   and d1.passenger_id = d2.passenger_id"
                + "   and d1.flight_id = d2.flight_id"
                + " join passenger p on p.id = d1.passenger_id"
                + " join flight f on f.id = d1.flight_id"
                + " join disposition_status s on s.id = d1.status_id"
                + " order by d1.created_at desc";

        Query q = em.createNativeQuery(nativeQuery);
        @SuppressWarnings("unchecked")
        List<Object[]> results = q.getResultList();
        
        return results;
    }

	private List<Predicate> createPredicates(CriteriaBuilder cb, PassengersRequestDto dto, Join<Passenger, Flight> flight) {
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
         * complete hack: we're returning more results than the 
         * pagesize b/c the service will potentially throw some of
         * them away.  This is all b/c the left join on hitssummary
         * will not work correctly if we have to check both flight id
         * and passenger id.
         */
        typedQuery.setMaxResults(pageSize * 3);
        return typedQuery;
    }
    
    private Set<String> flightColumns = new HashSet<>(Arrays.asList("fullFlightNumber", "etd"));
    private boolean isFlightColumn(String c) {
        return flightColumns.contains(c);
    }

	@Override
	public Passenger findExistingPassengerWithAttributes(String firstName, String lastName, String middleName,
			String gender, Date dob, String passengerType) {
		Passenger existing = null;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Passenger> paxcq = cb.createQuery(Passenger.class);
		Root<Passenger> root = paxcq.from(Passenger.class);
		List<Predicate> predicates = new ArrayList<>();

		if (StringUtils.isNotBlank(firstName)) {
			String likeString = String
					.format("%%%s%%", firstName.toUpperCase());
			predicates.add(cb.like(root.<String> get("firstName"), likeString));
		}
		if (StringUtils.isNotBlank(middleName)) {
			String likeString = String.format("%%%s%%", middleName.toUpperCase());
			predicates.add(cb.like(root.<String> get("middleName"), likeString));
		}
		if (StringUtils.isNotBlank(lastName)) {
			String likeString = String.format("%%%s%%", lastName.toUpperCase());
			predicates.add(cb.like(root.<String> get("lastName"), likeString));
		}
		if (StringUtils.isNotBlank(gender)) {
			String likeString = String.format("%%%s%%", gender.toUpperCase());
			predicates.add(cb.like(root.<String> get("gender"), likeString));
		}
		if (dob != null) {
			predicates.add(cb.equal(root.<String> get("dob"), dob));
		}
		if (StringUtils.isNotBlank(passengerType)) {
			String likeString = String.format("%%%s%%",
					passengerType.toUpperCase());
			predicates.add(cb.like(root.<String> get("passengerType"),
					likeString));
		}
		paxcq.select(root).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Passenger> paxtq = em.createQuery(paxcq);
		if (!CollectionUtils.isEmpty(paxtq.getResultList())) {
			existing = (Passenger)paxtq.getResultList().get(0);
		}
		return existing;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(Transactional.TxType.MANDATORY)
	public List<Passenger> getPassengersByFlightIdAndName(Long flightId, String firstName, String lastName) {
		String nativeQuery = "SELECT p.* FROM flight_passenger fp join passenger p ON (fp.passenger_id = p.id) where "
				+ "fp.flight_id = (\""+flightId+"\") "
				+ "AND UPPER(p.first_name) = UPPER(\""+firstName+"\") "
				+ "AND UPPER(p.last_name) = UPPER(\""+lastName+"\")";
		return (List<Passenger>) em.createNativeQuery(nativeQuery, Passenger.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Passenger> getPassengersByFlightId(Long flightId) {
		String nativeQuery = "SELECT p.* FROM flight_passenger fp join passenger p ON (fp.passenger_id = p.id) where fp.flight_id = (\""+flightId+"\")";
		return (List<Passenger>) em.createNativeQuery(nativeQuery, Passenger.class).getResultList();
	}
}
