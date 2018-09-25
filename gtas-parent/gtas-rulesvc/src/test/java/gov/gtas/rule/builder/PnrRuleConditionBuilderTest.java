/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleTemplateConstants.ADDRESS_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.DOCUMENT_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.EMAIL_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.FLIGHT_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.FREQUENT_FLYER_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_ATTRIBUTE_ID;
import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_PNR_ID;
import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;
import static gov.gtas.rule.builder.RuleTemplateConstants.PASSENGER_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.PHONE_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.PNR_VARIABLE_NAME;
import static gov.gtas.rule.builder.RuleTemplateConstants.FLIGHT_PAX_VARIABLE_NAME;
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

import gov.gtas.rule.builder.pnr.PnrRuleConditionBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PnrRuleConditionBuilderTest {

    private static final Logger logger = LoggerFactory.getLogger(PnrRuleConditionBuilderTest.class);
    
    private RuleConditionBuilder testTarget;

    @Before
    public void setUp() throws Exception {
        testTarget = new RuleConditionBuilder(EngineRuleUtils.createEngineRuleVariableMap());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSingleConditionPNR() throws ParseException {
        /*
         * just one PNR condition.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PNR,
                PNRMapping.BAG_COUNT,
                CriteriaOperatorEnum.EQUAL, new String[]{"0"}, TypeEnum.INTEGER);
        testTarget.addRuleCondition(cond);
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        assertEquals(
                PNR_VARIABLE_NAME+":"+EntityEnum.PNR.getEntityName()+"("
                   +PNRMapping.BAG_COUNT.getFieldName()+" == 0)\n"
                + PASSENGER_VARIABLE_NAME+":"+EntityEnum.PASSENGER.getEntityName()+"()\n"
                + PASSENGER_VARIABLE_NAME+LINK_VARIABLE_SUFFIX+":PnrPassengerLink("+LINK_PNR_ID+" == "
                   +PNR_VARIABLE_NAME+".id, "+LINK_ATTRIBUTE_ID+" == "+PASSENGER_VARIABLE_NAME+".id)",
                result.toString().trim());
    }

    @Test
    public void testSingleConditionPnrFlight() throws ParseException {
        /*
         * no direct PNR criterion, only ADDRESS criteria
         * no passenger criteria
         * single flight criterion with IN operator
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.ADDRESS,
                AddressMapping.COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, new String[]{"usa"}, TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.ADDRESS,
                AddressMapping.CITY,
                CriteriaOperatorEnum.IN, new String[]{"foo", "BAR"}, TypeEnum.STRING);//note foo lower case
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FLIGHT,
                FlightMapping.AIRPORT_DESTINATION,
                CriteriaOperatorEnum.IN, new String[]{"DBY","xyz","PQR"}, TypeEnum.STRING);//note lower case
        testTarget.addRuleCondition(cond);
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        assertTrue(result.length() > 0);
        
        
        /*
$fp:FlightPax(id > 0)
$p:Passenger(id == $fp.passenger.id)
$f:Flight(destination in ("DBY", "XYZ", "PQR"), id == $fp.flight.id)
$addr:Address(country != "USA", city in ("FOO", "BAR"))
$pnr:Pnr()
$addrlink:PnrAddressLink(pnrId == $pnr.id, linkAttributeId == $addr.id)
$plink:PnrPassengerLink(pnrId == $pnr.id, linkAttributeId == $p.id)      
*/        
        
     assertEquals(
             FLIGHT_PAX_VARIABLE_NAME + ":" + EntityEnum.FLIGHT_PAX.getEntityName() + "(id > 0)\n" +
             PASSENGER_VARIABLE_NAME +":"+ EntityEnum.PASSENGER.getEntityName()+ "(id == " +  FLIGHT_PAX_VARIABLE_NAME + ".passenger.id)\n" +
            FLIGHT_VARIABLE_NAME+":"+EntityEnum.FLIGHT.getEntityName()+"("
            +FlightMapping.AIRPORT_DESTINATION.getFieldName()+" in (\"DBY\", \"XYZ\", \"PQR\"), id == " + FLIGHT_PAX_VARIABLE_NAME + ".flight.id)\n"
            + ADDRESS_VARIABLE_NAME+":"+EntityEnum.ADDRESS.getEntityName()+"("
                  +AddressMapping.COUNTRY.getFieldName()+" != \"USA\", "
                  +AddressMapping.CITY.getFieldName()+" in (\"FOO\", \"BAR\"))\n"  //note "foo" converted to upper case
            + PNR_VARIABLE_NAME+":"+EntityEnum.PNR.getEntityName()+"()\n"
            + ADDRESS_VARIABLE_NAME+LINK_VARIABLE_SUFFIX+":PnrAddressLink("+LINK_PNR_ID+" == "
                   +PNR_VARIABLE_NAME+".id, "+LINK_ATTRIBUTE_ID+" == "+ADDRESS_VARIABLE_NAME+".id)\n"
            + PASSENGER_VARIABLE_NAME+LINK_VARIABLE_SUFFIX+":PnrPassengerLink("+LINK_PNR_ID+" == "
               +PNR_VARIABLE_NAME+".id, "+LINK_ATTRIBUTE_ID+" == "+PASSENGER_VARIABLE_NAME+".id)",
            result.toString().trim());       
    }

    @Test
    public void testSingleConditionPnrDocument() throws ParseException {
        /*
         * test just one document condition.
         * also test multiple PNR related entities.
         */
        QueryTerm cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.PHONE,
                PhoneMapping.PHONE_NUMBER,
                CriteriaOperatorEnum.NOT_CONTAINS, new String[]{"456"}, TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.EMAIL,
                EmailMapping.DOMAIN,
                CriteriaOperatorEnum.NOT_ENDS_WITH, new String[]{".com"}, TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.FREQUENT_FLYER,
                FrequentFlyerMapping.CARRIER,
                CriteriaOperatorEnum.NOT_EQUAL, new String[]{"NZ"}, TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        cond = RuleBuilderTestUtils.createQueryTerm(EntityEnum.DOCUMENT,
                DocumentMapping.ISSUANCE_COUNTRY,
                CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
        testTarget.addRuleCondition(cond);
        StringBuilder result = new StringBuilder();
        testTarget.buildConditionsAndApppend(result);
        logger.info(result.toString());
        assertTrue(result.length() > 0);
        /*
            $d:Document(issuanceCountry != "US")
            $p:Passenger(id == $d.passenger.id)
            $ph:Phone(phone_number != null, phone_number not matches ".*456.*")
            $e:Email(domain != null, domain not matches ".*.com.*")
            $ff:FrequentFlyer(ff_airline != "NZ")
            $pnr:Pnr()
            $phlink:PnrPhoneLink(pnrId == $pnr.id, linkAttributeId == $ph.id)
            $elink:PnrEmailLink(pnrId == $pnr.id, linkAttributeId == $e.id)
            $fflink:PnrFrequentFlyerLink(pnrId == $pnr.id, linkAttributeId == $ff.id)
            $plink:PnrPassengerLink(pnrId == $pnr.id, linkAttributeId == $p.id)
         */
        assertEquals(
                DOCUMENT_VARIABLE_NAME+":"+EntityEnum.DOCUMENT.getEntityName()+"("    +DocumentMapping.ISSUANCE_COUNTRY.getFieldName()+" != \"US\")\n"
                + PASSENGER_VARIABLE_NAME+":"+EntityEnum.PASSENGER.getEntityName()+"(id == "+DOCUMENT_VARIABLE_NAME+".passenger.id)\n"
                + PHONE_VARIABLE_NAME+":"+EntityEnum.PHONE.getEntityName()+"("
                    +PhoneMapping.PHONE_NUMBER.getFieldName()+" != null, "
                    +PhoneMapping.PHONE_NUMBER.getFieldName()+" not matches \".*456.*\")\n"
                + EMAIL_VARIABLE_NAME+":"+EntityEnum.EMAIL.getEntityName()+"(domain != null, domain not matches \".*.COM\")\n"
                + FREQUENT_FLYER_VARIABLE_NAME+":"+EntityEnum.FREQUENT_FLYER.getEntityName()+"("
                     +FrequentFlyerMapping.CARRIER.getFieldName()+" != \"NZ\")\n"
                + PNR_VARIABLE_NAME+":"+EntityEnum.PNR.getEntityName()+"()\n"
                + PHONE_VARIABLE_NAME+LINK_VARIABLE_SUFFIX+":PnrPhoneLink("+LINK_PNR_ID+" == "
                           +PNR_VARIABLE_NAME+".id, "+LINK_ATTRIBUTE_ID+" == "+PHONE_VARIABLE_NAME+".id)\n"
                + EMAIL_VARIABLE_NAME+LINK_VARIABLE_SUFFIX+":PnrEmailLink("+LINK_PNR_ID+" == "
                           +PNR_VARIABLE_NAME+".id, "+LINK_ATTRIBUTE_ID+" == "+EMAIL_VARIABLE_NAME+".id)\n"
                + FREQUENT_FLYER_VARIABLE_NAME+LINK_VARIABLE_SUFFIX+":PnrFrequentFlyerLink("+LINK_PNR_ID+" == "
                           +PNR_VARIABLE_NAME+".id, "+LINK_ATTRIBUTE_ID+" == "+FREQUENT_FLYER_VARIABLE_NAME+".id)\n"
                + PASSENGER_VARIABLE_NAME+LINK_VARIABLE_SUFFIX+":PnrPassengerLink("+LINK_PNR_ID+" == "
                           +PNR_VARIABLE_NAME+".id, "+LINK_ATTRIBUTE_ID+" == "+PASSENGER_VARIABLE_NAME+".id)",
                result.toString().trim());
    }
}
