/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository.udr;

import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.lookup.Airport;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Rule Repository with custom queries.
 */
public interface UdrRuleRepository extends CrudRepository<UdrRule, Long>,
        JpaSpecificationExecutor<UdrRule> {
    public List<UdrRule> findByDeleted(YesNoEnum deleted);

    @Query("SELECT udr FROM UdrRule udr WHERE udr.deleted = 'N' and udr.author.userId = :authorUserId")
    public List<UdrRule> findUdrRuleByAuthor(
            @Param("authorUserId") String authorUserId);

    @Query("SELECT udr FROM UdrRule udr "
            + "WHERE udr.deleted = 'N' and udr.author.userId = :authorUserId")
    public List<UdrRule> findAllUdrRuleSummaryByAuthor(
            @Param("authorUserId") String authorUserId);

    @Query("SELECT udr FROM UdrRule udr WHERE udr.deleted = 'N'")
    public List<UdrRule> findAllUdrRuleSummary();

    @Query("SELECT udr FROM UdrRule udr WHERE udr.deleted = 'N' and udr.metaData.title = :title and udr.author.userId = :authorUserId")
    public UdrRule getUdrRuleByTitleAndAuthor(@Param("title") String title,
            @Param("authorUserId") String authorUserId);

    @Query("SELECT kb FROM KnowledgeBase kb WHERE kb.kbName = :name")
    public KnowledgeBase getKnowledgeBaseByName(@Param("name") String name);

    @Query("SELECT rl FROM Rule rl WHERE rl.knowledgeBase.id = :kbId")
    public List<Rule> getRuleByKbId(@Param("kbId") Long kbId);

    @Query("SELECT udr FROM UdrRule udr WHERE  udr.deleted = 'N' and (udr.metaData.endDt is null or udr.metaData.endDt >= :targetDate)")
    public List<UdrRule> findValidUdrRuleByDate(
            @Param("targetDate") Date targetDate);

    @Query("SELECT udr.title FROM UdrRule udr WHERE udr.deleted = 'N' and udr.title like :titlePrefix and udr.author.userId = :authorUserId")
    public List<String> getUdrTitleByTitlePrefixAndAuthor(
            @Param("titlePrefix") String title,
            @Param("authorUserId") String authorUserId);

    @Query("SELECT udr FROM UdrRule udr where udr.deleted = :deleted AND udr.metaData.enabled = :enabled AND (udr.metaData.endDt is null OR udr.metaData.endDt >= CURRENT_DATE)")
    public List<UdrRule> findByDeletedAndEnabled(
            @Param("deleted") YesNoEnum deleted,
            @Param("enabled") YesNoEnum enabled);
    
    @Query("SELECT udr FROM Rule r, UdrRule udr WHERE r.id =:ruleId AND udr.id = r.parent")
    public UdrRule findUdrRuleByRuleId (
    		@Param("ruleId") Long ruleId);
    
    default UdrRule findOne(Long id)
    {
    	return findById(id).orElse(null);
    }
}
