/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import java.util.Collection;

/**
 * An interface for input requests to the Rule Engine.
 */
public interface RuleServiceRequest {
	/**
	 * Gets objects to be inserted into the working memory before the rule engine is
	 * executed.
	 * 
	 * @return list of objects to be inserted into the working memory.
	 */
	Collection<?> getRequestObjects();

	/**
	 * Gets the type of the request.<br>
	 * (e.g., APIS_MESSAGE)
	 * 
	 * @return request type.
	 */
	RuleServiceRequestType getRequestType();
}
