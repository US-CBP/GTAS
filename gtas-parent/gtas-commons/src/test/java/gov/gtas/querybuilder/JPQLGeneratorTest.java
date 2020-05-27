/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder;

import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.model.QueryRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.querybuilder.exceptions.InvalidQueryRepositoryException;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class JPQLGeneratorTest {

	EntityEnum queryTypePassenger;
	EntityEnum entityEnumAddress;
	EntityEnum entityEnumDwellTime;
	EntityEnum entityEnumEmail;
	EntityEnum entityEnumFrequentFlyer;
	EntityEnum entityEnumBag;
	EntityEnum entityAgency;

	@Before
	public void before() {

		queryTypePassenger = EntityEnum.PASSENGER;
		entityEnumAddress = EntityEnum.ADDRESS;
		entityEnumDwellTime = EntityEnum.DWELL_TIME;
		entityEnumEmail = EntityEnum.EMAIL;
		entityEnumFrequentFlyer = EntityEnum.FREQUENT_FLYER;
		entityEnumBag = EntityEnum.BAG;
		entityAgency = EntityEnum.TRAVEL_AGENCY;
	}

	@Test
	public void testJoinPnrAddress() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumAddress, queryTypePassenger);
		Assert.assertEquals(" left join pnr.addresses a", joinCondition);

	}

	@Test
	public void testJoinPnrEmail() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumEmail, queryTypePassenger);
		Assert.assertEquals(" left join pnr.emails e", joinCondition);

	}

	@Test
	public void testJoinPnrDwellTime() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumDwellTime, queryTypePassenger);
		Assert.assertEquals(" left join pnr.dwellTimes dwell", joinCondition);

	}

	@Test
	public void testJoinPnrFrequentFlyer() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumFrequentFlyer, queryTypePassenger);
		Assert.assertEquals(" left join pnr.frequentFlyers ff", joinCondition);

	}

	@Test
	public void testJoinPassengerBag() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityEnumBag, queryTypePassenger);
		Assert.assertEquals(" left join p.bags bag", joinCondition);

	}

	@Test
	public void testJoinTravelAgency() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(entityAgency, queryTypePassenger);
		Assert.assertEquals(" left join pnr.agencies ag", joinCondition);

	}

	@Test
	public void testApisFlightCoTravelers() throws InvalidQueryRepositoryException {

		String joinCondition = JPQLGenerator.getJoinCondition(queryTypePassenger, queryTypePassenger);
		Assert.assertEquals(" join f.passengers p", joinCondition);

	}

	@Test
	public void testNotInWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight " +
				"from Passenger p left join p.flight f  left join p.pnrs pnr " +
				"left join pnr.emails e where (e.domain not in (?1) and pnr.id " +
				"not in (select pnr.id from Pnr pnr left join pnr.emails e where e.domain" +
				" in (?1))) and (((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true))" +
				" and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true)" +
				" or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Email");
		mockQueryTerm.setOperator("NOT_IN");
		mockQueryTerm.setValue(new String[]{"HOTMAIL.COM"});
		mockQueryTerm.setField("domain");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testInWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p " +
				"left join p.flight f  left join p.pnrs pnr left join pnr.emails e " +
				"where (e.domain in ?1) and (((p.dataRetentionStatus.maskedAPIS = false " +
				"and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)) " +
				"and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.deletedPNR = false " +
				"and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Email");
		mockQueryTerm.setOperator("IN");
		mockQueryTerm.setValue(new String[]{"HOTMAIL.COM"});
		mockQueryTerm.setField("domain");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testNotEqualsWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p " +
				"left join p.flight f  left join p.pnrs pnr " +
				"left join pnr.emails e " +
				"where (e.domain not in (?1) and pnr.id not in " +
				"(select pnr.id from Pnr pnr left join pnr.emails e where e.domain in " +
				"(?1))) and " +
				"(((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)) " +
				"and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Email");
		mockQueryTerm.setOperator("NOT_EQUAL");
		mockQueryTerm.setValue(new String[]{"HOTMAIL.COM"});
		mockQueryTerm.setField("domain");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testEqualsWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p " +
				"left join p.flight f  " +
				"left join p.pnrs pnr " +
				"left join pnr.emails e where (e.domain = ?1) and " +
				"(((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true))" +
				" and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true)" +
				" or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Email");
		mockQueryTerm.setOperator("EQUALS");
		mockQueryTerm.setValue(new String[]{"HOTMAIL.COM"});
		mockQueryTerm.setField("domain");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testNotInWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p " +
				"left join p.flight f  " +
				"join p.documents d " +
				"where (d.type not in (?1) " +
				"and p.id not in (select p.id from Passenger p left join p.documents d where d.type in (?1))) " +
				"and (((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)) " +
				"and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Document");
		mockQueryTerm.setOperator("NOT_IN");
		mockQueryTerm.setValue(new String[]{"V"});
		mockQueryTerm.setField("type");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testInWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p " +
				"left join p.flight f  join p.documents d where (d.type in ?1) " +
				"and (((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true)" +
				" or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true))" +
				" and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Document");
		mockQueryTerm.setOperator("IN");
		mockQueryTerm.setValue(new String[]{"V"});
		mockQueryTerm.setField("type");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testNotEqualsWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, " +
				"p.flight from Passenger p " +
				"left join p.flight f  join p.documents d " +
				"where (d.type not in (?1) " +
				"and p.id not in (select p.id from Passenger p left join p.documents d where d.type in (?1))) " +
				"and (((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)) " +
				"and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Document");
		mockQueryTerm.setOperator("NOT_EQUAL");
		mockQueryTerm.setValue(new String[]{"V"});
		mockQueryTerm.setField("type");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testEqualsWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, " +
				"p.flight from Passenger p " +
				"left join p.flight f  join p.documents d " +
				"where (d.type = ?1) " +
				"and (((p.dataRetentionStatus.maskedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.maskedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)) " +
				"and ((p.dataRetentionStatus.deletedAPIS = false and p.dataRetentionStatus.hasApisMessage = true) " +
				"or (p.dataRetentionStatus.deletedPNR = false and p.dataRetentionStatus.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Document");
		mockQueryTerm.setOperator("EQUAL");
		mockQueryTerm.setValue(new String[]{"V"});
		mockQueryTerm.setField("type");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testGenericQueryPrefixForFlight() throws InvalidQueryRepositoryException {
		//This does not test any specific clauses or entities. This is to test the leading query prefix to insure it properly uses the passenger as the junction

		String expectedQuery = "select distinct f from Flight f left join f.passengers p join p.documents d where (d.type in ?1)";
		String expectedQuery2 = "select distinct f from Flight f left join f.passengers p left join f.pnrs pnr left join pnr.emails e where (e.domain in ?1)";
		String expectedQuery3 = "select distinct f from Flight f left join f.passengers p left join f.pnrs pnr left join pnr.creditCards cc where (cc.type in ?1)";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("Document");
		mockQueryTerm.setOperator("IN");
		mockQueryTerm.setValue(new String[]{"V"});
		mockQueryTerm.setField("type");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		String query = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.FLIGHT);
		Assert.assertEquals(expectedQuery, query);

		mockQueryTerm.setEntity("Email");
		mockQueryTerm.setValue(new String[]{"HOTMAIL.COM"});
		mockQueryTerm.setField("domain");

		String query2 = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.FLIGHT);
		Assert.assertEquals(expectedQuery2, query2);

		mockQueryTerm.setEntity("Creditcard");
		mockQueryTerm.setValue(new String[]{"VI"});
		mockQueryTerm.setField("type");

		String query3 = JPQLGenerator.generateQuery(mockQueryObject, EntityEnum.FLIGHT);
		Assert.assertEquals(expectedQuery3, query3);
	}

}
