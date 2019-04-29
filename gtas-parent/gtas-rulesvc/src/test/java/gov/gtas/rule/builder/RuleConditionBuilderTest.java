/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.util.DateCalendarUtils.formatRuleEngineDate;
import static gov.gtas.util.DateCalendarUtils.parseJsonDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.mappings.DocumentMapping;
import gov.gtas.querybuilder.mappings.FlightMapping;
import gov.gtas.querybuilder.mappings.PNRMapping;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import static gov.gtas.rule.builder.RuleTemplateConstants.FLIGHT_PAX_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.PASSENGER_VARIABLE_NAME;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RuleConditionBuilderTest {

    private static final Logger logger = LoggerFactory.getLogger(RuleConditionBuilderTest.class);
    
    private RuleConditionBuilder testTarget;
    private List<QueryTerm> queryTerms;

    @After
    public void tearDown() {
    }
    @Before
    public void setUp() {
        queryTerms = new ArrayList<>();
    }
    @Test
    public void testSingleConditionApisSeat() throws ParseException {
        /*
         * just one Seat condition.
         * also test IN operator.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER,
                PassengerMapping.SEAT,
                CriteriaOperatorEnum.IN, new String[]{"A7865","H76"}, TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        StringBuilder result = new StringBuilder();
        setTestTargetUp(queryTerms);
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        logger.info(result.toString().trim());
        assertEquals(
                "$seat2:Seat("+RuleTemplateConstants.SEAT_ATTRIBUTE_NAME+" in (\"A7865\", \"H76\"), apis == true)\n"
                + "$p:Passenger(id == $seat2.passenger.id)\n"
                + "$f:Flight(id == $seat2.flight.id)",
         result.toString().trim());
    }

    private void setTestTargetUp(List<QueryTerm> queryTerms) {
        testTarget = new RuleConditionBuilder(queryTerms);
        for (QueryTerm qt : queryTerms) {
            testTarget.addRuleCondition(qt);
        }
    }

    @Test
    public void testSingleConditionPnrSeat() throws ParseException {
        /*
         * just one Seat condition.
         * also test IN operator.
         */

        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PNR,
                PassengerMapping.SEAT,
                CriteriaOperatorEnum.IN, new String[]{"A7865","H76"}, TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);



        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        String expectedDrools = "$pnr_seat0:Seat(number in (\"A7865\", \"H76\"), apis == false)\n" +
                "$p:Passenger(id == $pnr_seat0.passenger.id)\n" +
                "$f:Flight(id == $pnr_seat0.flight.id)";
        assertEquals(
                expectedDrools, result.toString().trim());
    }
    @Test
    public void testSingleConditionPassenger() throws ParseException {
        /*
         * just one passenger condition.
         * also test BETWEEN operator.
         */

        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER,
                PassengerMapping.DOB,
                CriteriaOperatorEnum.BETWEEN, new String[]{"1990-01-01","1998-12-31"}, TypeEnum.DATE);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);

        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
 //       assertEquals("$p:Passenger("+PassengerMapping.DOB.getFieldName()+" >= \"01-Jan-1990\", "
   //     +PassengerMapping.DOB.getFieldName()+" <= \"31-Dec-1998\")", result.toString().trim());
    }
    @Test
    public void testSingleConditionFlight() throws ParseException {
        /*
         * just one flight.
         * also test IN operator.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT,
                FlightMapping.AIRPORT_DESTINATION,
                CriteriaOperatorEnum.IN, new String[]{"DBY","XYZ","PQR"}, TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
 
        assertEquals(FLIGHT_PAX_VARIABLE_NAME + ":" + EntityEnum.FLIGHT_PAX.getEntityName() + "(id > 0)\n" +
             PASSENGER_VARIABLE_NAME +":"+ EntityEnum.PASSENGER.getEntityName()+ "(id == " +  FLIGHT_PAX_VARIABLE_NAME + ".passenger.id)\n" +
             "$f:Flight("+FlightMapping.AIRPORT_DESTINATION.getFieldName()+" in (\"DBY\", \"XYZ\", \"PQR\"), id == " + FLIGHT_PAX_VARIABLE_NAME + ".flight.id)",
             result.toString().trim());
    }

    @Test
    public void testSeatAndFlight() throws ParseException {
        /*
         * just one flight.
         * also test ENDS_WITH operator.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT,
                FlightMapping.AIRPORT_DESTINATION,
                CriteriaOperatorEnum.IN, new String[]{"DBY","XYZ","PQR"}, TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);

        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER,
                PassengerMapping.SEAT,
                CriteriaOperatorEnum.ENDS_WITH, new String[]{"31"}, TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);

        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        logger.info(result.toString().trim());
        assertEquals(
                "$seat2:Seat("+RuleTemplateConstants.SEAT_ATTRIBUTE_NAME+" != null, "
                         + RuleTemplateConstants.SEAT_ATTRIBUTE_NAME + " str[endsWith] \"31\", apis == true)\n"
                + "$p:Passenger(id == $seat2.passenger.id)\n"
                + "$f:Flight("+FlightMapping.AIRPORT_DESTINATION.getFieldName()+" in (\"DBY\", \"XYZ\", \"PQR\"), id == $seat2.flight.id)",
                result.toString().trim());
    }

    @Test
    public void testSeatAndDocument() throws ParseException {
        /*
         * test just one document condition.
         * also test BEGINS_WITH operator.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);

        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);

        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PNR,
                PNRMapping.SEAT,
                CriteriaOperatorEnum.BEGINS_WITH, "29D", TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);



        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        String expectedDrools = "$pnr_seat0:Seat(number != null, number str[startsWith] \"29D\", apis == false)\n" +
                "$d1:Document(issuanceCountry != \"US\")\n" +
                "$p:Passenger(id == $pnr_seat0.passenger.id, id == $d1.passenger.id)\n" +
                "$f:Flight(id == $pnr_seat0.flight.id)";
        assertEquals(expectedDrools,
                result.toString().trim());
    }

    @Test
    public void testSingleConditionDocument() throws ParseException {
        /*
         * test just one document condition.
         * also test NOT_EQUAL operator.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        StringBuilder result = new StringBuilder();
        setTestTargetUp(queryTerms);

        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        String expectedDrools = "$d1:Document(issuanceCountry != \"US\")\n" +
                "$p:Passenger(id == $d1.passenger.id)";
        assertEquals(expectedDrools, result.toString().trim());
    }
    
    @Test
    @Ignore
    public void testStringConditionsOnPassenger() throws ParseException {
        for(CriteriaOperatorEnum op:
            new CriteriaOperatorEnum[]{
                CriteriaOperatorEnum.EQUAL, 
                CriteriaOperatorEnum.NOT_EQUAL,
                CriteriaOperatorEnum.BEGINS_WITH,
                CriteriaOperatorEnum.ENDS_WITH,
                CriteriaOperatorEnum.CONTAINS,
                CriteriaOperatorEnum.NOT_BEGINS_WITH,
                CriteriaOperatorEnum.NOT_ENDS_WITH,
                CriteriaOperatorEnum.NOT_CONTAINS,
                CriteriaOperatorEnum.NOT_IN,
                CriteriaOperatorEnum.IN
                }){
            verifyStringConditionOnPassenger(op);
        }
    }
    private void verifyStringConditionOnPassenger(CriteriaOperatorEnum op) throws ParseException{
        String[] val = new String[]{"Foo", "Bar"};
        String attr = PassengerMapping.LAST_NAME.getFieldName();
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME,
                op, val, TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        StringBuilder res = new StringBuilder();
        testTarget.buildConditionsAndApppend(res);
        assertTrue(res.length() > 0);
        
        String result = res.toString().trim();
        String prefix = "$p:Passenger(";
        switch(op){
        case EQUAL:
            assertEquals(prefix+attr+" == \"" + val[0].toUpperCase() + "\")", result);
            break;
        case NOT_EQUAL:
            assertEquals(prefix+attr+" != \"" + val[0].toUpperCase() + "\")", result);
            break;
        case IN:
            assertEquals(prefix+attr+" in " + createStringValueList(val)+")", result);
            break;
        case NOT_IN:
            assertEquals(prefix+attr+" not in " + createStringValueList(val)+")", result);
            break;
        case BEGINS_WITH:
            assertEquals(prefix + attr + " != null, "
                    +attr+" str[startsWith] \"" + val[0].toUpperCase() + "\")", result);
            break;
        case NOT_BEGINS_WITH:
            assertEquals(prefix + attr + " != null, "
                    +attr+" not matches \"" + val[0].toUpperCase() + ".*\")", result);
            break;
        case ENDS_WITH:
            assertEquals(prefix + attr + " != null, "
                    +attr+" str[endsWith] \"" + val[0].toUpperCase() + "\")", result);
            break;
        case NOT_ENDS_WITH:
            assertEquals(prefix + attr + " != null, "+
                    attr+" not matches \".*" + val[0].toUpperCase() + "\")", result);
            break;
        case CONTAINS:
            assertEquals(prefix + attr + " != null, "
                    +attr+" matches \".*" + val[0].toUpperCase() + ".*\")", result);
            break;
        case NOT_CONTAINS:
            assertEquals(prefix + attr + " != null, "
                    +attr+" not matches \".*" + val[0].toUpperCase() + ".*\")", result);
            break;
            default:
                fail("Unknown String operator");
        }
    }
    private String createStringValueList(String[] values){
        List<String> strList = Arrays.asList(values);
        String res = String.join("\", \"", strList);
        return "(\""+res.toUpperCase()+"\")";
    }
    @Test
    @Ignore
    public void testDateConditionsOnPassenger() throws ParseException {
        for(CriteriaOperatorEnum op:
            new CriteriaOperatorEnum[]{
                CriteriaOperatorEnum.EQUAL, 
                CriteriaOperatorEnum.NOT_EQUAL,
                CriteriaOperatorEnum.BETWEEN,
                CriteriaOperatorEnum.NOT_BETWEEN,
                CriteriaOperatorEnum.GREATER,
                CriteriaOperatorEnum.GREATER_OR_EQUAL,
                CriteriaOperatorEnum.LESS,
                CriteriaOperatorEnum.LESS_OR_EQUAL,
                CriteriaOperatorEnum.NOT_IN,
                CriteriaOperatorEnum.IN
                }){
            verifyDateConditionOnPassenger(op);
        }
    }
    private void verifyDateConditionOnPassenger(CriteriaOperatorEnum op) throws ParseException{
        String[] val = new String[]{"2011-05-24", "2015-01-25"};
        String attr = PassengerMapping.DOB.getFieldName();
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER,
                PassengerMapping.DOB,
                op, val, TypeEnum.DATE);
        testTarget.addRuleCondition(cond);
        StringBuilder res = new StringBuilder();
        testTarget.buildConditionsAndApppend(res);
        assertTrue(res.length() > 0);
        
        String result = res.toString().trim();
        String prefix = "$p:Passenger(";
        switch(op){
        case EQUAL:
            assertEquals(prefix+attr+" == \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\")", result);
            break;
        case NOT_EQUAL:
            assertEquals(prefix+attr+" != \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\")", result);
            break;
        case IN:
            assertEquals(prefix+attr+" in " + createDateValueList(val)+")", result);
            break;
        case NOT_IN:
            assertEquals(prefix+attr+" not in " + createDateValueList(val)+")", result);
            break;
        case GREATER:
            assertEquals(prefix 
                    +attr+" > \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\")", result);
            break;
        case GREATER_OR_EQUAL:
            assertEquals(prefix
                    +attr+" >= \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\")", result);
            break;
        case LESS:
            assertEquals(prefix
                    +attr+" < \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\")", result);
            break;
        case LESS_OR_EQUAL:
            assertEquals(prefix
                    +attr+" <= \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\")", result);
            break;
        case BETWEEN:
            assertEquals(prefix
                    +attr+" >= \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\", "
                    +attr+ " <= \""+ formatRuleEngineDate(parseJsonDate(val[1])) +"\")", result);
            break;
        case NOT_BETWEEN:
            assertEquals(prefix + "("
                    +attr+" < \"" + formatRuleEngineDate(parseJsonDate(val[0])) + "\" || "
                    +attr+ " > \""+ formatRuleEngineDate(parseJsonDate(val[1])) +"\"))", result);
            break;
            default:
                fail("Unknown String operator");
        }
    }
    private String createDateValueList(String[] values) throws ParseException{
        for(int i = 0; i < values.length; ++i){
            values[i] = formatRuleEngineDate(parseJsonDate(values[i]));
        }
        List<String> strList = Arrays.asList(values);
        String res = String.join("\", \"", strList);
        return "(\""+res+"\")";
    }

    @Test
    public void testMultipleConditionsDocument() throws ParseException {
        /*
         * test multiple document conditions.
         * also test GREATER_EQUAL and NOT_EQUAL.
         */
        UUID docUUID = UUID.randomUUID();
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
        cond.setUuid(docUUID);
        queryTerms.add(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_DATE,
                CriteriaOperatorEnum.GREATER_OR_EQUAL, "2010-01-01", TypeEnum.DATE);
        cond.setUuid(docUUID);
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);

        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        String expectedDrools = "$d1:Document(issuanceCountry != \"US\", issuanceDate >= \"01-Jan-2010\")\n" +
                "$p:Passenger(id == $d1.passenger.id)";
        assertEquals(expectedDrools, result.toString().trim());
    }
    
    @Test
    public void testDocumentWithTypeEquality() throws ParseException {
        /*
         * one document condition and one type equality
         */
        UUID docUUID = UUID.randomUUID();

        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
        cond.setUuid(docUUID);
        queryTerms.add(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.DOCUMENT_TYPE,
                CriteriaOperatorEnum.EQUAL, "P", TypeEnum.STRING);
        cond.setUuid(docUUID);
        queryTerms.add(cond);

        setTestTargetUp(queryTerms);
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        String expectedDrools = "$d1:Document(issuanceCountry != \"US\", documentType == \"P\")\n" +
                "$p:Passenger(id == $d1.passenger.id)";
        assertEquals(expectedDrools, result.toString().trim());
    }
    @Test
    public void testDocumentTypeEqualityOnly() throws ParseException {
        /*
         * one document condition and one type equality
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.DOCUMENT_TYPE,
                CriteriaOperatorEnum.EQUAL, "P", TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        String expectedDrools = "$d1:Document(documentType == \"P\")\n" +
                "$p:Passenger(id == $d1.passenger.id)";
        assertEquals(expectedDrools, result.toString().trim());
    }
    @Test
    public void testDocumentWithTypeInEquality() throws ParseException {
        /*
         * one document condition and one type equality
         */

        UUID docUUID = UUID.randomUUID();

        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
        cond.setUuid(docUUID);
        queryTerms.add(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.DOCUMENT_TYPE,
                CriteriaOperatorEnum.NOT_EQUAL, "P", TypeEnum.STRING);
        cond.setUuid(docUUID);
        queryTerms.add(cond);

        setTestTargetUp(queryTerms);


        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        logger.info(result.toString());
        assertTrue(result.length() > 0);
        String expectedDrools = "$d1:Document(issuanceCountry != \"US\", documentType != \"P\")\n" +
                "$p:Passenger(id == $d1.passenger.id)";
        assertEquals(expectedDrools, result.toString().trim());
    }
    @Test
    public void testDocumentTypeInEqualityOnly() throws ParseException {
        /*
         * one document condition and one type inequality
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.DOCUMENT_TYPE,
                CriteriaOperatorEnum.NOT_EQUAL, "P", TypeEnum.STRING);
        cond.setUuid(UUID.randomUUID());
        queryTerms.add(cond);
        setTestTargetUp(queryTerms);
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        String expectedDrools = "$d1:Document(documentType != \"P\")\n" +
                "$p:Passenger(id == $d1.passenger.id)";
        assertEquals(expectedDrools, result.toString().trim());
    }
    @Test
    @Ignore
    public void testMultipleConditionsPersonFlightDocument() throws ParseException {
        /*
         * conditions for passenger, document and Flight.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_DATE,
                CriteriaOperatorEnum.GREATER_OR_EQUAL, "2010-01-01", TypeEnum.DATE);
        testTarget.addRuleCondition(cond);

        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER,
                PassengerMapping.DOB,
                CriteriaOperatorEnum.BETWEEN, new String[]{"1990-01-01","1998-12-31"}, TypeEnum.DATE);
        testTarget.addRuleCondition(cond);

        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT,
                FlightMapping.AIRPORT_DESTINATION,
                CriteriaOperatorEnum.EQUAL, "DBY", TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT,
                FlightMapping.ETA,
                CriteriaOperatorEnum.EQUAL, "2015-01-01", TypeEnum.DATE);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT,
                FlightMapping.ETD,
                CriteriaOperatorEnum.EQUAL, "2015-01-01", TypeEnum.DATE);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT,
                FlightMapping.FLIGHT_NUMBER,
                CriteriaOperatorEnum.EQUAL, "2231", TypeEnum.INTEGER);
        testTarget.addRuleCondition(cond);

        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME,
                CriteriaOperatorEnum.EQUAL, "Jones", TypeEnum.STRING);
        testTarget.addRuleCondition(cond);

        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PNR,
                PassengerMapping.SEAT,
                CriteriaOperatorEnum.EQUAL, new String[]{"A7865"}, TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
   
        assertEquals(
                "$seat:Seat("+RuleTemplateConstants.SEAT_ATTRIBUTE_NAME+" == \"A7865\", apis == false)\n"
                +"$d:Document("+DocumentMapping.ISSUANCE_COUNTRY.getFieldName()+" != \"US\", "
                    +DocumentMapping.ISSUANCE_DATE.getFieldName()+" >= \"01-Jan-2010\")\n"
                +"$p:Passenger("
                    +PassengerMapping.DOB.getFieldName()+" >= \"01-Jan-1990\", "
                    +PassengerMapping.DOB.getFieldName()+" <= \"31-Dec-1998\", "
                    +PassengerMapping.LAST_NAME.getFieldName()+" == \"JONES\", "
                    +"id == $seat.passenger.id, id == $d.passenger.id)\n"

                + "$f:Flight("+FlightMapping.AIRPORT_DESTINATION.getFieldName()+" == \"DBY\", "
                   +FlightMapping.ETA.getFieldName()+" == \"01-Jan-2015\", "
                   +FlightMapping.ETD.getFieldName()+" == \"01-Jan-2015\", "
                   +FlightMapping.FLIGHT_NUMBER.getFieldName()+" == 2231, "
                   + "id == $seat.flight.id)",
        result.toString().trim());
    }
}
