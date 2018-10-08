/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.constants.Constants;
import gov.gtas.enumtype.Status;
import gov.gtas.error.CommonServiceException;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.svc.RuleManagementService;
import gov.gtas.svc.WatchlistService;
import gov.gtas.util.SampleDataGenerator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST service end-point controller for creating and managing watch lists.
 */
@RestController
public class WatchlistManagementController {
	private static final Logger logger = LoggerFactory
			.getLogger(WatchlistManagementController.class);

	@Autowired
	private WatchlistService watchlistService;

	@Autowired
	private RuleManagementService ruleManagementService;

	/**
	 * Gets the watchlist.
	 *
	 * @param entity
	 *            the entity
	 * @param name
	 *            the name
	 * @return the watchlist
	 */
	@RequestMapping(value = Constants.WL_GET_BY_NAME, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse getWatchlist(@PathVariable String entity,
			@PathVariable String name) {
		logger.debug("******** name =" + name);
		WatchlistSpec resp = watchlistService.fetchWatchlist(name);
		if (resp == null) {
			resp = new WatchlistSpec(name, entity);
		}
		return new JsonServiceResponse(Status.SUCCESS,
				"GET Watchlist By Name was successful", resp);
	}

	@RequestMapping(value = Constants.WL_GETALL, method = RequestMethod.GET)
	public List<WatchlistSpec> getWatchlist() {
		return watchlistService.fetchAllWatchlists();
	}

	@RequestMapping(value = Constants.WL_GETDRL, method = RequestMethod.GET)
	public JsonServiceResponse getDrl() {
		String rules = ruleManagementService
				.fetchDrlRulesFromKnowledgeBase(WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
		return createDrlRulesResponse(rules);
	}

	/**
	 * Creates the DRL rule response JSON object.
	 * 
	 * @param rules
	 *            the DRL rules.
	 * @return the JSON response object containing the rules.
	 */
	private JsonServiceResponse createDrlRulesResponse(String rules) {
		JsonServiceResponse resp = new JsonServiceResponse(Status.SUCCESS,
				"Drools rules fetched successfully");
		String[] lines = rules.split("\n");
		resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(
				"DRL Rules", lines));
		return resp;
	}

	/**
	 * Creates the watchlist.
	 *
	 * @param entity
	 *            the entity
	 * @param inputSpec
	 *            the input spec
	 * @return the json service response
	 */
	@RequestMapping(value = Constants.WL_CREATE_UPDATE_DELETE_ITEMS, method = {
			RequestMethod.POST, RequestMethod.PUT }, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse createWatchlist(@PathVariable String entity,
			@RequestBody WatchlistSpec inputSpec) {

		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.info("******** Received Watchlist Create/Update request by user ="
				+ userId);
		validateInput(inputSpec);
		return watchlistService.createUpdateDeleteWatchlistItems(userId, inputSpec);
	}

	private void validateInput(WatchlistSpec inputSpec) {
		List<WatchlistItemSpec> items = inputSpec != null ? inputSpec
				.getWatchlistItems() : null;
		if (inputSpec == null || CollectionUtils.isEmpty(items)) {
			throw new CommonServiceException(
					CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					String.format(
							CommonErrorConstants.NULL_ARGUMENT_ERROR_MESSAGE,
							"Create Query For Rule", "inputSpec"));
		}
	}

	/**
	 * Delete all watchlist items.
	 *
	 * @param name
	 *            the name
	 * @return the json service response
	 */
	@RequestMapping(value = Constants.WL_DELETE, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse deleteAllWatchlistItems(@PathVariable String name) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.info("******** Received Watchlist DeleteAll request for watch list ="
				+ name + " by user " + userId);
		return watchlistService.deleteWatchlist(userId,name);

	//	return watchlistService.deleteWatchlist(userId,"NONE");
	}

	/**
	 * Compile watchlists.
	 *
	 * @return the json service response
	 */
	@RequestMapping(value = Constants.WL_COMPILE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse compileWatchlists() {
		return watchlistService.activateAllWatchlists();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = Constants.WL_TEST, method = RequestMethod.GET)
	public WatchlistSpec getTestWatchlist() {
		return SampleDataGenerator.createSampleWatchlist("TestWatchlist");
	}

}
