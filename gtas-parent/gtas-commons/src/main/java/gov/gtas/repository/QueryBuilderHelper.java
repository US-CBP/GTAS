/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import gov.gtas.services.dto.SortOptionsDto;

/**
 * TBD: consolidate code b/w flight and pax repo's
 */
public class QueryBuilderHelper {
    public Predicate createEtaDateFilterPredicate(Root<?> root, CriteriaBuilder cb, Date etaStart, Date etaEnd) {
        if (etaStart == null || etaEnd == null) {
            return null;
        }
        
        Predicate etaCondition = null;
        Path<Date> eta = root.<Date>get("eta");
        Predicate startPredicate = cb.or(cb.isNull(eta), cb.greaterThanOrEqualTo(root.<Date>get("eta"), etaStart));
        Predicate endPredicate = cb.or(cb.isNull(eta), cb.lessThanOrEqualTo(eta, etaEnd)); 
        etaCondition = cb.and(startPredicate, endPredicate);
        
        return etaCondition;
    }
    
    public List<Order> createOrderBys(Root<?> root, CriteriaBuilder cb, Collection<SortOptionsDto> sortOptions) {
        if (CollectionUtils.isEmpty(sortOptions)) {
            return null;
        }
        
        List<Order> orders = new ArrayList<>();
        for (SortOptionsDto sort : sortOptions) {
            Expression<?> e = root.get(sort.getColumn());
            Order order = null;
            if (sort.getDir().equals("desc")) {
                order = cb.desc(e);
            } else {
                order = cb.asc(e);
            }
            orders.add(order);
        }
        
        return orders;
    }
    
    public List<Predicate> createOriginDestFilterPredicate(Root<?> root, CriteriaBuilder cb, String origin, String dest) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        if (StringUtils.isNotBlank(origin)) {
            predicates.add(cb.equal(root.<String>get("origin"), origin));
        }
        if (StringUtils.isNotBlank(dest)) {
            predicates.add(cb.equal(root.<String>get("destination"), dest));
        }
        
        return predicates;
    }
}
