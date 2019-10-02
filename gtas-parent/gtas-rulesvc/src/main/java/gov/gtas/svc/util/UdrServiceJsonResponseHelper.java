/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.constant.RuleConstants;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.udr.UdrRule;

/**
 * Helper class for the UDR service response generation.
 */
public class UdrServiceJsonResponseHelper {
	public static JsonServiceResponse createResponse(boolean success, String op, UdrRule rule) {
		return createResponse(success, op, rule, null);
	}

	public static JsonServiceResponse createResponse(boolean success, String op, UdrRule rule, String failureReason) {
		JsonServiceResponse resp = null;
		if (success) {
			resp = new JsonServiceResponse(Status.SUCCESS, String.format(
					op + " on UDR Rule with title='%s' and ID='%s' was successful.", rule.getTitle(), rule.getId()));
			resp.setResult(rule.getId());
			resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(
					RuleConstants.UDR_ID_ATTRIBUTE_NAME, String.valueOf(rule.getId())));
			resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(
					RuleConstants.UDR_TITLE_ATTRIBUTE_NAME, String.valueOf(rule.getTitle())));
		} else {
			if (rule != null) {
				resp = new JsonServiceResponse(Status.FAILURE, String.format(
						op + " on UDR Rule with title='%s' and ID='%s' failed.", rule.getTitle(), rule.getId()));
				resp.setResult(rule.getId());
			} else {
				String msg = null;
				if (StringUtils.isEmpty(failureReason)) {
					msg = op + " failed.";
				} else {
					msg = op + " failed " + failureReason + ".";
				}
				resp = new JsonServiceResponse(Status.FAILURE, msg);
			}

		}
		return resp;
	}
}
