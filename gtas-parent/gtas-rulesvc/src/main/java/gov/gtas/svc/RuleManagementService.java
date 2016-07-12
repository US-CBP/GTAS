/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.watchlist.WatchlistItem;

import java.util.Collection;

/**
 * Interface specification for the rule management service.
 */
public interface RuleManagementService {
	/**
	 * Creates a named Knowledge Base from the given DRL rules string and saves
	 * it in the DB. Note: the KB name must not be the default KB name.
	 * 
	 * @param kbName
	 *            the Knowledge Base name.
	 * @param drlString
	 *            the rules.
	 * @return the KnowledgeBase object.
	 */
	KnowledgeBase createKnowledgeBaseFromDRLString(String kbName,
			String drlString);

	/**
	 * Fetches DRL rules from a Knowledge Base.
	 * 
	 * @param kbName
	 *            the knowledge base name.
	 * @return the DRL rules as a string.
	 */
	String fetchDrlRulesFromKnowledgeBase(String kbName);

	/**
	 * Fetches DRL rules from the default Knowledge Base.
	 * 
	 * @return the DRL rules as a string.
	 */
	String fetchDefaultDrlRulesFromKnowledgeBase();

	/**
	 * Creates a Knowledge Base from a list of UDR rule objects and saves it to
	 * the DB.
	 * 
	 * @param kbName
	 *            the name of the Knowledge Base.
	 * @param rules
	 *            the list of UDR rules.
	 * @param userId
	 *            user id
	 * @return the created Knowledge Base.
	 */
	KnowledgeBase createKnowledgeBaseFromUdrRules(String kbName,
			Collection<UdrRule> rules, String userId);

	/**
	 * Creates a Knowledge Base from a list of watch list item objects and saves
	 * it to the DB.
	 * 
	 * @param kbName
	 *            the name of the Knowledge Base.
	 * @param rules
	 *            the collection of watch list items.
	 * @return the created Knowledge Base.
	 */
	KnowledgeBase createKnowledgeBaseFromWatchlistItems(String kbName,
			Iterable<WatchlistItem> rules);

	/**
	 * Deletes the named Knowledge Base.
	 * 
	 * @param kbName
	 *            the knowledge base name.
	 * @return the deleted knowledge base, or null if the knowledge base could
	 *         not be found.
	 */
	KnowledgeBase deleteKnowledgeBase(String kbName);
}
