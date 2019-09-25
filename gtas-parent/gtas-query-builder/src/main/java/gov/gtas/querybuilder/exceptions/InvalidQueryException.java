/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.exceptions;

public class InvalidQueryException extends QueryBuilderException {

	private static final long serialVersionUID = 1L;
	private Object object;

	public InvalidQueryException(String message, Object object) {
		super(message, object);
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

}
