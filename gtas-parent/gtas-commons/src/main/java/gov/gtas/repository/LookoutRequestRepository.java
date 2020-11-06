package gov.gtas.repository;

import gov.gtas.model.LookoutRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LookoutRequestRepository  extends CrudRepository<LookoutRequest, Long> {
    List<LookoutRequest> findTop500ByOrderByIdAsc();
}
