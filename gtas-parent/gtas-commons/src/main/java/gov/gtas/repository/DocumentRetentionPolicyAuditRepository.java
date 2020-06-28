package gov.gtas.repository;

import gov.gtas.model.DocumentRetentionPolicyAudit;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRetentionPolicyAuditRepository  extends CrudRepository<DocumentRetentionPolicyAudit, Long> {
}
