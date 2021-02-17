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
import gov.gtas.querybuilder.model.QueryRequestWithMetaData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
	public void testNotInWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from "
				+ "Passenger p "
				+ "join p.flight f  "
				+ "join p.dataRetentionStatus drsps    "
				+ "join  p.pnrs pnr  "
				+ "join  pnr.emails e "
				+ "where (e.domain not in (?1) "
				+ "and pnr.id "
				+ "not in (select pnr.id from Pnr pnr join pnr.emails e where e.domain in (?1))) "
				+ "and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.maskedPNR = false and drsps.hasPnrMessage = true))"
				+ " and "
				+ "((drsps.deletedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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
		
		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testInWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from "
				+ "Passenger p "
				+ "join p.flight f  "
				+ "join p.dataRetentionStatus drsps    "
				+ "join  p.pnrs pnr  "
				+ "join  pnr.emails e "
				+ "where (e.domain in ?1) "
				+ "and (((drsps.maskedAPIS = false "
				+ "and drsps.hasApisMessage = true) "
				+ "or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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
		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testNotEqualsWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p "
				+ "join p.flight f  join p.dataRetentionStatus drsps    "
				+ "join  p.pnrs pnr  join  pnr.emails e "
				+ "where (e.domain not in (?1) and "
				+ "pnr.id not in (select pnr.id from Pnr pnr join pnr.emails e where e.domain in (?1))) "
				+ "and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true)"
				+ " or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testEqualsWhereClauseForEmail() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p "
				+ "join p.flight f  join p.dataRetentionStatus drsps    "
				+ "join  p.pnrs pnr  join  pnr.emails e where (e.domain = ?1) "
				+ "and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true)"
				+ " or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}


	@Test
	public void testContainsSegmentClause() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p "
				+ "join p.flight f  "
				+ "join p.dataRetentionStatus drsps    "
				+ "join  p.pnrs pnr  "
				+ "join  pnr.savedSegments savedSegment "
				+ "where (savedSegment.rawMessage LIKE ?1) "
				+ "and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true)"
				+ " or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

		QueryObject mockQueryObject  = new QueryObject();
		QueryTerm mockQueryTerm = new QueryTerm();

		mockQueryTerm.setUuid(null);
		mockQueryTerm.setType("string");
		mockQueryTerm.setEntity("SavedSegment");
		mockQueryTerm.setOperator("contains");
		mockQueryTerm.setValue(new String[]{"FOO"});
		mockQueryTerm.setField("rawMessage");

		List<QueryEntity> mockQTList = new ArrayList<>();
		mockQTList.add(mockQueryTerm);

		mockQueryObject.setCondition("AND");
		mockQueryObject.setRules(mockQTList);

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}
	@Test
	public void testNotInWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p "
				+ "join p.flight f  "
				+ "join p.dataRetentionStatus drsps    "
				+ "join  p.documents d "
				+ "where (d.type not in (?1) "
				+ "and p.id not in (select p.id from Passenger p join p.documents d where d.type in (?1))) "
				+ "and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testInWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p "
				+ "join p.flight f  "
				+ "join p.dataRetentionStatus drsps    "
				+ "join  p.documents d "
				+ "where (d.type in ?1) "
				+ "and (((drsps.maskedAPIS = false "
				+ "and drsps.hasApisMessage = true) "
				+ "or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testNotEqualsWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p "
				+ "join p.flight f  "
				+ "join p.dataRetentionStatus drsps    "
				+ "join  p.documents d "
				+ "where (d.type not in (?1) "
				+ "and p.id not in (select p.id from Passenger p join p.documents d where d.type in (?1))) "
				+ "and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testEqualsWhereClauseForDocument() throws InvalidQueryRepositoryException {
		String expectedQuery = "select distinct p.id, p, p.flight from Passenger p "
				+ "join p.flight f  "
				+ "join p.dataRetentionStatus drsps    "
				+ "join  p.documents d where (d.type = ?1) "
				+ "and (((drsps.maskedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.maskedPNR = false and drsps.hasPnrMessage = true)) "
				+ "and ((drsps.deletedAPIS = false and drsps.hasApisMessage = true) "
				+ "or (drsps.deletedPNR = false and drsps.hasPnrMessage = true)))";

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

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.PASSENGER);
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testGenericQueryPrefixForFlight() throws InvalidQueryRepositoryException {
		//This does not test any specific clauses or entities. This is to test the leading query prefix to insure it properly uses the passenger as the junction

		String expectedQuery  = "select distinct f from Flight f join f.passengers p join p.dataRetentionStatus drsps   join  p.documents d where (d.type in ?1)";
		String expectedQuery2 = "select distinct f from Flight f join f.passengers p join p.dataRetentionStatus drsps   join  f.pnrs pnr  join  pnr.emails e where (e.domain in ?1)";
		String expectedQuery3 = "select distinct f from Flight f join f.passengers p join p.dataRetentionStatus drsps   join  f.pnrs pnr  join  pnr.creditCards cc where (cc.type in ?1)";

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

		QueryRequest qeRequest = new QueryRequest();
		qeRequest.setUtcMinuteOffset(0);
		qeRequest.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm = QueryRequestWithMetaData.generate(qeRequest);

		String query = JPQLGenerator.generateQuery(qrwm, EntityEnum.FLIGHT);
		Assert.assertEquals(expectedQuery, query);

		mockQueryTerm.setEntity("Email");
		mockQueryTerm.setValue(new String[]{"HOTMAIL.COM"});
		mockQueryTerm.setField("domain");

		QueryRequest qeRequest2 = new QueryRequest();
		qeRequest2.setUtcMinuteOffset(0);
		qeRequest2.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm2 = QueryRequestWithMetaData.generate(qeRequest);

		String query2 = JPQLGenerator.generateQuery(qrwm2, EntityEnum.FLIGHT);
		Assert.assertEquals(expectedQuery2, query2);

		mockQueryTerm.setEntity("Creditcard");
		mockQueryTerm.setValue(new String[]{"VI"});
		mockQueryTerm.setField("type");
		QueryRequest qeRequest3 = new QueryRequest();
		qeRequest3.setUtcMinuteOffset(0);
		qeRequest3.setQuery(mockQueryObject);
		QueryRequestWithMetaData qrwm3 = QueryRequestWithMetaData.generate(qeRequest);

		String query3 = JPQLGenerator.generateQuery(qrwm3, EntityEnum.FLIGHT);
		Assert.assertEquals(expectedQuery3, query3);
	}

}
