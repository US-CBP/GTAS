/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.mappings.*;

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
		String rule = "$p:Passenger()\n" + "$f:Flight()\n"
				+ "$flpaxlk:FlightPassengerLink(passengerId == $p.id, flightId == $f.id)\n"
				+ "$pnr0:Pnr(bagCount == 0)\n" + "$plink:PnrPassengerLink(pnrId == $pnr0.id, linkAttributeId == $p.id)";
		assertEquals(
				rule,
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

		String expectedOutcome = "$p:Passenger()\n" + "$f:Flight(destination in (\"DBY\", \"XYZ\", \"PQR\"))\n"
				+ "$flpaxlk:FlightPassengerLink(passengerId == $p.id, flightId == $f.id)\n" + "$pnr0:Pnr()\n"
				+ "$a1link:PnrAddressLink(pnrId == $pnr0.id)\n"
				+ "$plink:PnrPassengerLink(pnrId == $pnr0.id, linkAttributeId == $p.id)\n"
				+ "$a1:Address(id == $a1link.linkAttributeId, country != \"USA\", city in (\"FOO\", \"BAR\"))";

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
		String expectedDrools = "$p:Passenger()\n" + "$f:Flight()\n"
				+ "$flpaxlk:FlightPassengerLink(passengerId == $p.id, flightId == $f.id)\n"
				+ "$d1:Document(passengerId == $p.id, issuanceCountry != \"US\")\n" + "$pnr0:Pnr()\n"
				+ "$ph1link:PnrPhoneLink(pnrId == $pnr0.id)\n" + "$e2link:PnrEmailLink(pnrId == $pnr0.id)\n"
				+ "$ff3link:PnrFrequentFlyerLink(pnrId == $pnr0.id)\n"
				+ "$plink:PnrPassengerLink(pnrId == $pnr0.id, linkAttributeId == $p.id)\n"
				+ "$ph1:Phone(id == $ph1link.linkAttributeId, number != null, number not matches \".*456.*\")\n"
				+ "$e2:Email(id == $e2link.linkAttributeId, domain != null, domain not matches \".*.COM\")\n"
				+ "$ff3:FrequentFlyer(id == $ff3link.linkAttributeId, carrier != \"NZ\")";
		assertEquals(expectedDrools, result.toString().trim());
	}

	@Test
	public void testCoTravelerCountRule() throws ParseException {
		/*
		 * test just one document condition. also test multiple PNR related entities.
		 */

		List<QueryTerm> queryTerms = new ArrayList<>();
		QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER, PassengerMapping.APIS_CO_TRAVELERS,
				CriteriaOperatorEnum.EQUAL, new String[] { "4" }, TypeEnum.INTEGER);
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
		String expectedDrools = "$p:Passenger()\n" + "$f:Flight()\n"
				+ "$flpaxlk:FlightPassengerLink(passengerId == $p.id, flightId == $f.id)\n"
				+ "$ptcb:PassengerTripDetails(passengerId == $p.id, coTravelerCount == 4)";
		assertEquals(expectedDrools, result.toString().trim());
	}
}
