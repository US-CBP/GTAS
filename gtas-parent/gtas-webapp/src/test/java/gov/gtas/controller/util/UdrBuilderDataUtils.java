/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller.util;

import gov.gtas.enumtype.ConditionEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.udr.json.*;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.util.DateCalendarUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UdrBuilderDataUtils {

	public UdrSpecification createSpec() {
		QueryObject queryObject = new QueryObject();
		queryObject.setCondition(ConditionEnum.OR.toString());
		List<QueryEntity> rules = new LinkedList<QueryEntity>();
		QueryTerm trm = new QueryTerm(EntityEnum.PASSENGER.getEntityName(), "embarkationDate", "String",
				CriteriaOperatorEnum.EQUAL.toString(), new String[] { new Date().toString() });
		rules.add(trm);
		rules.add(new QueryTerm(EntityEnum.PASSENGER.getEntityName(), PassengerMapping.LAST_NAME.getFieldName(),
				"String", CriteriaOperatorEnum.EQUAL.toString(), new String[] { "Jones" }));

		QueryObject queryObjectEmbedded = new QueryObject();
		queryObjectEmbedded.setCondition(ConditionEnum.AND.toString());
		List<QueryEntity> rules2 = new LinkedList<QueryEntity>();

		QueryTerm trm2 = new QueryTerm(EntityEnum.PASSENGER.getEntityName(),
				PassengerMapping.EMBARKATION.getFieldName(), "String", CriteriaOperatorEnum.IN.toString(),
				new String[] { "DBY", "PKY", "FLT" });
		rules2.add(trm2);
		rules2.add(new QueryTerm(EntityEnum.PASSENGER.getEntityName(), PassengerMapping.DEBARKATION.getFieldName(),
				"String", CriteriaOperatorEnum.EQUAL.toString(), new String[] { "IAD" }));
		queryObjectEmbedded.setRules(rules2);

		queryObject.setRules(rules);

		UdrSpecification resp = new UdrSpecification(null, queryObject,
				new MetaData("Hello Rule 1", "This is a test", new Date(), "jpjones"));
		return resp;
	}

	public UdrSpecification createSimpleSpec() {
		QueryObject queryObject = new QueryObject();
		queryObject.setCondition(ConditionEnum.OR.toString());
		List<QueryEntity> rules = new LinkedList<QueryEntity>();
		QueryTerm trm = new QueryTerm(EntityEnum.PASSENGER.getEntityName(), "embarkationDate", "String", "EQUAL",
				new String[] { new Date().toString() });
		rules.add(trm);
		rules.add(new QueryTerm(EntityEnum.PASSENGER.getEntityName(), "lastName", "String", "EQUAL",
				new String[] { "Jones" }));

		queryObject.setRules(rules);

		UdrSpecification resp = new UdrSpecification(null, queryObject,
				new MetaData("Hello Rule 1", "This is a test", new Date(), "jpjones"));
		return resp;
	}

	public UdrSpecification createSimpleSpec(String title, String description, String author) {
		QueryObject queryObject = new QueryObject();
		queryObject.setCondition(ConditionEnum.OR.toString());
		List<QueryEntity> rules = new LinkedList<QueryEntity>();
		QueryTerm trm = new QueryTerm(EntityEnum.PASSENGER.getEntityName(), PassengerMapping.DOB.getFieldName(),
				PassengerMapping.DOB.getFieldType(), "EQUAL",
				new String[] { DateCalendarUtils.formatJsonDate(new Date()) });
		rules.add(trm);
		rules.add(new QueryTerm(EntityEnum.PASSENGER.getEntityName(), "lastName", "String", "EQUAL",
				new String[] { "Jones" }));

		queryObject.setRules(rules);

		UdrSpecification resp = new UdrSpecification(null, queryObject,
				new MetaData(title, description, new Date(), author));
		resp.getSummary().setEnabled(true);
		return resp;
	}
}
