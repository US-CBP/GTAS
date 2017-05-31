/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.exceptions;

public class QueryBuilderException extends Exception {

    private static final long serialVersionUID = 1L;
    private Object object;

    public QueryBuilderException(String message, Object object) {
        super(message);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
    
}
