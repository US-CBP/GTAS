/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.User;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.RuleMeta;
//import gov.gtas.model.udr.RuleCond;
//import gov.gtas.model.udr.RuleCondPk;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.mappings.AddressMapping;
import gov.gtas.querybuilder.mappings.CreditCardMapping;
import gov.gtas.querybuilder.mappings.DocumentMapping;
import gov.gtas.querybuilder.mappings.EmailMapping;
import gov.gtas.querybuilder.mappings.FlightMapping;
import gov.gtas.querybuilder.mappings.FrequentFlyerMapping;
import gov.gtas.querybuilder.mappings.IEntityMapping;
import gov.gtas.querybuilder.mappings.PNRMapping;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.querybuilder.mappings.PhoneMapping;
import gov.gtas.querybuilder.mappings.TravelAgencyMapping;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RuleBuilderTestUtils {
    public static final String UDR_RULE_TITLE="UDR_TEST_RULE";
    public static final long UDR_RULE_ID=33L;
    public static final long ENGINE_RULE_ID=21L;
    public static final int DOC_FLIGHT_CRITERIA_RULE_INDX=1;
    public static final int ENGINE_RULE_INDX2=2;
    public static final int ENGINE_RULE_INDX3=3;
    public static final int PNR_CRITERIA_RULE_INDX=4;
    public static final int PNR_PASSENGER_RULE_INDX=5;
    public static final int ADDRESS_PHONE_EMAIL_DOCUMENT_RULE_INDX=6;
    public static final int AGENCY_CC_FF_FLIGHT_DOC_RULE_INDX=7;
    public static final int FLIGHT_ETA_ETD_RULE_INDX=8;
    public static final int PASSENGER_SEAT_RULE_INDX=9;
    public static final int PNR_SEAT_RULE_INDX=10;
    
    public static UdrRule createSimpleUdrRule(String userId, int indx) throws ParseException{
        return createSimpleUdrRule(userId, indx, new Date(), null);
    }
    public static UdrRule createSimpleUdrRule(String userId, int indx, Date startDate, Date endDate) throws ParseException{
        UdrRule ret = new UdrRule(UDR_RULE_ID, YesNoEnum.N, null, new Date());
        User author = new User();
        author.setUserId(userId);
        ret.setAuthor(author);
        ret.setTitle(UDR_RULE_TITLE);
        RuleMeta meta = new RuleMeta(ret, UDR_RULE_TITLE, UDR_RULE_TITLE, startDate, endDate, YesNoEnum.Y, YesNoEnum.Y, YesNoEnum.Y);
        ret.setMetaData(meta);
        Rule engineRule = createEngineRule(ENGINE_RULE_ID, ret, indx);      
        ret.addEngineRule(engineRule);
        
        return ret;
    }
    /**
     * Create a Rule condition object using common (query and criteria)
     * enums.
     * @param ent
     * @param attr
     * @param op
     * @param value
     * @param type
     * @return
     * @throws ParseException
     */
    public static QueryTerm createQueryTerm(EntityEnum entity,
            IEntityMapping attr, CriteriaOperatorEnum op, String value,
            TypeEnum type) throws ParseException {
        QueryTerm ret = new QueryTerm(entity.getEntityName(), attr.getFieldName(), type.getType(), op.toString(), new String[]{value});
        return ret;
    }
    public static QueryTerm createQueryTerm(EntityEnum entity,
            IEntityMapping attr, CriteriaOperatorEnum op, String[] values,
            TypeEnum type) throws ParseException {
        QueryTerm ret = new QueryTerm(entity.getEntityName(), attr.getFieldName(), type.getType(), op.toString(), values);
        return ret;
    }

    //////////////////////////////////////////////////////
    //RULES
    /////////////////////////////////////////////////////
    private static Rule createEngineRule(Long id, UdrRule parent, int indx) throws ParseException{
        Rule engineRule = null;
        List<QueryTerm> ruleMinTerm = new LinkedList<QueryTerm>();
        switch(indx){
            case DOC_FLIGHT_CRITERIA_RULE_INDX:/* doc.iso2 != US && doc.issueDate > 2012-01-01 && flight# == 0012  */
                QueryTerm cond = createQueryTerm(EntityEnum.DOCUMENT,
                        DocumentMapping.ISSUANCE_COUNTRY,
                        CriteriaOperatorEnum.NOT_EQUAL, "US", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.DOCUMENT,
                        DocumentMapping.ISSUANCE_DATE,
                        CriteriaOperatorEnum.GREATER_OR_EQUAL, "2012-01-01", TypeEnum.DATE);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.FLIGHT_NUMBER,
                        CriteriaOperatorEnum.EQUAL, "0012", TypeEnum.STRING);               
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case ENGINE_RULE_INDX2:/* doc.iso2 in (YE,GB) && flight.origin.iata == LHR && flight.carrier.iata==CO  */
                cond = createQueryTerm(EntityEnum.DOCUMENT,
                        DocumentMapping.ISSUANCE_COUNTRY,
                        CriteriaOperatorEnum.IN, new String[]{"YE", "GB"}, TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.AIRPORT_ORIGIN,
                        CriteriaOperatorEnum.EQUAL, "LHR", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.CARRIER,
                        CriteriaOperatorEnum.EQUAL, "CO", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case ENGINE_RULE_INDX3:/* flight.origin.iata == LHR && flight.carrier.iata==CO  */
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.AIRPORT_ORIGIN,
                        CriteriaOperatorEnum.EQUAL, "LHR", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.CARRIER,
                        CriteriaOperatorEnum.EQUAL, "CO", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.FLIGHT_DATE,
                        CriteriaOperatorEnum.GREATER, "2015-07-20 14:00:00", TypeEnum.DATETIME);
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case PNR_CRITERIA_RULE_INDX:
                cond = createQueryTerm(EntityEnum.PNR,
                        PNRMapping.RECORD_LOCATOR,
                        CriteriaOperatorEnum.NOT_CONTAINS, "FOO", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PNR,
                        PNRMapping.RECORD_LOCATOR,
                        CriteriaOperatorEnum.CONTAINS, "CO", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PNR,
                        PNRMapping.RECORD_LOCATOR,
                        CriteriaOperatorEnum.BEGINS_WITH, "DU", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case PNR_PASSENGER_RULE_INDX:
                cond = createQueryTerm(EntityEnum.PNR,
                        PNRMapping.RECORD_LOCATOR,
                        CriteriaOperatorEnum.NOT_CONTAINS, "3255", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PNR,
                        PNRMapping.RECORD_LOCATOR,
                        CriteriaOperatorEnum.CONTAINS, "901", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PNR,
                        PNRMapping.RECORD_LOCATOR,
                        CriteriaOperatorEnum.BEGINS_WITH, "VYZ", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PASSENGER,
                        PassengerMapping.PASSENGER_TYPE,
                        CriteriaOperatorEnum.EQUAL, "P", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PASSENGER,
                        PassengerMapping.LAST_NAME,
                        CriteriaOperatorEnum.EQUAL, "Baggins", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case ADDRESS_PHONE_EMAIL_DOCUMENT_RULE_INDX:
                cond = createQueryTerm(EntityEnum.ADDRESS,
                        AddressMapping.COUNTRY,
                        CriteriaOperatorEnum.NOT_EQUAL, "USA", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.ADDRESS,
                        AddressMapping.ADDRESS_LINE_1,
                        CriteriaOperatorEnum.CONTAINS, "Nowhere", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PHONE,
                        PhoneMapping.PHONE_NUMBER,
                        CriteriaOperatorEnum.ENDS_WITH, "9087", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.EMAIL,
                        EmailMapping.DOMAIN,
                        CriteriaOperatorEnum.NOT_ENDS_WITH, "om", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.EMAIL,
                        EmailMapping.DOMAIN,
                        CriteriaOperatorEnum.NOT_BEGINS_WITH, "all", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.DOCUMENT,
                        DocumentMapping.ISSUANCE_COUNTRY,
                        CriteriaOperatorEnum.NOT_IN, new String[]{"GBR", "USA"}, TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.DOCUMENT,
                        DocumentMapping.ISSUANCE_DATE,
                        CriteriaOperatorEnum.BETWEEN, new String[]{"2012-05-01", "2013-06-30"}, TypeEnum.DATE);
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case AGENCY_CC_FF_FLIGHT_DOC_RULE_INDX:
                cond = createQueryTerm(EntityEnum.TRAVEL_AGENCY,
                        TravelAgencyMapping.NAME,
                        CriteriaOperatorEnum.ENDS_WITH, "Tours", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.CREDIT_CARD,
                        CreditCardMapping.CREDIT_CARD_NUMBER,
                        CriteriaOperatorEnum.BEGINS_WITH, "123", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FREQUENT_FLYER,
                        FrequentFlyerMapping.CARRIER,
                        CriteriaOperatorEnum.EQUAL, "aa", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.AIRPORT_DESTINATION,
                        CriteriaOperatorEnum.EQUAL, "jfk", TypeEnum.STRING);
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.DOCUMENT,
                        DocumentMapping.ISSUANCE_DATE,
                        CriteriaOperatorEnum.LESS, "2014-01-30", TypeEnum.DATE);
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case FLIGHT_ETA_ETD_RULE_INDX:
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.ETA,
                        CriteriaOperatorEnum.EQUAL, "2015-01-01", TypeEnum.DATE);               
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.FLIGHT,
                        FlightMapping.ETD,
                        CriteriaOperatorEnum.EQUAL, "2015-01-01", TypeEnum.DATE);               
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case PASSENGER_SEAT_RULE_INDX:
                cond = createQueryTerm(EntityEnum.PASSENGER,
                        PassengerMapping.SEAT,
                        CriteriaOperatorEnum.IN, new String[]{"39G", "24A"}, TypeEnum.STRING);              
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PASSENGER,
                        PassengerMapping.FIRST_NAME,
                        CriteriaOperatorEnum.NOT_IN, new String[]{"John", "Dotin"}, TypeEnum.STRING);               
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
            case PNR_SEAT_RULE_INDX:
                cond = createQueryTerm(EntityEnum.PNR,
                        PassengerMapping.SEAT,
                        CriteriaOperatorEnum.IN, new String[]{"55E", "39G"}, TypeEnum.STRING);              
                ruleMinTerm.add(cond);
                cond = createQueryTerm(EntityEnum.PASSENGER,
                        PassengerMapping.FIRST_NAME,
                        CriteriaOperatorEnum.NOT_IN, new String[]{"John", "Dotin"}, TypeEnum.STRING);               
                ruleMinTerm.add(cond);
                engineRule = EngineRuleUtils.createEngineRule(ruleMinTerm, parent, indx);
                engineRule.setId(ENGINE_RULE_ID);
                break;
        }
        return engineRule;
    }
}
