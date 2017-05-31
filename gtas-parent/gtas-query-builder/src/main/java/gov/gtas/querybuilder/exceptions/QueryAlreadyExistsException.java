/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.exceptions;

import gov.gtas.querybuilder.model.UserQueryRequest;

public class QueryAlreadyExistsException extends QueryBuilderException {

    private static final long serialVersionUID = 1L;
    private UserQueryRequest queryRequest;
        
    public QueryAlreadyExistsException(String message, UserQueryRequest queryRequest) {
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
