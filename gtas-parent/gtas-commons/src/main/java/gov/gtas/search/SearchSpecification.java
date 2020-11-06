package gov.gtas.search;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import gov.gtas.enumtype.FilterOperationsEnum;

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
				DateRange range = (DateRange) criteria.getValue();
				return criteriaBuilder.between(root.<Date>get(criteria.getKey()), range.getStart(), range.getEnd());
			}
			
		
		} else if (criteria.getOperation().equalsIgnoreCase(FilterOperationsEnum.EQUAL.toString())) {
			if (root.get(criteria.getKey()).getJavaType() == String.class) {
				return criteriaBuilder.equal(root.<String>get(criteria.getKey()), criteria.getValue());
			} else if (root.get(criteria.getKey()).getJavaType() == Boolean.class) {
				return criteriaBuilder.equal(root.<Boolean>get(criteria.getKey()),
						Boolean.parseBoolean((String) criteria.getValue()));
			}

			else {
				return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
			}
		}

		return null;
	}

}
