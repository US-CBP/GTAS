/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.udr;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;
import gov.gtas.model.BaseEntity;
import gov.gtas.model.User;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The Persistence Layer service for UDR (User Defined Rule). Since an UDR can
 * have complex AND-OR logic in it, it can give rise to multiple rules for the
 * Rule Engine. The latter are called engine rules.
 */
public interface RulePersistenceService {
	/**
	 * Creates UDR rule.
	 * 
	 * @param rule
	 *            the UDR rule object to persist in the DB.
	 * @param userId
	 *            the user persisting the rule (usually also the rule author.)
	 * @return the persisted rule.
	 */
	public UdrRule create(UdrRule rule, String userId);

	/**
	 * Deletes a UDR rule. The object is not physically deleted, but a "delete flag"
	 * is set to indicate it is no longer in use.
	 * 
	 * @param id
	 *            the Id of the rule to delete.
	 * @param userId
	 *            the user Id of the person deleting the rule (usually its author).
	 * @return the deleted rule.
	 */
	public UdrRule delete(Long id, String userId);

	/**
	 * Find and return the list of all rules that have the "delete flag" set to "N".
	 * 
	 * @return list of all non-deleted rules.
	 */
	public List<UdrRule> findAll();

	/**
	 * Find and return the list of all UDR that have the "delete flag" set to "N".
	 * If the userId is not null or empty, then the list is filtered by UDRs
	 * authored by userId.
	 * 
	 * @param userId
	 *            the user Id of the person who authored the UDR,
	 * @return list of all non-deleted rules.
	 */
	public List<UdrRule> findAllUdrSummary(String userId);

	/**
	 * Updates a list of entities.
	 * 
	 * @param entities
	 *            the list of entities to update.
	 * @return the updated list of entities.
	 */
	public Collection<? extends BaseEntity> batchUpdate(final Collection<? extends BaseEntity> entities);

	/**
	 * Updates a UDR rule or its children engine rules.
	 * 
	 * @param rule
	 *            the UDR rule to update.
	 * @param userId
	 *            the user Id of the person updating the rule (usually its author).
	 * @return the updated rule.
	 */
	public UdrRule update(UdrRule rule, String userId);

	/**
	 * Fetches a UDR rule by its Id. Note: deleted rules can also be fetched by
	 * specifying their Id.
	 * 
	 * @param id
	 *            the Id of the rule to fetch.
	 * @return the fetched rule or null.
	 */
	public UdrRule findById(Long id);

	/**
	 * Fetches a UDR rule by its author and Title.
	 * 
	 * @param title
	 *            the rule title.
	 * @param authorUserId
	 *            the author's user Id.
	 * @return the fetched rule or null.
	 */
	@PreAuthorize(PRIVILEGE_ADMIN)
	public UdrRule findByTitleAndAuthor(String title, String authorUserId);

	/**
	 * Fetches a list of rules created by the specified author.
	 * 
	 * @param authorUserId
	 *            the user Id of the author.
	 * @return list of rules authored by the specified author or an empty list.
	 */
	public List<UdrRule> findByAuthor(String authorUserId);

	/**
	 * Fetches a list of rules that are valid on a particular target date. (i.e.,
	 * rule.startDate <= targetDate and rule.endDate >= targetDate) Note: the time
	 * part of target date will not be used.
	 * 
	 * @param targetDate
	 *            the target date.
	 * @return list of rules valid on the target date or an empty list.
	 */
	public List<UdrRule> findValidUdrOnDate(Date targetDate);

	/**
	 * Fetches the latest version of the default knowledge base.
	 * 
	 * @return the knowledge base.
	 */
	public KnowledgeBase findUdrKnowledgeBase();

	/**
	 * Fetches the latest version of the named knowledge base.
	 * 
	 * @param kbName
	 *            the name of the knowledge base record to fetch.
	 * @return the knowledge base.
	 */
	public KnowledgeBase findUdrKnowledgeBase(String kbName);

	/**
	 * Saves or updates the knowledge base.
	 * 
	 * @param kb
	 *            the knowledge base.
	 * @return the saved knowledge base.
	 */
	public KnowledgeBase saveKnowledgeBase(KnowledgeBase kb);

	/**
	 * Deletes the knowledge base.
	 * 
	 * @param kbName
	 *            the name of the knowledge base
	 * @return the deleted knowledge base or null if it does not exist.
	 */
	public KnowledgeBase deleteKnowledgeBase(String kbName);

	/**
	 * Fetches the latest version of the default knowledge base.
	 * 
	 * @return the knowledge base.
	 */
	public List<Rule> findRulesByKnowledgeBaseId(Long id);

	/**
	 * Used for Testing only.
	 * 
	 * @return
	 */
	public EntityManager getEntityManager();

	/**
	 * Updates a UDR rule or its children engine rules. This overloaded method helps
	 * bypasses an EntityExistsException in certain scenarios.
	 * 
	 * @param rule
	 *            the UDR rule to update.
	 * @param user
	 *            the user who represents the author of the update.
	 * @return the updated rule.
	 */
	UdrRule update(UdrRule rule, User user);
}
