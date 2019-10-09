/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.json;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * JSON object class to convey generic requests.
 */
public class JsonGenericRequest implements Serializable {
	private static final long serialVersionUID = -4390502518498037033L;

	private String request;
	private List<ServiceRequestParameter> parameters;

	public JsonGenericRequest(final String req) {
		this.request = req;
		parameters = new LinkedList<JsonGenericRequest.ServiceRequestParameter>();
	}

	public void addParameter(final String pname, final String pvalue) {
		parameters.add(new ServiceRequestParameter(pname, pvalue));
	}

	/**
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * @return the parameters
	 */
	public List<ServiceRequestParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	public static class ServiceRequestParameter {
		private String name;
		private String value;

		public ServiceRequestParameter(String paramName, String paramValue) {
			name = paramName;
			value = paramValue;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

	}

}
