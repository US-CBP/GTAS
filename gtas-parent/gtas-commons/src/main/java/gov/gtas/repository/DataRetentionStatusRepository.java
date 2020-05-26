package gov.gtas.repository;

import gov.gtas.model.DataRetentionStatus;
import org.springframework.data.repository.CrudRepository;

public interface DataRetentionStatusRepository  extends CrudRepository<DataRetentionStatus, Long> {
}
