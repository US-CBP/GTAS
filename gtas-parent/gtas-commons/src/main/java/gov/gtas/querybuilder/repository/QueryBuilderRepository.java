/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.repository;

import gov.gtas.querybuilder.exceptions.InvalidQueryRepositoryException;
import gov.gtas.querybuilder.exceptions.InvalidUserRepositoryException;
import gov.gtas.querybuilder.exceptions.QueryAlreadyExistsRepositoryException;
import gov.gtas.querybuilder.exceptions.QueryDoesNotExistRepositoryException;
import gov.gtas.querybuilder.model.QueryRequest;
import gov.gtas.querybuilder.model.UserQuery;
import gov.gtas.querybuilder.vo.FlightQueryVo;
import gov.gtas.querybuilder.vo.PassengerQueryVo;

import java.util.List;

public interface QueryBuilderRepository {
    
    public FlightQueryVo getFlightsByDynamicQuery(QueryRequest queryRequest) throws InvalidQueryRepositoryException;
    public PassengerQueryVo getPassengersByDynamicQuery(QueryRequest queryRequest) throws InvalidQueryRepositoryException;
    public UserQuery saveQuery(UserQuery query) throws QueryAlreadyExistsRepositoryException, InvalidQueryRepositoryException, InvalidUserRepositoryException;
    public UserQuery editQuery(UserQuery query) throws QueryAlreadyExistsRepositoryException, QueryDoesNotExistRepositoryException, 
        InvalidQueryRepositoryException, InvalidUserRepositoryException;
    public List<UserQuery> listQueryByUser(String userId) throws InvalidUserRepositoryException;
    public void deleteQuery(String userId, int queryId) throws InvalidUserRepositoryException, QueryDoesNotExistRepositoryException;
}
