/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

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
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import gov.gtas.model.Flight;
import gov.gtas.services.dto.FlightsRequestDto;
import gov.gtas.services.dto.SortOptionsDto;

@Repository
public class FlightRepositoryImpl implements FlightRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(FlightRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    public FlightRepositoryImpl() {
    }

    public Pair<Long, List<Flight>> findByCriteria(FlightsRequestDto dto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Flight> q = cb.createQuery(Flight.class);
        Root<Flight> root = q.from(Flight.class);
        List<Predicate> predicates = new ArrayList<Predicate>();

        // dates
        Predicate etaCondition = null;
        if (dto.getEtaStart() != null && dto.getEtaEnd() != null) {
            Path<Date> eta = root.<Date> get("eta");
            Predicate startPredicate = cb.or(cb.isNull(eta),
                    cb.greaterThanOrEqualTo(root.<Date> get("eta"), dto.getEtaStart()));
            Predicate endPredicate = cb.or(cb.isNull(eta), cb.lessThanOrEqualTo(eta, dto.getEtaEnd()));
            etaCondition = cb.and(startPredicate, endPredicate);
            predicates.add(etaCondition);
        }

        // sorting
        if (dto.getSort() != null) {
            List<Order> orders = new ArrayList<>();
            for (SortOptionsDto sort : dto.getSort()) {
                Expression<?> e = root.get(sort.getColumn());
                Order order = null;
                if (sort.getDir().equals("desc")) {
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
            predicates.add(cb.like(root.<String> get("fullFlightNumber"), likeString));
        }
        /*
         * hack: javascript sends the empty string represented by the 'all'
         * dropdown value as 'A', so we check for that here to mean 'any
         * direction'
         */
        if (StringUtils.isNotBlank(dto.getDirection()) && !"A".equals(dto.getDirection())) {
            predicates.add(cb.equal(root.<String> get("direction"), dto.getDirection()));
        }
        
        q.select(root).where(predicates.toArray(new Predicate[] {}));
        TypedQuery<Flight> typedQuery = em.createQuery(q);

        // total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Flight.class))).where(predicates.toArray(new Predicate[] {}));
        Long count = em.createQuery(countQuery).getSingleResult();

        // pagination
        int pageNumber = dto.getPageNumber();
        int pageSize = dto.getPageSize();
        int firstResultIndex = (pageNumber - 1) * pageSize;
        typedQuery.setFirstResult(firstResultIndex);
        typedQuery.setMaxResults(dto.getPageSize());

        logger.debug(typedQuery.unwrap(org.hibernate.Query.class).getQueryString());
        List<Flight> results = typedQuery.getResultList();

        return new ImmutablePair<Long, List<Flight>>(count, results);
    }
    
    @Transactional
    public void deleteAllMessages() throws Exception {
        String[] sqlScript = {  
                "delete from document", 
                "delete from hit_detail", 
                "delete from hits_summary", 
                "delete from disposition", 
                "delete from pnr_passenger", 
                "delete from apis_message_passenger", 
                "delete from flight_passenger", 
                "delete from pnr_flight", 
                "delete from apis_message_flight", 
                "delete from flight_leg", 
                "delete from seat", 
                "delete from flight", 
                "delete from pnr_agency", 
                "delete from pnr_credit_card", 
                "delete from pnr_frequent_flyer", 
                "delete from pnr_phone", 
                "delete from pnr_email", 
                "delete from pnr_address", 
                "delete from apis_message_reporting_party", 
                "delete from reporting_party", 
                "delete from agency", 
                "delete from credit_card", 
                "delete from frequent_flyer", 
                "delete from phone", 
                "delete from email", 
                "delete from address", 
                "delete from pnr", 
                "delete from apis_message", 
                "delete from message", 
                "delete from passenger" 
        };
        
        Session session = em.unwrap(Session.class);
        for (String sql : sqlScript) {
            SQLQuery q = session.createSQLQuery(sql); 
            q.executeUpdate();
        }
    }
}