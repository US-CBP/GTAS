/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.constants.Constants;
import gov.gtas.svc.RuleManagementService;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.util.RuleExecutionContext;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST service end-point controller Targeting Services.
 */
@RestController
public class TargetingManagementController {
	/*
	 * The logger for the TargetingManagementController
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(TargetingManagementController.class);

	@Autowired
	private TargetingService targetingService;

	@Autowired
	private RuleManagementService ruleManagementService;

	/**
	 * Gets the targeting result.
	 *
	 * @param id
	 *            the id
	 * @return the targeting result
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = Constants.TARGET_ONE_APIS_MSG, method = RequestMethod.GET)
	public List getTargetingResult(@PathVariable Long id) {
		RuleServiceResult resp = targetingService.analyzeApisMessage(id);
		return resp.getResultList();
	}

	/**
	 * Gets the targeting apis result.
	 *
	 * @return the targeting apis result
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = Constants.TARGET_ALL_APIS, method = RequestMethod.GET)
	public List getTargetingApisResult() {
		return targetingService.analyzeLoadedApisMessage();
	}

	/**
	 * Gets the targeting pnr result.
	 *
	 * @return the targeting pnr result
	 */
	@RequestMapping(value = Constants.TARGET_ALL_PNR, method = RequestMethod.GET)
	public List<RuleHitDetail> getTargetingPnrResult() {
		return targetingService.analyzeLoadedPnr();
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = Constants.TARGET_ALL_MSG, method = RequestMethod.GET)
	public Collection getTargetingResult() {
		RuleExecutionContext result = targetingService
				.analyzeLoadedMessages(true);
		logger.info("TargetingManagementController.getTargetingResult() - rules fired ="
				+ result.getRuleExecutionStatistics().getTotalRulesFired());
		return result.getTargetingResult();
	}
}
