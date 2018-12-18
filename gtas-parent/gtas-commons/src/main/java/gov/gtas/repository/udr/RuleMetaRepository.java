package gov.gtas.repository.udr;

import gov.gtas.model.udr.RuleMeta;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface RuleMetaRepository extends CrudRepository<RuleMeta, Long>,
        JpaSpecificationExecutor<RuleMeta> {

    @Modifying
    @Query("Update RuleMeta rm set rm.overMaxHits = true where rm.parent.id in :udrRulesWithTooManyHits")
    void flagUdrRule(@Param("udrRulesWithTooManyHits") Set<Long> udrRulesWithTooManyHits);
}
