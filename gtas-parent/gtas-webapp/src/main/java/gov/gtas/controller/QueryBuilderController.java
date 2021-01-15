/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.constants.Constants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.querybuilder.exceptions.InvalidQueryException;
import gov.gtas.querybuilder.exceptions.QueryAlreadyExistsException;
import gov.gtas.querybuilder.exceptions.QueryBuilderException;
import gov.gtas.querybuilder.exceptions.QueryDoesNotExistException;
import gov.gtas.querybuilder.mappings.QueryBuilderMapping;
import gov.gtas.querybuilder.mappings.QueryBuilderMappingFactory;
import gov.gtas.querybuilder.model.IUserQueryResult;
import gov.gtas.querybuilder.model.QueryRequest;
import gov.gtas.querybuilder.model.UserQueryRequest;
import gov.gtas.querybuilder.service.QueryBuilderService;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.dto.FlightsPageDto;
import gov.gtas.services.dto.PassengersPageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Query Builder
 */

@RestController
@RequestMapping(Constants.QUERY_SERVICE)
public class QueryBuilderController {
	private static final Logger logger = LoggerFactory.getLogger(QueryBuilderController.class);

	final
	QueryBuilderService queryService;

	final AppConfigurationService appConfigurationService;

	public QueryBuilderController(QueryBuilderService queryService, AppConfigurationService appConfigurationService) {
		this.queryService = queryService;
		this.appConfigurationService = appConfigurationService;
	}

	/**
	 * This method generates the Entity and Field mappings for the Rule and Query UI
	 * 
	 * @return
	 */
	@RequestMapping(value = Constants.INIT, method = RequestMethod.GET)
	public Map<String, QueryBuilderMapping> initQueryBuilder() {

		logger.debug("Getting query builder UI mappings");
		return getQueryBuilderMapping();
	}

	/**
	 * This method makes a call to the method in the service layer to execute the
	 * user defined query against Flight data
	 * 
	 * @param queryRequest
	 * @return
	 * @throws InvalidQueryException
	 */
	@RequestMapping(value = Constants.RUN_QUERY_FLIGHT_URI, method = RequestMethod.POST)
	public JsonServiceResponse runFlightQuery(@RequestBody QueryRequest queryRequest) throws InvalidQueryException {

		logger.info("Executing query against flight");
		FlightsPageDto flights = queryService.runFlightQuery(queryRequest);
		return new JsonServiceResponse(Status.SUCCESS, flights.getTotalFlights() + " record(s)", flights);
	}

	/**
	 * This method makes a call to the method in the service layer to execute the
	 * user defined query against Passenger data
	 * 
	 * @param queryRequest
	 * @return
	 * @throws InvalidQueryException
	 */
	@RequestMapping(value = Constants.RUN_QUERY_PASSENGER_URI, method = RequestMethod.POST)
	public JsonServiceResponse runPassengerQuery(@RequestBody QueryRequest queryRequest) throws InvalidQueryException {

		logger.info("Executing query against passenger");
		PassengersPageDto passengers = queryService.runPassengerQuery(queryRequest);
		return new JsonServiceResponse(Status.SUCCESS, passengers.getTotalPassengers() + " record(s)", passengers);
	}

	/**
	 * This method makes a call to the method in the service layer to save the user
	 * defined query
	 * 
	 * @param queryRequest
	 * @return
	 * @throws InvalidQueryException
	 * @throws QueryAlreadyExistsException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public JsonServiceResponse saveQuery(@RequestBody UserQueryRequest queryRequest)
			throws InvalidQueryException, QueryAlreadyExistsException {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();

		logger.info("Create query '" + queryRequest.getTitle() + "' by " + userId);
		IUserQueryResult result = queryService.saveQuery(userId, queryRequest);
		return new JsonServiceResponse(Status.SUCCESS, Constants.QUERY_SAVED_SUCCESS_MSG, result.getId());
	}

	/**
	 * This method makes a call to the method in the service layer to update a user
	 * defined query
	 * 
	 * @param queryRequest
	 * @return
	 * @throws InvalidQueryException
	 * @throws QueryAlreadyExistsException
	 * @throws QueryDoesNotExistException
	 */
	@RequestMapping(value = Constants.PATH_VARIABLE_ID, method = RequestMethod.PUT)
	public JsonServiceResponse editQuery(@PathVariable int id, @RequestBody UserQueryRequest queryRequest)
			throws InvalidQueryException, QueryAlreadyExistsException, QueryDoesNotExistException {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();

		logger.info("Edit query '" + queryRequest.getTitle() + "' by " + userId);
		queryService.editQuery(userId, queryRequest);
		return new JsonServiceResponse(Status.SUCCESS, Constants.QUERY_EDITED_SUCCESS_MSG, null);
	}

	/**
	 * This method makes a call to the method in the service layer to list a user's
	 * query
	 * 
	 * @return
	 * @throws InvalidQueryException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public JsonServiceResponse listQueryByUser() throws InvalidQueryException {
		List<IUserQueryResult> resultList = new ArrayList<>();
		String userId = GtasSecurityUtils.fetchLoggedInUserId();

		logger.info("List query by " + userId);
		resultList = queryService.listQueryByUser(userId);
		return new JsonServiceResponse(Status.SUCCESS,
				resultList != null ? resultList.size() + " record(s)" : "resultList is null", resultList);
	}

	/**
	 * This method makes a call to the method in the service layer to delete a
	 * user's query
	 * 
	 * @param id
	 *            the id of the query to be deleted
	 * @return
	 * @throws QueryDoesNotExistException
	 */
	@RequestMapping(value = Constants.PATH_VARIABLE_ID, method = RequestMethod.DELETE)
	public JsonServiceResponse deleteQuery(@PathVariable int id)
			throws QueryDoesNotExistException, InvalidQueryException {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();

		logger.info("Delete query id: " + id + " by " + userId);
		queryService.deleteQuery(userId, id);
		return new JsonServiceResponse(Status.SUCCESS, Constants.QUERY_DELETED_SUCCESS_MSG, null);
	}

	@RequestMapping(value = Constants.APIS_ONLY_FLAG, method = RequestMethod.GET)
	@ResponseBody
	public String getApisOnlyFlagAndVersion() {
		AppConfiguration appConfigApisFlag = appConfigurationService
				.findByOption(AppConfigurationRepository.APIS_ONLY_FLAG);
		return appConfigApisFlag == null ? "FALSE" : appConfigApisFlag.getValue();
	}

	private Map<String, QueryBuilderMapping> getQueryBuilderMapping() {
		Map<String, QueryBuilderMapping> qbMap = new LinkedHashMap<>();

		qbMap.put(EntityEnum.ADDRESS.getEntityName(), getMapping(EntityEnum.ADDRESS));
		qbMap.put(EntityEnum.CREDIT_CARD.getEntityName(), getMapping(EntityEnum.CREDIT_CARD));
		qbMap.put(EntityEnum.DOCUMENT.getEntityName(), getMapping(EntityEnum.DOCUMENT));
		qbMap.put(EntityEnum.EMAIL.getEntityName(), getMapping(EntityEnum.EMAIL));
		qbMap.put(EntityEnum.FLIGHT.getEntityName(), getMapping(EntityEnum.FLIGHT));
		qbMap.put(EntityEnum.FREQUENT_FLYER.getEntityName(), getMapping(EntityEnum.FREQUENT_FLYER));
		qbMap.put(EntityEnum.HITS.getEntityName(), getMapping(EntityEnum.HITS));
		qbMap.put(EntityEnum.PASSENGER.getEntityName(), getMapping(EntityEnum.PASSENGER));
		qbMap.put(EntityEnum.PHONE.getEntityName(), getMapping(EntityEnum.PHONE));
		qbMap.put(EntityEnum.PNR.getEntityName(), getMapping(EntityEnum.PNR));
		qbMap.put(EntityEnum.TRAVEL_AGENCY.getEntityName(), getMapping(EntityEnum.TRAVEL_AGENCY));
		qbMap.put(EntityEnum.DWELL_TIME.getEntityName(), getMapping(EntityEnum.DWELL_TIME));
		return qbMap;
	}

	private QueryBuilderMapping getMapping(EntityEnum entityType) {
		QueryBuilderMappingFactory factory = new QueryBuilderMappingFactory();

		QueryBuilderMapping model = factory.getQueryBuilderMapping(entityType);
		return model;
	}

	@ExceptionHandler(QueryBuilderException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public JsonServiceResponse handleExceptions(QueryBuilderException exception) {

		logger.info("QueryBuilderException: " + exception.getMessage());

		return new JsonServiceResponse(Status.FAILURE, exception.getMessage(), exception.getObject());
	}

}
