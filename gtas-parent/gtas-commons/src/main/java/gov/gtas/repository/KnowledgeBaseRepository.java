package gov.gtas.repository;

import gov.gtas.model.udr.KnowledgeBase;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface KnowledgeBaseRepository extends CrudRepository<KnowledgeBase, Long> {
	
	@Transactional
	@Query("select kb from KnowledgeBase kb left join fetch kb.watchlistItemsInKb left join fetch kb.udrRulesInKb where kb.kbName = :name")
	public KnowledgeBase anewName(@Param("name") String name);
}
