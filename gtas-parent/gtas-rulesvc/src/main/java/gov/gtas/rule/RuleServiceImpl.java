/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule;

import static gov.gtas.rule.listener.RuleEventListenerUtils.createEventListeners;
import gov.gtas.bo.BasicRuleServiceResult;
import gov.gtas.bo.RuleExecutionStatistics;
import gov.gtas.model.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.RuleServiceConstants;
import gov.gtas.error.ErrorHandler;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.error.RuleServiceErrorHandler;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.rule.listener.RuleEventListenerUtils;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.UdrService;

import java.io.IOException;
import java.util.*;

import javax.annotation.PostConstruct;

import org.kie.api.KieBase;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the Rule Engine Service.
 */
@Service
public class RuleServiceImpl implements RuleService {

	private static final Logger logger = LoggerFactory.getLogger(RuleServiceImpl.class);

	@Autowired
	private RulePersistenceService rulePersistenceService;

	@Autowired
	private UdrService udrService;

	@Autowired
	private WatchlistItemRepository watchlistItemRepository;

	/**
	 * Initialize error handling.
	 */
	@PostConstruct
	public void initializeErrorHandling() {
		ErrorHandler errorHandler = new RuleServiceErrorHandler();
		ErrorHandlerFactory.registerErrorHandler(errorHandler);
	}

	@Override
	public RuleServiceResult invokeAdhocRules(String rulesFilePath, RuleServiceRequest req) {
		if (null == req) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"RuleServiceRequest", "RuleServiceImpl.invokeRuleset()");
		}
		KieBase kbase = null;
		try {
			kbase = RuleUtils.createKieBaseFromClasspathFile(rulesFilePath);
		} catch (IOException ioe) {
			throw ErrorHandlerFactory.getErrorHandler().createException(RuleServiceConstants.KB_CREATION_IO_ERROR_CODE,
					ioe, "RuleServiceImpl.invokeAdhocRules() with file:" + rulesFilePath);
		}
		return createSessionAndExecuteRules(kbase, req);
	}

	@Override
	public RuleServiceResult invokeRuleEngine(RuleServiceRequest req, String kbName, Map<String, KIEAndLastUpdate> rules) {
		RuleServiceResult ruleServiceResult = null;
		if (rules.containsKey(kbName)) {
			KieBase kbase = rules.get(kbName).getKieBase();
			ruleServiceResult = createSessionAndExecuteRules(kbase, req);
		}
		return ruleServiceResult;
	}
	/**
	 * Creates a session from a KieBase, loads the request objects and fires all
	 * rules.
	 * 
	 * @param kbase
	 *            the KIE knowledge base containing the rules.
	 * @param req
	 *            the request object container.
	 * @return the rule execution result.
	 */
	private RuleServiceResult createSessionAndExecuteRules(KieBase kbase, RuleServiceRequest req) {
		logger.info("Entering createSessionAndExecuteRules() and creating Stateless session.");

		StatelessKieSession ksession = kbase.newStatelessKieSession();
		ksession.setGlobal(RuleServiceConstants.RULE_RESULT_LIST_NAME, new ArrayList<Object>());
		/*
		 * object where execution statistics are collected.
		 */
		final RuleExecutionStatistics stats = new RuleExecutionStatistics();

		List<EventListener> listeners = createEventListeners(stats);
		RuleEventListenerUtils.addEventListenersToKieSEssion(ksession, listeners);
		// ksession.addEventListener(new DebugAgendaEventListener());
		// ksession.addEventListener(new DebugRuleRuntimeEventListener());
		logger.debug("About to run rules.");
		Collection<?> requestObjects = req.getRequestObjects();
		ksession.execute(requestObjects);
		logger.debug("ran rules against kie (knowledge is everything)");
		Globals globals = ksession.getGlobals();
		Collection<String> keys = globals.getGlobalKeys();

		Iterator<String> iter = keys.iterator();
		RuleServiceResult res = null;

		logger.debug("Retrieved Rule execution statistics objects.");
		while (iter.hasNext()) {
			if (iter.next().equals(RuleServiceConstants.RULE_RESULT_LIST_NAME)) {
				@SuppressWarnings("unchecked")
				List<RuleHitDetail> resList = (List<RuleHitDetail>) globals
						.get(RuleServiceConstants.RULE_RESULT_LIST_NAME);
				res = new BasicRuleServiceResult(resList, stats);
			}
		}
		logger.info("Retrieved Rule execution result objects and exit createSessionAndExecuteRules().");
		return res;
	}
}
