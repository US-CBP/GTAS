package gov.gtas.repository;

import org.springframework.data.repository.CrudRepository;

import gov.gtas.model.ApiAccess;
import gov.gtas.model.lookup.Airport;

public interface ApiAccessRepository extends CrudRepository<ApiAccess, Long> {

	default ApiAccess findOne(Long id) {
		return findById(id).orElse(null);
	}
}
