package gov.gtas.search;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import gov.gtas.enumtype.FilterOperationsEnum;
import gov.gtas.enumtype.OrderTypeEnum;

public class SearchSpecification<T> implements Specification<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchCriteria criteria;

	public SearchSpecification(SearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

		if (criteria.getOperation().equalsIgnoreCase(FilterOperationsEnum.BETWEEN.toString())) {
			if (root.get(criteria.getKey()).getJavaType() == Date.class) {
				Path<Date> pathValue = root.<Date>get(criteria.getKey());
				DateRange range = (DateRange) criteria.getValue();
				if (criteria.isOrderBy()) {
					if (OrderTypeEnum.DESCENDING.equals(criteria.getOrderType())) {
						query.orderBy(criteriaBuilder.desc(pathValue));
					} else {
						query.orderBy(criteriaBuilder.asc(pathValue));
					}
				}
				return criteriaBuilder.between(pathValue, range.getStart(), range.getEnd());
			}

		} else if (criteria.getOperation().equalsIgnoreCase(FilterOperationsEnum.EQUAL.toString())) {
			if (root.get(criteria.getKey()).getJavaType() == String.class) {
				Path<String> pathValue = root.<String>get(criteria.getKey());
				
				if (criteria.isOrderBy()) {
					if (OrderTypeEnum.DESCENDING.equals(criteria.getOrderType())) {
						query.orderBy(criteriaBuilder.desc(pathValue));
					} else {

						query.orderBy(criteriaBuilder.asc(pathValue));

					}
				}
				return criteriaBuilder.equal(pathValue, criteria.getValue());
				
			} else if (root.get(criteria.getKey()).getJavaType() == Boolean.class) {
				Path<Boolean> pathValue = root.<Boolean>get(criteria.getKey());
				if (criteria.isOrderBy()) {
					if (OrderTypeEnum.DESCENDING.equals(criteria.getOrderType())) {
						query.orderBy(criteriaBuilder.desc(pathValue));
					} else {

						query.orderBy(criteriaBuilder.asc(pathValue));

					}
				}
				return criteriaBuilder.equal(pathValue,
						Boolean.parseBoolean((String) criteria.getValue()));
			}

			else {
				if (criteria.isOrderBy()) {
					if (OrderTypeEnum.DESCENDING.equals(criteria.getOrderType())) {
						query.orderBy(criteriaBuilder.desc(root.get(criteria.getKey())));
					} else {

						query.orderBy(criteriaBuilder.asc(root.get(criteria.getKey())));

					}
				}

				return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
			}
		}

		return null;
	}

}
