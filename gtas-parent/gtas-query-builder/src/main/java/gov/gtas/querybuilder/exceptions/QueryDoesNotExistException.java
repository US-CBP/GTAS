/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.exceptions;

import gov.gtas.querybuilder.model.UserQueryRequest;

public class QueryDoesNotExistException extends QueryBuilderException {

	private static final long serialVersionUID = 1L;
	private UserQueryRequest queryRequest;

	public QueryDoesNotExistException(String message, UserQueryRequest queryRequest) {
		super(message, queryRequest);
		this.queryRequest = queryRequest;
	}

	public UserQueryRequest getQueryRequest() {
		return queryRequest;
	}

	public void setQueryRequest(UserQueryRequest queryRequest) {
		this.queryRequest = queryRequest;
	}

}
