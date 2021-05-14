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
import gov.gtas.json.BasicApiResponse;
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
import gov.gtas.services.dto.DocumentWatchlistItemDto;
import gov.gtas.services.dto.PassengerWatchlistItemDto;
import gov.gtas.services.dto.WLRequest;
import gov.gtas.services.security.UserService;
import gov.gtas.svc.RuleManagementService;
import gov.gtas.svc.WatchlistService;
import gov.gtas.util.SampleDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	 * @param entity the entity
	 * @param name   the name
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
		hitCategory.setPromoteToLookout(wlCategory.isPromoteToLookout());
		hitCategoryService.create(hitCategory);

		// This is to make sure that manual hit generation functions on new category
		// additions
		pendingHitDetailsService.createManualHitMaker(hitCategory.getDescription(),
				userService.fetchUser(GtasSecurityUtils.fetchLoggedInUserId()), hitCategory.getId());

	}

	@RequestMapping(method = RequestMethod.DELETE, value =Constants.WL_CATEGORY_DELETEBYID)
	public JsonServiceResponse deleteWatchlistCategoryById(@PathVariable("id") Long id){
		return watchlistService.deleteWatchlistCategory(id);
	}

	@RequestMapping(method = RequestMethod.PUT, value=Constants.WL_ADD_WL_CAT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse editWatchlistCategoryById(@RequestBody JsonLookupData wlCategory){
		HitCategory tmpHc = hitCategoryService.findById(wlCategory.getId());
		if(tmpHc != null){
			Optional<HitSeverityEnum> tmpHitEnum = HitSeverityEnum.fromString(wlCategory.getSeverity());
			if(tmpHitEnum.isPresent()){
				tmpHc.setSeverity(tmpHitEnum.get());
			}
			tmpHc.setDescription(wlCategory.getDescription());
			tmpHc.setName(wlCategory.getLabel());
			tmpHc.setPromoteToLookout(wlCategory.isPromoteToLookout());
			return hitCategoryService.updateHitCategory(tmpHc);
		}
		return new JsonServiceResponse(Status.FAILURE, "Invalid hit category update", wlCategory);
	}

	/**
	 * Creates the watchlist.
	 *
	 * @param entity    the entity
	 * @param inputSpec the input spec
	 * @return the json service response
	 */
	@Deprecated
	@PostMapping(value = Constants.WL_CREATE_UPDATE_ITEMS, produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse createWatchlist(@PathVariable String entity, @RequestBody WatchlistSpec inputSpec) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.info("******** Received Watchlist Create request by user: {}", userId);

		return createUpdateWatchlist(inputSpec);
	}

	@PostMapping(value = "/wl/passenger", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResponseEntity<?> createPaxWatchlist(@RequestBody WLRequest<PassengerWatchlistItemDto> request) {

		return addUpdatePaxWatchlist(request);
	}

	@PostMapping(value = "/wl/document", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResponseEntity<?> createDocWatchlist(@RequestBody WLRequest<DocumentWatchlistItemDto> request) {

		return addUpdateDocWatchlist(request);
	}

	@PutMapping(value = "/wl/passenger", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResponseEntity<?> updatePaxWatchlist(@RequestBody WLRequest<PassengerWatchlistItemDto> request) {

		return addUpdatePaxWatchlist(request);
	}

	@PutMapping(value = "/wl/document", produces = MediaType.APPLICATION_JSON_VALUE, consumes = "application/json")
	public ResponseEntity<?> updateDocWatchlist(@RequestBody WLRequest<DocumentWatchlistItemDto> request) {

		return addUpdateDocWatchlist(request);
	}

	public ResponseEntity<?> addUpdatePaxWatchlist(@RequestBody WLRequest<PassengerWatchlistItemDto> request) {
		List<PassengerWatchlistItemDto> wlItems = request.getWlItems();
		List<JsonServiceResponse> results = new ArrayList<JsonServiceResponse>();

		for (PassengerWatchlistItemDto wlitem : wlItems) {
			WatchlistSpec wlSpec = new WatchlistSpec("Passenger", "PASSENGER");
			List<WatchlistTerm> terms = new ArrayList<WatchlistTerm>();
			terms.add(new WatchlistTerm("firstName", "string", wlitem.getFirstName()));
			terms.add(new WatchlistTerm("lastName", "string", wlitem.getLastName()));
			terms.add(new WatchlistTerm("dob", "date", wlitem.getDob()));
			Long categoryId = Long.parseLong(wlitem.getCategoryId());

			WatchlistItemSpec wlItemSpec = new WatchlistItemSpec(request.getId(), request.getAction(),
					terms.toArray(new WatchlistTerm[0]));

			wlSpec.getWatchlistItems().add(wlItemSpec);
			JsonServiceResponse result = createUpdateWatchlist(wlSpec, categoryId);
			results.add(result);
		}

		return setRecompileFlagAndReturnResult(results);
	}

	private ResponseEntity<?> addUpdateDocWatchlist(WLRequest<DocumentWatchlistItemDto> request) {
		List<DocumentWatchlistItemDto> wlItems = request.getWlItems();
		List<JsonServiceResponse> results = new ArrayList<JsonServiceResponse>();

		for (DocumentWatchlistItemDto wlitem : wlItems) {
			WatchlistSpec wlSpec = new WatchlistSpec("Document", "DOCUMENT");
			List<WatchlistTerm> terms = new ArrayList<WatchlistTerm>();
			terms.add(new WatchlistTerm("documentType", "string", wlitem.getDocumentType()));
			terms.add(new WatchlistTerm("documentNumber", "string", wlitem.getDocumentNumber()));

			Long categoryId = Long.parseLong(wlitem.getCategoryId());

			WatchlistItemSpec wlItemSpec = new WatchlistItemSpec(request.getId(), request.getAction(),
					terms.toArray(new WatchlistTerm[0]));

			wlSpec.getWatchlistItems().add(wlItemSpec);

			JsonServiceResponse result = createUpdateWatchlist(wlSpec, categoryId);
			results.add(result);
		}
		return setRecompileFlagAndReturnResult(results);

	}

	private JsonServiceResponse createUpdateWatchlist(WatchlistSpec wlSpec, Long categoryId) {
		validateInput(wlSpec);
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		JsonServiceResponse result = watchlistService.createUpdateDeleteWatchlistItems(userId, wlSpec, categoryId);
		if (categoryId > 0) {
			List<Long> ids = (List<Long>) result.getResult();
			watchlistService.updateWatchlistItemCategory(categoryId, ids.get(0));
		}

		return result;
	}

	private ResponseEntity<?> setRecompileFlagAndReturnResult(List<JsonServiceResponse> results) {
		appConfigurationService.setRecompileFlag();

		List<JsonServiceResponse> responsesWithError = results.stream().filter(res -> res.getStatus() == Status.FAILURE)
				.collect(Collectors.toList());

		if (responsesWithError.isEmpty()) {
			return ResponseEntity.ok(new BasicApiResponse(Status.SUCCESS, "Watchlist item added"));
		}

		else {
			return ResponseEntity.ok(new BasicApiResponse(Status.FAILURE, responsesWithError.get(0).getMessage()));
		}

	}

	/**
	 * Updates the watchlist.
	 *
	 * @param entity    the entity
	 * @param inputSpec the input spec
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

	@RequestMapping(value = Constants.WL_CATEGORY_GETALLNONARCHIVED, method = RequestMethod.GET)
	@ResponseBody
	public List<JsonLookupData> getAllNonArchivedWatchlistCategories() {
		List<JsonLookupData> result = hitCategoryService.getAllNonArchivedCategories();
		return result;
	}

	@RequestMapping(value = Constants.WL_DELETE_ITEMS, method = RequestMethod.DELETE)
	public void deleteWatchlistItems(@PathVariable List<Long> watchlistItemIds) {
		watchlistService.deleteWatchlistItems(watchlistItemIds);
	}

	/**
	 * Creates the DRL rule response JSON object.
	 *
	 * @param rules the DRL rules.
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

	@Deprecated
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
		return results.stream().reduce(results.get(0),
				(a, b) -> new JsonServiceResponse(a.getStatus(), a.getMessage(), merge(a.getResult(), b.getResult())));
	}

}
