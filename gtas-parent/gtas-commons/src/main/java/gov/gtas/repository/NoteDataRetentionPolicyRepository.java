package gov.gtas.repository;

import gov.gtas.model.NoteDataRetentionPolicyAudit;
import org.springframework.data.repository.CrudRepository;

public interface NoteDataRetentionPolicyRepository extends CrudRepository<NoteDataRetentionPolicyAudit, Long> {
}
