package gov.gtas.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import gov.gtas.model.SignupRequest;

public class SignupRequestSpecification implements Specification<SignupRequest> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchCriteria criteria;
	

	public SignupRequestSpecification(SearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}


	@Override
	public Predicate toPredicate(Root<SignupRequest> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		
		if (criteria.getOperation().equalsIgnoreCase("=")) {
			return criteriaBuilder.equal(root.<String>get(criteria.getKey()), criteria.getValue().toString());
		} else if (criteria.getOperation().equalsIgnoreCase(":")) {
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
