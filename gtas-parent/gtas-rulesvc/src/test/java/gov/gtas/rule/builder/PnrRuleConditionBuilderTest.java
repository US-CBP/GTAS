/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_ATTRIBUTE_ID;
import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_PNR_ID;
import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;
import static gov.gtas.rule.builder.RuleTemplateConstants.PASSENGER_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.PNR_VARIABLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.mappings.AddressMapping;
import gov.gtas.querybuilder.mappings.DocumentMapping;
import gov.gtas.querybuilder.mappings.EmailMapping;
import gov.gtas.querybuilder.mappings.FlightMapping;
import gov.gtas.querybuilder.mappings.FrequentFlyerMapping;
import gov.gtas.querybuilder.mappings.PNRMapping;
import gov.gtas.querybuilder.mappings.PhoneMapping;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PnrRuleConditionBuilderTest {

	private static final Logger logger = LoggerFactory.getLogger(PnrRuleConditionBuilderTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSingleConditionPNR() throws ParseException {
		/*
		 * just one PNR condition.
		 */
		QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PNR, PNRMapping.BAG_COUNT,
				CriteriaOperatorEnum.EQUAL, new String[] { "0" }, TypeEnum.INTEGER);
		List<QueryTerm> qt = new ArrayList<>();
		cond.setUuid(UUID.randomUUID());
		qt.add(cond);
		RuleConditionBuilder testTarget = new RuleConditionBuilder(qt);
		testTarget.addRuleCondition(cond);
		StringBuilder result = new StringBuilder();
		testTarget.buildConditionsAndApppend(result);
		assertTrue(result.length() > 0);
		assertEquals(
				PNR_VARIABLE_NAME + "0:" + EntityEnum.PNR.getEntityName() + "(" + PNRMapping.BAG_COUNT.getFieldName()
						+ " == 0)\n" + PASSENGER_VARIABLE_NAME + ":" + EntityEnum.PASSENGER.getEntityName() + "()\n"
						+ PASSENGER_VARIABLE_NAME + LINK_VARIABLE_SUFFIX + ":PnrPassengerLink(" + LINK_PNR_ID + " == "
						+ PNR_VARIABLE_NAME + "0.id, " + LINK_ATTRIBUTE_ID + " == " + PASSENGER_VARIABLE_NAME + ".id)",
				result.toString().trim());
	}

	@Test
	public void testSingleConditionPnrFlight() throws ParseException {
		/*
		 * no direct PNR criterion, only ADDRESS criteria no passenger criteria single
		 * flight criterion with IN operator
		 */
		UUID addressUUID = UUID.randomUUID();
		List<QueryTerm> queryTerms = new ArrayList<>();
		QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.ADDRESS, AddressMapping.COUNTRY,
				CriteriaOperatorEnum.NOT_EQUAL, new String[] { "usa" }, TypeEnum.STRING);
		cond.setUuid(addressUUID);
		queryTerms.add(cond);
		cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.ADDRESS, AddressMapping.CITY, CriteriaOperatorEnum.IN,
				new String[] { "foo", "BAR" }, TypeEnum.STRING);// note foo lower case
		cond.setUuid(addressUUID);
		queryTerms.add(cond);
		cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT, FlightMapping.AIRPORT_DESTINATION,
				CriteriaOperatorEnum.IN, new String[] { "DBY", "xyz", "PQR" }, TypeEnum.STRING);// note lower case
		cond.setUuid(UUID.randomUUID());
		queryTerms.add(cond);

		RuleConditionBuilder testTarget = new RuleConditionBuilder(queryTerms);

		for (QueryTerm qt : queryTerms) {
			testTarget.addRuleCondition(qt);
		}

		StringBuilder result = new StringBuilder();
		testTarget.buildConditionsAndApppend(result);
		assertTrue(result.length() > 0);

		String expectedOutcome = "$a1:Address(country != \"USA\", city in (\"FOO\", \"BAR\"))\n"
				+ "$fp:FlightPax(id > 0)\n" + "$p:Passenger(id == $fp.passenger.id)\n"
				+ "$f:Flight(destination in (\"DBY\", \"XYZ\", \"PQR\"), id == $fp.flight.id)\n" + "$pnr0:Pnr()\n"
				+ "$a1link:PnrAddressLink(pnrId == $pnr0.id, linkAttributeId == $a1.id)\n"
				+ "$plink:PnrPassengerLink(pnrId == $pnr0.id, linkAttributeId == $p.id)";

		assertEquals(expectedOutcome, result.toString().trim());
	}

	@Test
	public void testSingleConditionPnrDocument() throws ParseException {
		/*
		 * test just one document condition. also test multiple PNR related entities.
		 */

		List<QueryTerm> queryTerms = new ArrayList<>();
		QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PHONE, PhoneMapping.PHONE_NUMBER,
				CriteriaOperatorEnum.NOT_CONTAINS, new String[] { "456" }, TypeEnum.STRING);
		cond.setUuid(UUID.randomUUID());
		queryTerms.add(cond);
		cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.EMAIL, EmailMapping.DOMAIN,
				CriteriaOperatorEnum.NOT_ENDS_WITH, new String[] { ".com" }, TypeEnum.STRING);
		cond.setUuid(UUID.randomUUID());
		queryTerms.add(cond);
		cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FREQUENT_FLYER, FrequentFlyerMapping.CARRIER,
				CriteriaOperatorEnum.NOT_EQUAL, new String[] { "NZ" }, TypeEnum.STRING);
		cond.setUuid(UUID.randomUUID());
		queryTerms.add(cond);
		cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT, DocumentMapping.ISSUANCE_COUNTRY,
				CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
		cond.setUuid(UUID.randomUUID());
		queryTerms.add(cond);
		RuleConditionBuilder testTarget = new RuleConditionBuilder(queryTerms);
		for (QueryTerm qt : queryTerms) {
			testTarget.addRuleCondition(qt);
		}
		StringBuilder result = new StringBuilder();
		testTarget.buildConditionsAndApppend(result);
		logger.info(result.toString());
		assertTrue(result.length() > 0);
		String expectedDrools = "$ph1:Phone(number != null, number not matches \".*456.*\")\n"
				+ "$e2:Email(domain != null, domain not matches \".*.COM\")\n"
				+ "$ff3:FrequentFlyer(carrier != \"NZ\")\n" + "$d1:Document(issuanceCountry != \"US\")\n"
				+ "$p:Passenger(id == $d1.passenger.id)\n" + "$pnr0:Pnr()\n"
				+ "$ph1link:PnrPhoneLink(pnrId == $pnr0.id, linkAttributeId == $ph1.id)\n"
				+ "$e2link:PnrEmailLink(pnrId == $pnr0.id, linkAttributeId == $e2.id)\n"
				+ "$ff3link:PnrFrequentFlyerLink(pnrId == $pnr0.id, linkAttributeId == $ff3.id)\n"
				+ "$plink:PnrPassengerLink(pnrId == $pnr0.id, linkAttributeId == $p.id)";
		assertEquals(expectedDrools, result.toString().trim());
	}
}
