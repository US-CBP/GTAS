/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.RuleConstants;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.rule.RuleUtils;
import gov.gtas.rule.builder.DrlRuleFileBuilder;
import gov.gtas.services.security.UserService;
import gov.gtas.services.udr.RulePersistenceService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


import org.apache.commons.lang3.StringUtils;
import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of the Knowledge Base and Rule management service interface.
 */
@Service
public class RuleManagementServiceImpl implements RuleManagementService {
	private static final Logger logger = LoggerFactory
			.getLogger(RuleManagementServiceImpl.class);

	@Autowired
	private RulePersistenceService rulePersistenceService;

	@Autowired
	UserService userService;

	@Override
	public KnowledgeBase createKnowledgeBaseFromDRLString(String kbName,
			String drlString) {
		try {
			KieBase kieBase = RuleUtils.createKieBaseFromDrlString(drlString);
			byte[] kbBlob = RuleUtils.convertKieBaseToBytes(kieBase);
			logger.debug("Size of the compiled Knowledge Base = "
					+ kbBlob.length);
			KnowledgeBase kb = rulePersistenceService
					.findUdrKnowledgeBase(kbName);
			if (kb == null) {
				kb = new KnowledgeBase(kbName);
			}
			kb.setRulesBlob(drlString
					.getBytes(RuleConstants.UDR_EXTERNAL_CHARACTER_ENCODING));
			kb.setKbBlob(kbBlob);
			if (StringUtils.isEmpty(kbName)) {
				kb.setKbName(RuleConstants.UDR_KNOWLEDGE_BASE_NAME);
			}
			kb = rulePersistenceService.saveKnowledgeBase(kb);
			return kb;
		} catch (Exception ioe) {
			logger.error(ioe.getMessage());
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.SYSTEM_ERROR_CODE,
					System.currentTimeMillis(), ioe);
		}
	}

	@Override
	public String fetchDrlRulesFromKnowledgeBase(String kbName) {
		KnowledgeBase kb = rulePersistenceService.findUdrKnowledgeBase(kbName);
		if (kb == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					RuleServiceConstants.KB_NOT_FOUND_ERROR_CODE, kbName);
		}
		String drlRules = null;
		try {
			drlRules = new String(kb.getRulesBlob(),
					RuleConstants.UDR_EXTERNAL_CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException uee) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					RuleServiceConstants.KB_INVALID_ERROR_CODE,
					RuleConstants.UDR_KNOWLEDGE_BASE_NAME, uee);
		}
		return drlRules;
	}

	@Override
	public String fetchDefaultDrlRulesFromKnowledgeBase() {
		return this
				.fetchDrlRulesFromKnowledgeBase(RuleConstants.UDR_KNOWLEDGE_BASE_NAME);
	}

	@Override
	@Transactional()
	public KnowledgeBase createKnowledgeBaseFromUdrRules(String kbName,
			Collection<UdrRule> rules, String userId) {
		if (!CollectionUtils.isEmpty(rules)) {
			DrlRuleFileBuilder ruleFileBuilder = new DrlRuleFileBuilder();
			for (UdrRule rule : rules) {
				ruleFileBuilder.addRule(rule);
			}
			String drlRules = ruleFileBuilder.build();
			KnowledgeBase kb = createKnowledgeBaseFromDRLString(kbName,
					drlRules);
			linkRulesToKnowledgeBase(kb, rules);
			return kb;
		} else {
			return null;
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public KnowledgeBase createKnowledgeBaseFromWatchlistItems(String kbName,
			Iterable<WatchlistItem> rules) {
		if (rules != null) {
			DrlRuleFileBuilder ruleFileBuilder = new DrlRuleFileBuilder();
			for (WatchlistItem rule : rules) {
				ruleFileBuilder.addWatchlistItemRule(rule);
			}
			String drlRules = ruleFileBuilder.build();
			return createKnowledgeBaseFromDRLString(kbName, drlRules);
		} else {
			return null;
		}
	}

	private void linkRulesToKnowledgeBase(KnowledgeBase kb,
			Collection<UdrRule> rules) {
		if (kb != null && kb.getId() != null) {
			List<Rule> ruleList = new LinkedList<>();
			for (UdrRule rule : rules) {
				for (Rule engineRule : rule.getEngineRules()) {
					engineRule.setKnowledgeBase(kb);
				}
				ruleList.addAll(rule.getEngineRules());
			}
			rulePersistenceService.batchUpdate(ruleList);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public KnowledgeBase deleteKnowledgeBase(String kbName) {
		KnowledgeBase kb = rulePersistenceService.findUdrKnowledgeBase(kbName);
		if (kb != null) {
			List<Rule> ruleList = rulePersistenceService
					.findRulesByKnowledgeBaseId(kb.getId());
			List<Rule> saveRuleList = new ArrayList<>();
			for (Rule rule : ruleList) {
				rule.setKnowledgeBase(null);
				saveRuleList.add(rule);
			}
			rulePersistenceService.batchUpdate(saveRuleList);
			kb = rulePersistenceService.deleteKnowledgeBase(kbName);
		}
		return kb;
	}

}
