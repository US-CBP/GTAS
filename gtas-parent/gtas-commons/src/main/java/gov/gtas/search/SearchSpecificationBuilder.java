package gov.gtas.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;

public class SearchSpecificationBuilder<T> {
	private final List<SearchCriteria> queryParams;
	private final FilterOperations filterOperations = initializeFilterOperations();
	
	private FilterOperations initializeFilterOperations() {
		FilterOperations ops = new FilterOperations();
		return ops;
	}
	

	public SearchSpecificationBuilder() {
		queryParams = new ArrayList<>();
	}
	
	public void addFilterOperation(String fieldName, String operation) {
		filterOperations.put(fieldName, operation);
	}
	
	public SearchSpecificationBuilder<T> with(Map<String, Object> params) {
		params.keySet().stream().forEach(fieldName -> {
			queryParams.add(new SearchCriteria(fieldName, filterOperations.get(fieldName), params.get(fieldName)));
		});

		return this;
	}
	
	public SearchSpecificationBuilder<T> with(String key, String operation, Object value) {
		queryParams.add(new SearchCriteria(key, operation, value));
		return this;
	}
	
	public Specification<T> build() {
		if (queryParams.isEmpty()) {
			return null;
		}

		List<Specification<T>> specs = queryParams.stream().map(SearchSpecification<T>::new).collect(Collectors.toList());

		Specification<T> result = specs.get(0);

		for (int i = 1; i < queryParams.size(); i++) {
			result = Specification.where(result).and(specs.get(i));
		}
		return result;
	}
}
