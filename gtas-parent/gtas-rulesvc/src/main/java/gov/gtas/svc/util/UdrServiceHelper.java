/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.error.CommonValidationException;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.querybuilder.validation.util.QueryValidationUtils;
import gov.gtas.rule.builder.EngineRuleUtils;
import gov.gtas.rule.builder.util.UdrSplitterUtils;

import java.util.*;

import org.springframework.validation.Errors;

/**
 * Helper class for the UDR service.
 */
public class UdrServiceHelper {

	public static void addEngineRulesToUdrRule(UdrRule parent, UdrSpecification inputJson) {
		// validate and create minterms
		List<List<QueryTerm>> mintermList = createRuleMinterms(inputJson);
		int indx = 0;
		for (List<QueryTerm> minterm : mintermList) {
			Rule rule = EngineRuleUtils.createEngineRule(minterm, parent, indx++);
			parent.addEngineRule(rule);
		}
	}

	/**
	 * Creates engine rules from "minterms" (i.e., sets of AND conditions). This
	 * method is called from the UDR service when a new UDR is being created.
	 *
	 * @param inputJson
	 *            the JSON UDR object
	 */
	private static List<List<QueryTerm>> createRuleMinterms(UdrSpecification inputJson) {
		QueryObject qobj = inputJson.getDetails();
		if (qobj == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
					"details", "Create UDR");
		}
		recursivelyHandleQueryObjects(qobj);
		// validate the input JSON object
		Errors errors = QueryValidationUtils.validateQueryObject(qobj);
		if (errors.hasErrors()) {
			throw new CommonValidationException("JsonToDomainObjectConverter.createEngineRules() - validation errors:",
					errors);
		}
		return UdrSplitterUtils.createFlattenedList(qobj);
	}

	private static void recursivelyHandleQueryObjects(QueryObject qobj) {
		Map<String, UUID> stringUUIDMap = new HashMap<>();

		for (QueryEntity qe : qobj.getRules()) {
			if (qe instanceof QueryTerm) {
				QueryTerm qt = (QueryTerm) qe;
				if (qt.getUuid() == null) {
					EntityEnum entityEnum = EntityEnum.fromString(qt.getEntity()).orElse(EntityEnum.NOT_LISTED);
					UUID qtId = stringUUIDMap.getOrDefault(entityEnum.getEntityName(), UUID.randomUUID());
					stringUUIDMap.putIfAbsent(qt.getEntity(), qtId);
					qt.setUuid(qtId);
					recursivelySetQueryTermUUID(((QueryTerm) qe).getEntity(), qt.getUuid(), qobj);
				}
			}
		}

		for (QueryEntity qe : qobj.getRules()) {
			if (qe instanceof QueryObject) {
				recursivelyHandleQueryObjects((QueryObject) qe);
			}
		}
	}

	private static void recursivelySetQueryTermUUID(String entity, UUID randomUUID, QueryObject qobj) {
		for (QueryEntity qe : qobj.getRules()) {
			if (qe instanceof QueryTerm) {
				QueryTerm qt = (QueryTerm) qe;
				if (entity.equals(qt.getEntity()) && qt.getUuid() == null) {
					qt.setUuid(randomUUID);
				}
			} else if (qe instanceof QueryObject) {
				recursivelySetQueryTermUUID(entity, randomUUID, (QueryObject) qe);
			}
		}
	}

	/**
	 * Creates a new list of engine rules when a UDR is being updated.
	 * 
	 * @param parent
	 *            the UDR.
	 * @param inputJson
	 *            the update JSON object.
	 * @return list of engine rules to replace the existing rules.
	 */
	public static List<Rule> listEngineRules(UdrRule parent, UdrSpecification inputJson) {
		List<Rule> ret = new LinkedList<>();
		List<List<QueryTerm>> ruleDataList = createRuleMinterms(inputJson);
		int indx = 0;
		for (List<QueryTerm> ruleData : ruleDataList) {
			Rule r = EngineRuleUtils.createEngineRule(ruleData, parent, indx);
			r.setParent(parent);
			ret.add(r);
			++indx;
		}
		return ret;
	}

}
