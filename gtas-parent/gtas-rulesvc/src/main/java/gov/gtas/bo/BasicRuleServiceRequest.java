/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 * The Class BasicRuleServiceRequest.
 */
public class BasicRuleServiceRequest implements RuleServiceRequest, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8527612411998852833L;

	/** The request objects. */
	private Collection<Object> requestObjects;

	/** The request type. */
	private RuleServiceRequestType requestType;

	/**
	 * Constructs an object using defaults.
	 */
	public BasicRuleServiceRequest() {
		this.requestObjects = new LinkedList<>();
		this.requestType = RuleServiceRequestType.ANY_MESSAGE;
	}

	/**
	 * Constructs a request object using provided parameters.
	 * 
	 * @param requestColl
	 *            the collection of request objects.
	 * @param type
	 *            the type of request.
	 */
	public BasicRuleServiceRequest(final Collection<Object> requestColl, final RuleServiceRequestType type) {
		this.requestObjects = requestColl;
		this.requestType = type;
	}

	/**
	 * Adds a request object to this request.
	 * 
	 * @param reqObj
	 *            the request object to add.
	 */
	public void addRequestObject(final Object reqObj) {
		this.requestObjects.add(reqObj);
	}

	/**
	 * Adds a collection of request objects to this request.
	 * 
	 * @param reqObjects
	 *            the collection of request objects to add.
	 */
	public void addRequestObjects(final Collection<Object> reqObjects) {
		this.requestObjects.add(reqObjects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.bo.RuleServiceRequest#getRequestObjects()
	 */
	@Override
	public Collection<?> getRequestObjects() {
		return requestObjects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.bo.RuleServiceRequest#getRequestType()
	 */
	@Override
	public RuleServiceRequestType getRequestType() {
		return requestType;
	}

}
