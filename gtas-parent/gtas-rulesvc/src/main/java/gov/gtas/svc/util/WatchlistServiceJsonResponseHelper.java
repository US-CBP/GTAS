/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.constant.RuleConstants;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.udr.KnowledgeBase;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * Helper class for the UDR service response generation.
 */
public class WatchlistServiceJsonResponseHelper {
	public static JsonServiceResponse createResponse(boolean success, String op, Long wlId, String wlName) {
		return createResponse(success, op, wlId, wlName, null);
	}

	public static JsonServiceResponse createResponse(boolean success, String op, Long wlId, String wlName,
			String failureReason) {
		return createResponse(success, op, wlId, wlName, null, failureReason);
	}

	public static JsonServiceResponse createResponse(boolean success, String op, Long wlId, String wlName,
			List<Long> itemIds, String failureReason) {
		JsonServiceResponse resp = null;
		if (success) {
			resp = new JsonServiceResponse(Status.SUCCESS,
					String.format(op + " on Watch list with name='%s' and ID='%s' was successful.", wlName, wlId));
			resp.setResult(wlId);
			resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(
					RuleConstants.WL_ID_ATTRIBUTE_NAME, String.valueOf(wlId)));
			resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(
					RuleConstants.WL_TITLE_ATTRIBUTE_NAME, String.valueOf(wlName)));
			if (!CollectionUtils.isEmpty(itemIds)) {
				resp.setResult(itemIds);
				resp.addResponseDetails(new JsonServiceResponse.ServiceResponseDetailAttribute(
						RuleConstants.WL_ITEM_IDS_ATTRIBUTE_NAME, (Serializable) itemIds));
			} else {
				resp.setResult(null);
			}
		} else {
			if (wlId != null) {
				resp = new JsonServiceResponse(Status.FAILURE,
						String.format(op + " on Watch List with name='%s' and ID='%s' failed.", wlName, wlId));
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

	public static JsonServiceResponse createKnowledBaseResponse(KnowledgeBase kb, String failureReason) {
		JsonServiceResponse resp = null;
		boolean success = kb == null ? false : true;
		if (success) {
			resp = new JsonServiceResponse(Status.SUCCESS,
					"Knowledge Base creation for all watch lists was successful.");
			resp.addResponseDetails(
					new JsonServiceResponse.ServiceResponseDetailAttribute("id", String.valueOf(kb.getId())));
		} else {
			String msg = null;
			if (StringUtils.isEmpty(failureReason)) {
				msg = "Create KB failed.";
			} else {
				msg = "Create KB failed " + failureReason + ".";
			}
			resp = new JsonServiceResponse(Status.SUCCESS, msg);
		}
		return resp;
	}
}
