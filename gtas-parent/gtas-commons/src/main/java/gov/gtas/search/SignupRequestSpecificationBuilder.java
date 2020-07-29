package gov.gtas.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

import gov.gtas.model.SignupRequest;

public class SignupRequestSpecificationBuilder {
	private final List<SearchCriteria> queryParams;
	private final FilterOperations filterOperations = initializeFilterOperations();
	
	private FilterOperations initializeFilterOperations() {
		FilterOperations ops = new FilterOperations();
		return ops;
	}
	

	public SignupRequestSpecificationBuilder() {
		queryParams = new ArrayList<>();
	}
	
	public SignupRequestSpecificationBuilder with(Map<String, Object> params) {
		params.keySet().stream().forEach(fieldName -> {
			queryParams.add(new SearchCriteria(fieldName, filterOperations.get(fieldName), params.get(fieldName)));
		});

		return this;
	}
	
	public SignupRequestSpecificationBuilder with(String key, String operation, Object value) {
		queryParams.add(new SearchCriteria(key, operation, value));
		return this;
	}
	
	public Specification<SignupRequest> build() {
		if (queryParams.isEmpty()) {
			return null;
		}

		List<Specification<SignupRequest>> specs = queryParams.stream().map(SignupRequestSpecification::new).collect(Collectors.toList());

		Specification<SignupRequest> result = specs.get(0);

		for (int i = 1; i < queryParams.size(); i++) {
			result = Specification.where(result).and(specs.get(i));
		}
		return result;
	}
}
