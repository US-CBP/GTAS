/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.constants.Constants;
import gov.gtas.enumtype.HitSeverityEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.error.CommonServiceException;
import gov.gtas.json.JsonLookupData;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.HitCategoryService;
import gov.gtas.services.PendingHitDetailsService;
import gov.gtas.services.security.UserService;
import gov.gtas.svc.RuleManagementService;
import gov.gtas.svc.WatchlistService;
import gov.gtas.util.SampleDataGenerator;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The REST service end-point controller for creating and managing watch lists.
 */
@RestController
public class WatchlistManagementController {
	private static final Logger logger = LoggerFactory.getLogger(WatchlistManagementController.class);


	@Autowired
	private AppConfigurationService appConfigurationService;

	@Autowired
	private WatchlistService watchlistService;

	@Autowired
	private HitCategoryService hitCategoryService;

	@Autowired
	private RuleManagementService ruleManagementService;

	@Autowired
	private PendingHitDetailsService pendingHitDetailsService;

	@Autowired
	UserService userService;

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
	public JsonServiceResponse getWatchlist(@PathVariable String entity, @PathVariable String name) {
		logger.debug("******** name =" + name);

		WatchlistSpec resp = new WatchlistSpec(name, entity);

		for (WatchlistItem item : this.watchlistService.fetchItemsByWatchlistName(name)) {
			try {
				WatchlistItemSpec itemSpec = new ObjectMapper().readValue(item.getItemData(), WatchlistItemSpec.class);
				itemSpec.setId(item.getId());

				WatchlistTerm[] items = new WatchlistTerm[itemSpec.getTerms().length + 1];
				int i = 0;
				for (; i < itemSpec.getTerms().length; i++) {
					items[i] = itemSpec.getTerms()[i];
				}
				items[i] = new WatchlistTerm("categoryId", "int", item.getHitCategory().getId().toString());
				itemSpec.setTerms(items);
				resp.addWatchlistItem(itemSpec);

			} catch (JsonParseException e) {
				logger.error("caught JsonParseException");
			} catch (JsonMappingException e) {
				logger.error("caught JsonMappingException");
			} catch (IOException e) {
				logger.error("caught IOException");
			}
		}

		return new JsonServiceResponse(Status.SUCCESS, "GET Watchlist By Name was successful", resp);
	}

	@RequestMapping(value = Constants.WL_GETALL, method = RequestMethod.GET)
	public List<WatchlistSpec> getWatchlist() {
		return watchlistService.fetchAllWatchlists();
	}

	@RequestMapping(value = Constants.WL_GETDRL, method = RequestMethod.GET)
	public JsonServiceResponse getDrl() {
		String rules = ruleManagementService.fetchDrlRulesFromKnowledgeBase(WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
		return createDrlRulesResponse(rules);
	}

	@RequestMapping(value = Constants.WL_ADD_WL_CAT, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createWatchlistCategory(@RequestBody JsonLookupData wlCategory) {
		HitCategory hitCategory = new HitCategory();
		hitCategory.setDescription(wlCategory.getDescription());
		hitCategory.setName(wlCategory.getLabel());
		HitSeverityEnum hitSeverityEnum = HitSeverityEnum.fromString(wlCategory.getSeverity())
				.orElseThrow(RuntimeException::new);
		hitCategory.setSeverity(hitSeverityEnum);
		hitCategoryService.create(hitCategory);

		//This is to make sure that manual hit generation functions on new category additions
		pendingHitDetailsService.createManualHitMaker(hitCategory.getDescription(), userService.fetchUser(GtasSecurityUtils.fetchLoggedInUserId()), hitCategory.getId());

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
	@PostMapping(value = Constants.WL_CREATE_UPDATE_ITEMS, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse createWatchlist(@PathVariable String entity, @RequestBody WatchlistSpec inputSpec) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.info("******** Received Watchlist Create request by user: {}", userId);

		return createUpdateWatchlist(inputSpec);
	}

	/**
	 * Updates the watchlist.
	 *
	 * @param entity
	 *            the entity
	 * @param inputSpec
	 *            the input spec
	 * @return the json service response
	 */
	@PutMapping(value = Constants.WL_CREATE_UPDATE_ITEMS, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse updateWatchlist(@PathVariable String entity, @RequestBody WatchlistSpec inputSpec) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.info("******** Received Watchlist Update request by user: {}", userId);

		return createUpdateWatchlist(inputSpec);
	}

	/**
	 * Compile watchlists.
	 *
	 * @return the json service response
	 */
	@RequestMapping(value = Constants.WL_COMPILE, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse compileWatchlists() {
		appConfigurationService.setRecompileFlag();
		return new JsonServiceResponse(Status.SUCCESS, "Set rule engine to recompile watchlist!");
	}

	/**
	 * @return
	 */
	@RequestMapping(value = Constants.WL_TEST, method = RequestMethod.GET)
	public WatchlistSpec getTestWatchlist() {
		return SampleDataGenerator.createSampleWatchlist("TestWatchlist");
	}

	@RequestMapping(value = Constants.WL_CATEGORY_GETALL, method = RequestMethod.GET)
	@ResponseBody
	public List<JsonLookupData> getWatchlistCategories() {

		List<JsonLookupData> result = watchlistService.findWatchlistCategories();
		return result;
	}
	
	@RequestMapping(value = Constants.WL_DELETE_ITEMS, method = RequestMethod.DELETE)
	public void deleteWatchlistItems(@PathVariable List<Long> watchlistItemIds){	
		watchlistService.deleteWatchlistItems(watchlistItemIds);
	}

	/**
	 * Creates the DRL rule response JSON object.
	 *
	 * @param rules
	 *            the DRL rules.
	 * @return the JSON response object containing the rules.
	 */
	private JsonServiceResponse createDrlRulesResponse(String rules) {
		JsonServiceResponse resp = new JsonServiceResponse(Status.SUCCESS, "Drools rules fetched successfully");
		String[] lines = rules.split("\n");
		resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute("DRL Rules", lines));
		return resp;
	}

	private Object merge(Object a, Object b) {
		if (a == null)
			return merge(new ArrayList<>(), b);
		if (b == null)
			return merge(a, new ArrayList<>());
		List<Long> _a = (List<Long>) a;
		List<Long> _b = (List<Long>) b;
		_a.addAll(_b);
		return _a;
	}

	private void validateInput(WatchlistSpec inputSpec) {
		List<WatchlistItemSpec> items = inputSpec != null ? inputSpec.getWatchlistItems() : null;
		if (inputSpec == null || CollectionUtils.isEmpty(items)) {
			throw new CommonServiceException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE, String
					.format(CommonErrorConstants.NULL_ARGUMENT_ERROR_MESSAGE, "Create Query For Rule", "inputSpec"));
		}
	}

	private JsonServiceResponse createUpdateWatchlist(WatchlistSpec inputSpec) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.info("******** Received Watchlist Create/Update request by user =" + userId);

		List<WatchlistTerm> _terms = new ArrayList<WatchlistTerm>();

		List<JsonServiceResponse> results = new ArrayList<JsonServiceResponse>();
		long categoryId = 0L;
		for (WatchlistItemSpec spec : inputSpec.getWatchlistItems()) {
			if (spec.getTerms() != null) {
				_terms = new ArrayList<>();
				for (int i = 0; i < spec.getTerms().length; i++) {
					if (!spec.getTerms()[i].getField().equals("categoryId"))
						_terms.add(spec.getTerms()[i]);
					else
						categoryId = Long.parseLong(spec.getTerms()[i].getValue());
				}
				spec.setTerms((_terms.toArray(new WatchlistTerm[1])));

				WatchlistSpec _spec = new WatchlistSpec(inputSpec.getName(), inputSpec.getEntity());

				_spec.addWatchlistItem(spec);

				validateInput(_spec);

				JsonServiceResponse result = watchlistService.createUpdateDeleteWatchlistItems(userId, _spec,
						categoryId);

				if (categoryId > 0) {
					List<Long> ids = (List<Long>) result.getResult();
					watchlistService.updateWatchlistItemCategory(categoryId, ids.get(0));
				}

				results.add(result);
			}
		}

		appConfigurationService.setRecompileFlag();
		return results.stream()
				.reduce(results.get(0),
				(a, b) -> new JsonServiceResponse(a.getStatus(), a.getMessage(), merge(a.getResult(), b.getResult())));
	}

}
