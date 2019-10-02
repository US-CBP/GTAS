/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Pnr;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TargetingServiceUtils {

	private static final Logger logger = LoggerFactory.getLogger(TargetingServiceUtils.class);

	public TargetingServiceUtils() {
	}

	/**
	 * Creates a request from a API message.
	 * 
	 * @param req
	 *            the API message.
	 * @return RuleServiceRequest object.
	 */
	public static RuleExecutionContext createApisRequest(final ApisMessage req) {
		Collection<ApisMessage> apisMessages = new LinkedList<ApisMessage>();
		apisMessages.add(req);
		return null;// createPnrApisRequestContext(apisMessages, null);
	}

	/**
	 * Creates a request from a PNR message.
	 * 
	 * @param req
	 *            the PNR message.
	 * @return RuleServiceRequest object.
	 */
	public static RuleExecutionContext createPnrRequestContext(final Pnr req) {
		Collection<Pnr> pnrs = new LinkedList<Pnr>();
		pnrs.add(req);
		return null;// createPnrApisRequestContext(null, pnrs);
	}

}
