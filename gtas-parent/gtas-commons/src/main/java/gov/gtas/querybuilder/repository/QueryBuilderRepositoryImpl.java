/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.repository;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.OperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.Flight;
import gov.gtas.model.User;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.JPQLGenerator;
import gov.gtas.querybuilder.constants.Constants;
import gov.gtas.querybuilder.exceptions.InvalidQueryRepositoryException;
import gov.gtas.querybuilder.exceptions.InvalidUserRepositoryException;
import gov.gtas.querybuilder.exceptions.QueryAlreadyExistsRepositoryException;
import gov.gtas.querybuilder.exceptions.QueryDoesNotExistRepositoryException;
import gov.gtas.querybuilder.model.QueryRequest;
import gov.gtas.querybuilder.model.UserQuery;
import gov.gtas.querybuilder.validation.util.QueryValidationUtils;
import gov.gtas.querybuilder.vo.FlightQueryVo;
import gov.gtas.querybuilder.vo.PassengerQueryVo;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.services.FlightService;
import gov.gtas.vo.passenger.FlightVo;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

@Repository
public class QueryBuilderRepositoryImpl implements QueryBuilderRepository {
	private static final Logger logger = LoggerFactory.getLogger(QueryBuilderRepository.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");
	private static final String CREATED_BY = "createdBy";
	private static final String TITLE = "title";
	private static final String USER_ID = "userId";
	private static final String PERCENT_SIGN = "%";

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private AppConfigurationRepository appConfigurationRepository;

	@Autowired
	private FlightService flightService;

	@Override
	@Transactional
	public UserQuery saveQuery(UserQuery query) throws QueryAlreadyExistsRepositoryException,
			InvalidQueryRepositoryException, InvalidUserRepositoryException {

		if (query == null) {
			throw new InvalidQueryRepositoryException(Constants.NULL_QUERY, query);
		}

		String userId = query.getCreatedBy() != null ? query.getCreatedBy().getUserId() : "null";
		if (!isValidUser(userId)) {
			throw new InvalidUserRepositoryException(Constants.INVALID_USER + " userId: " + userId);
		}

		try {
			Errors errors = QueryValidationUtils.validateQueryRequest(query);

			if (errors != null && errors.hasErrors()) {
				throw new InvalidQueryRepositoryException(QueryValidationUtils.getErrorString(errors), query);
			}
		} catch (IOException e) {
			throw new InvalidQueryRepositoryException(e.getMessage(), query);
		}

		// check whether the title is unique before saving
		// you can't have queries with duplicate titles for the same user
		if (isUniqueTitle(query)) {
			query.setId(null);
			query.setTitle(query.getTitle() != null ? query.getTitle().trim() : query.getTitle());
			query.setDescription(
					query.getDescription() != null ? query.getDescription().trim() : query.getDescription());
			query.setCreatedDt(new Date());

			// save query to database
			entityManager.persist(query);
		} else {
			throw new QueryAlreadyExistsRepositoryException(Constants.QUERY_EXISTS_ERROR_MSG, query);
		}

		return query;
	}

	@Override
	@Transactional
	public UserQuery editQuery(UserQuery query) throws QueryAlreadyExistsRepositoryException,
			QueryDoesNotExistRepositoryException, InvalidQueryRepositoryException, InvalidUserRepositoryException {
		UserQuery queryToSave = new UserQuery();
		boolean isUniqueTitle = true;

		if (query == null || query.getId() == null) {
			throw new InvalidQueryRepositoryException(Constants.NULL_QUERY, query);
		}

		String userId = query.getCreatedBy() != null ? query.getCreatedBy().getUserId() : "null";
		if (!isValidUser(userId)) {
			throw new InvalidUserRepositoryException(Constants.INVALID_USER + " userId: " + userId);
		}

		try {
			Errors errors = QueryValidationUtils.validateQueryRequest(query);

			if (errors != null && errors.hasErrors()) {
				throw new InvalidQueryRepositoryException(QueryValidationUtils.getErrorString(errors), query);
			}
		} catch (IOException e) {
			throw new InvalidQueryRepositoryException(e.getMessage(), query);
		}

		queryToSave = entityManager.find(UserQuery.class, query.getId());

		// check whether the query exists or has not been deleted
		// before updating
		if (queryToSave == null || queryToSave.getDeletedDt() != null) {
			throw new QueryDoesNotExistRepositoryException(Constants.QUERY_DOES_NOT_EXIST_ERROR_MSG, query);
		}

		// check if the query's title is unique
		if (!query.getTitle().trim().equalsIgnoreCase(queryToSave.getTitle().trim())) {
			isUniqueTitle = isUniqueTitle(query);
		}

		// update the query
		if (isUniqueTitle) {
			queryToSave.setTitle(query.getTitle() != null ? query.getTitle().trim() : query.getTitle());
			queryToSave.setDescription(
					query.getDescription() != null ? query.getDescription().trim() : query.getDescription());
			queryToSave.setQueryText(query.getQueryText());

			entityManager.flush();
		} else {
			throw new QueryAlreadyExistsRepositoryException(Constants.QUERY_EXISTS_ERROR_MSG, query);
		}

		return queryToSave;
	}

	@Override
	public List<UserQuery> listQueryByUser(String userId) throws InvalidUserRepositoryException {
		List<UserQuery> queryList = new ArrayList<>();

		if (!isValidUser(userId)) {
			throw new InvalidUserRepositoryException(Constants.INVALID_USER + " userId: " + userId);
		}

		// get all the user's queries that have not been deleted
		queryList = entityManager.createNamedQuery(Constants.LIST_QUERY, UserQuery.class)
				.setParameter(CREATED_BY, userId).getResultList();

		return queryList;
	}

	@Override
	@Transactional
	public void deleteQuery(String userId, int id)
			throws InvalidUserRepositoryException, QueryDoesNotExistRepositoryException {

		if (!isValidUser(userId)) {
			throw new InvalidUserRepositoryException(Constants.INVALID_USER + " userId: " + userId);
		}

		if (id <= 0) {
			throw new QueryDoesNotExistRepositoryException(Constants.QUERY_DOES_NOT_EXIST_ERROR_MSG + " id: " + id,
					null);
		}

		UserQuery query = entityManager.find(UserQuery.class, id);

		// check whether the query exists or has not been deleted
		// before deleting
		if (query == null || query.getDeletedDt() != null) {
			throw new QueryDoesNotExistRepositoryException(Constants.QUERY_DOES_NOT_EXIST_ERROR_MSG, null);
		}

		// delete the query
		User user = new User();
		user.setUserId(userId);

		query.setDeletedDt(new Date());
		query.setDeletedBy(user);

		entityManager.flush();
	}

	@Override
	public FlightQueryVo getFlightsByDynamicQuery(QueryRequest queryRequest) throws InvalidQueryRepositoryException {
		FlightQueryVo vo = new FlightQueryVo();

		try {
			String jpqlQuery = JPQLGenerator.generateQuery(queryRequest.getQuery(), EntityEnum.FLIGHT);
			TypedQuery<Flight> query = entityManager.createQuery(jpqlQuery, Flight.class);
			MutableInt positionalParameter = new MutableInt();
			setJPQLParameters(query, queryRequest.getQuery(), positionalParameter);
			int maxQueryResults = Integer.parseInt(appConfigurationRepository
					.findByOption(AppConfigurationRepository.MAX_FLIGHT_QUERY_RESULT).getValue());
			query.setMaxResults(maxQueryResults);
			// if page size is less than zero, return all flight result
			// if(queryRequest.getPageSize() < 0) {
			logger.info("Getting all flights with this query: " + jpqlQuery);
			List<Flight> flights = query.getResultList();
			List<FlightVo> flightVos = flightService.convertFlightToFlightVo(flights);
			vo.setFlights(flightVos);
			vo.setTotalFlights(flights.size());
			vo.setQueryLimitReached(flights.size() >= maxQueryResults);

			// } else {
			// // get total number of flights
			// logger.debug("Pagination, Getting total number of flights...");
			// vo.setTotalFlights(query.getResultList().size());
			// logger.debug("done");
			//
			// // paginate results
			// query = entityManager.createQuery(jpqlQuery, Flight.class);
			// positionalParameter = new MutableInt();
			// setJPQLParameters(query, queryRequest.getQuery(), positionalParameter);
			//
			// int pageNumber = queryRequest.getPageNumber();
			// int pageSize = queryRequest.getPageSize();
			// int firstResultIndex = (pageNumber - 1) * pageSize;
			//
			// logger.debug("Getting " + pageSize + " flights per page with this query: " +
			// jpqlQuery);
			// logger.debug("Getting " + pageSize + " flights per page");
			// query.setFirstResult(firstResultIndex);
			// query.setMaxResults(pageSize);
			// vo.setFlights(query.getResultList());
			// }

			logger.info("Total number of Flights: " + vo.getTotalFlights());
		} catch (InvalidQueryRepositoryException | ParseException e) {
			throw new InvalidQueryRepositoryException(e.getMessage(), queryRequest.getQuery());
		}

		return vo;
	}

	@Override
	public PassengerQueryVo getPassengersByDynamicQuery(QueryRequest queryRequest)
			throws InvalidQueryRepositoryException {
		PassengerQueryVo vo = new PassengerQueryVo();

		try {
			String jpqlQuery = JPQLGenerator.generateQuery(queryRequest.getQuery(), EntityEnum.PASSENGER);

			TypedQuery<Object[]> query = entityManager.createQuery(jpqlQuery, Object[].class);
			MutableInt positionalParameter = new MutableInt();
			setJPQLParameters(query, queryRequest.getQuery(), positionalParameter);

			int maxQueryResults = Integer.parseInt(appConfigurationRepository
					.findByOption(AppConfigurationRepository.MAX_PASSENGER_QUERY_RESULT).getValue());
			query.setMaxResults(maxQueryResults);

			// if(queryRequest.getPageSize() < 0) {
			logger.info("Getting all passengers with this query: " + jpqlQuery);
			List<Object[]> result = query.getResultList();
			vo.setResult(result);
			vo.setTotalPassengers(result.size());
			vo.setQueryLimitReached(result.size() >= maxQueryResults);
			// }
			// else {
			// // get total number of passengers
			// vo.setTotalPassengers(query.getResultList().size());
			//
			// // pagination
			// query = entityManager.createQuery(jpqlQuery, Object[].class);
			// positionalParameter = new MutableInt();
			// setJPQLParameters(query, queryRequest.getQuery(), positionalParameter);
			//
			// int pageNumber = queryRequest.getPageNumber();
			// int pageSize = queryRequest.getPageSize();
			// int firstResultIndex = (pageNumber - 1) * pageSize;
			//
			// logger.info("Getting " + pageSize + " passengers with this query: " +
			// jpqlQuery);
			// query.setFirstResult(firstResultIndex);
			// query.setMaxResults(pageSize);
			// vo.setResult(query.getResultList());
			// }

			logger.info("Total number of Passengers: " + vo.getTotalPassengers());
		} catch (InvalidQueryRepositoryException | ParseException e) {
			throw new InvalidQueryRepositoryException(e.getMessage(), queryRequest.getQuery());
		}

		return vo;
	}

	private void setJPQLParameters(Query query, QueryEntity queryEntity, MutableInt positionalParameter)
			throws ParseException {
		QueryObject queryObject = null;
		QueryTerm queryTerm = null;

		if (queryEntity instanceof QueryObject) {
			queryObject = (QueryObject) queryEntity;

			List<QueryEntity> rules = queryObject.getRules();

			for (QueryEntity rule : rules) {
				setJPQLParameters(query, rule, positionalParameter);
			}
		} else if (queryEntity instanceof QueryTerm) {
			queryTerm = (QueryTerm) queryEntity;

			String field = queryTerm.getField();
			String type = queryTerm.getType();
			String operator = queryTerm.getOperator();
			String value = (queryTerm.getValue() != null && queryTerm.getValue().length == 1) ? queryTerm.getValue()[0]
					: null;
			EntityEnum entityEnum = EntityEnum.getEnum(queryTerm.getEntity());
			// These four operators don't have any value ex. where firstname IS NULL
			// field isRuleHit doesn't have any value either
			if (!OperatorEnum.IS_EMPTY.toString().equalsIgnoreCase(operator)
					&& !OperatorEnum.IS_NOT_EMPTY.toString().equalsIgnoreCase(operator)
					&& !OperatorEnum.IS_NULL.toString().equalsIgnoreCase(operator)
					&& !OperatorEnum.IS_NOT_NULL.toString().equalsIgnoreCase(operator)
					&& !(entityEnum == EntityEnum.HITS && (field.equalsIgnoreCase(Constants.IS_RULE_HIT)
							|| field.equalsIgnoreCase(Constants.IS_WATCHLIST_HIT)))) {

				positionalParameter.increment();

				if (OperatorEnum.BETWEEN.toString().equalsIgnoreCase(operator)) {
					List<String> values = null;

					if (queryTerm.getValue() != null && queryTerm.getValue().length > 0) {
						values = Arrays.asList(queryTerm.getValue());
					}

					if (values != null && values.size() == 2) {

						if (TypeEnum.INTEGER.toString().equalsIgnoreCase(type)) {
							if (entityEnum == EntityEnum.HITS && field.equalsIgnoreCase(Constants.HITS_ID)) {
								query.setParameter(positionalParameter.intValue(), Long.parseLong(values.get(0)));
								positionalParameter.increment();
								query.setParameter(positionalParameter.intValue(), Long.parseLong(values.get(1)));
							} else if (entityEnum == EntityEnum.PNR && field.equalsIgnoreCase(Constants.PNR_ID)) {
								query.setParameter(positionalParameter.intValue(), Long.parseLong(values.get(0)));
								positionalParameter.increment();
								query.setParameter(positionalParameter.intValue(), Long.parseLong(values.get(1)));
							} else {
								query.setParameter(positionalParameter.intValue(), Integer.parseInt(values.get(0)));
								positionalParameter.increment();
								query.setParameter(positionalParameter.intValue(), Integer.parseInt(values.get(1)));
							}
						} else if (TypeEnum.DOUBLE.toString().equalsIgnoreCase(type)) {
							query.setParameter(positionalParameter.intValue(), Double.parseDouble(values.get(0)));
							positionalParameter.increment();
							query.setParameter(positionalParameter.intValue(), Double.parseDouble(values.get(1)));
						} else if (TypeEnum.DATE.toString().equalsIgnoreCase(type)) {
							query.setParameter(positionalParameter.intValue(), sdf.parse(values.get(0)),
									TemporalType.DATE);
							positionalParameter.increment();
							query.setParameter(positionalParameter.intValue(), sdf.parse(values.get(1)),
									TemporalType.DATE);
						} else if (TypeEnum.DATETIME.toString().equalsIgnoreCase(type)) {
							query.setParameter(positionalParameter.intValue(), dtFormat.parse(values.get(0)),
									TemporalType.DATE);
							positionalParameter.increment();
							query.setParameter(positionalParameter.intValue(), dtFormat.parse(values.get(1)),
									TemporalType.DATE);
						} else {
							query.setParameter(positionalParameter.intValue(), values.get(0));
							positionalParameter.increment();
							query.setParameter(positionalParameter.intValue(), values.get(1));
						}
					}
				} else if (OperatorEnum.IN.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_IN.toString().equalsIgnoreCase(operator)) {
					List<String> values = null;

					if (queryTerm.getValue() != null && queryTerm.getValue().length > 0) {
						values = Arrays.asList(queryTerm.getValue());
					}

					if (TypeEnum.INTEGER.toString().equalsIgnoreCase(type)) {
						if (entityEnum == EntityEnum.HITS && field.equalsIgnoreCase(Constants.HITS_ID)) {

							List<Long> vals = new ArrayList<>();
							if (values != null) {
								for (String val : values) {
									vals.add(Long.parseLong(val));
								}
							}
							query.setParameter(positionalParameter.intValue(), vals);
						} else if (entityEnum == EntityEnum.PNR && field.equalsIgnoreCase(Constants.PNR_ID)) {
							List<Long> vals = new ArrayList<>();
							if (values != null) {
								for (String val : values) {
									vals.add(Long.parseLong(val));
								}
							}
							query.setParameter(positionalParameter.intValue(), vals);
						} else {
							List<Integer> vals = new ArrayList<>();
							if (values != null) {
								for (String val : values) {
									vals.add(Integer.parseInt(val));
								}
							}
							query.setParameter(positionalParameter.intValue(), vals);
						}
					} else if (TypeEnum.DOUBLE.toString().equalsIgnoreCase(type)) {
						List<Double> vals = new ArrayList<>();
						if (values != null) {
							for (String val : values) {
								vals.add(Double.parseDouble(val));
							}
						}
						query.setParameter(positionalParameter.intValue(), vals);
					} else if (TypeEnum.DATE.toString().equalsIgnoreCase(type)) {
						List<Date> vals = new ArrayList<>();
						if (values != null) {
							for (String val : values) {
								vals.add(sdf.parse(val));
							}
						}
						query.setParameter(positionalParameter.intValue(), vals);
					} else if (TypeEnum.DATETIME.toString().equalsIgnoreCase(type)) {
						List<Date> vals = new ArrayList<>();
						if (values != null) {
							for (String val : values) {
								vals.add(dtFormat.parse(val));
							}
						}
						query.setParameter(positionalParameter.intValue(), vals);
					} else if (entityEnum.toString().equalsIgnoreCase(EntityEnum.EMAIL.toString()) ||
								entityEnum.toString().equalsIgnoreCase(EntityEnum.DOCUMENT.toString()) ||
								entityEnum.toString().equalsIgnoreCase(EntityEnum.CREDIT_CARD.toString())) {
						List<String> vals = new ArrayList<>();
						if (values != null) {
							for (String val : values)
								Collections.addAll(vals, val.split(","));
						}
						query.setParameter(positionalParameter.intValue(), vals);
					} else {
						query.setParameter(positionalParameter.intValue(), values);
					}
				} else if (OperatorEnum.BEGINS_WITH.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_BEGINS_WITH.toString().equalsIgnoreCase(operator)) {
					query.setParameter(positionalParameter.intValue(), value + PERCENT_SIGN);
				} else if (OperatorEnum.CONTAINS.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_CONTAINS.toString().equalsIgnoreCase(operator)) {
					query.setParameter(positionalParameter.intValue(), PERCENT_SIGN + value + PERCENT_SIGN);
				} else if (OperatorEnum.ENDS_WITH.toString().equalsIgnoreCase(operator)
						|| OperatorEnum.NOT_ENDS_WITH.toString().equalsIgnoreCase(operator)) {
					query.setParameter(positionalParameter.intValue(), PERCENT_SIGN + value);
				} else {
					if (TypeEnum.INTEGER.toString().equalsIgnoreCase(type)) {
						if (entityEnum == EntityEnum.HITS && field.equalsIgnoreCase(Constants.HITS_ID)) {
							query.setParameter(positionalParameter.intValue(), Long.parseLong(value));
						} else if (entityEnum == EntityEnum.PNR && field.equalsIgnoreCase(Constants.PNR_ID)) {
							query.setParameter(positionalParameter.intValue(), Long.parseLong(value));
						} else {
							query.setParameter(positionalParameter.intValue(), Integer.parseInt(value));
						}
					} else if (TypeEnum.DOUBLE.toString().equalsIgnoreCase(type)) {
						query.setParameter(positionalParameter.intValue(), Double.parseDouble(value));
					} else if (TypeEnum.DATE.toString().equalsIgnoreCase(type)) {
						query.setParameter(positionalParameter.intValue(), sdf.parse(value), TemporalType.DATE);
					} else if (TypeEnum.DATETIME.toString().equalsIgnoreCase(type)) {
						query.setParameter(positionalParameter.intValue(), dtFormat.parse(value), TemporalType.DATE);
					} else if (TypeEnum.BOOLEAN.toString().equalsIgnoreCase(type)) {
						// For Marketing Flights/Operating Flights
						// 0 = False 1 = True
						assert value != null;
						if (value.equals("0")) {
							query.setParameter(positionalParameter.intValue(), Boolean.FALSE);
						} else if (value.equals("1")) {
							query.setParameter(positionalParameter.intValue(), Boolean.TRUE);
						}
					} else {
						query.setParameter(positionalParameter.intValue(), value);
					}
				}
			}
		}
	}

	private boolean isUniqueTitle(UserQuery query) {

		// check uniqueness of query title for this user
		List<Integer> ids = entityManager.createNamedQuery(Constants.UNIQUE_TITLE_QUERY, Integer.class)
				.setParameter(CREATED_BY, query.getCreatedBy())
				.setParameter(TITLE, query.getTitle() != null ? query.getTitle().trim() : query.getTitle())
				.getResultList();

		if (ids == null || ids.size() == 0) {
			return true;
		}

		return false;
	}

	private boolean isValidUser(String userId) {

		// check if valid user
		Long count = entityManager.createNamedQuery(Constants.IS_VALID_USER, Long.class).setParameter(USER_ID, userId)
				.getSingleResult();

		if (count > 0) {
			return true;
		}

		return false;
	}
}
