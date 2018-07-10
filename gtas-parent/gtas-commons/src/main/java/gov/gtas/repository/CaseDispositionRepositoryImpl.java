/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Case;
import gov.gtas.model.Flight;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.services.dto.SortOptionsDto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class CaseDispositionRepositoryImpl implements CaseDispositionRepositoryCustom {

    private static final Logger logger = LoggerFactory
            .getLogger(CaseDispositionRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Pair<Long, List<Case>> findByCriteria(CaseRequestDto dto) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Case> q = cb.createQuery(Case.class);
        Root<Case> root = q.from(Case.class);
        List<Predicate> predicates = new ArrayList<>();

        TypedQuery<Case> typedQuery = em.createQuery(q);

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
        
        if (dto.getOneDayLookoutFlag() != null) {
			predicates.add(cb.equal(root.<Boolean> get("oneDayLookoutFlag"),
					dto.getOneDayLookoutFlag()));
		}
        
        
		if (dto.getFlightId() != null) {
			predicates.add(cb.equal(root.<Long> get("flightId"),
					dto.getFlightId()));
		}
		
		if (dto.getPaxId() != null) {
            predicates.add(cb.equal(root.<Long> get("paxId"),
                    dto.getPaxId()));
        }

        if (dto.getPaxName() != null) {
            String likeString = String.format("%%%s%%", dto.getPaxName().toUpperCase());
            predicates.add(cb.like(root.<String>get("paxName"),likeString));
        }

        if (dto.getLastName() != null) { // map this to full pax name
            String likeString = String.format("%%%s%%", dto.getLastName().toUpperCase());
            predicates.add(cb.like(root.<String>get("paxName"),likeString));
        }

        if (dto.getStatus() != null) {
            String likeString = String.format("%%%s%%", dto.getStatus().toUpperCase());
            predicates.add(cb.like(root.<String>get("status"),likeString));
        }

        if (dto.getFlightNumber() != null) {
            predicates.add(cb.equal(root.<Long> get("flightNumber"),
                    dto.getFlightNumber()));
        }

        if (dto.getRuleCatId() != null) {
            predicates.add(cb.equal(root.<Long> get("highPriorityRuleCatId"),
                    dto.getRuleCatId()));
        }

        Predicate etaCondition;
        if (dto.getEtaStart() != null && dto.getEtaEnd() != null) {

            Path<Date> eta = root.<Date> get("flightETADate");
            Predicate startPredicate = cb.or(
                    cb.isNull(eta),
                    cb.greaterThanOrEqualTo(eta,
                            dto.getEtaStart()));
            Predicate endPredicate = cb.or(cb.isNull(eta),
                    cb.lessThanOrEqualTo(eta, dto.getEtaEnd()));
            etaCondition = cb.and(startPredicate, endPredicate);
            predicates.add(etaCondition);
        }

		q.select(root).where(predicates.toArray(new Predicate[] {}));
		typedQuery = em.createQuery(q);

        // total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Case.class))).where(
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
        List<Case> results = typedQuery.getResultList();

        return new ImmutablePair<>(count, results);

    }
}
