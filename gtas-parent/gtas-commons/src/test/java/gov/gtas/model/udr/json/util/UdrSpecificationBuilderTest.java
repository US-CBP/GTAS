/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.util.DateCalendarUtils;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UdrSpecificationBuilderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidConstructon() {
        UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.OR);
        // cannot add sibling to top level
        builder.addSiblingQueryObject(QueryConditionEnum.AND);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidConstructon2() {
        UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.OR);
        // cannot pop the root
        builder.endCurrentQueryObject();
    }
    @Test
    public void testSimple() {
        UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.OR);
        // add terms and then another query object
        builder.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.DEBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "IAD" });
        builder.addNestedQueryObject(QueryConditionEnum.AND);
        builder.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "Jones" });
        builder.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.EMBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "DBY" });

        UdrSpecification spec = builder.build();
        assertNotNull(spec);
        assertNotNull(spec.getDetails());
        QueryObject topLevel = spec.getDetails();
        assertEquals(QueryConditionEnum.OR.toString(), topLevel.getCondition());
        assertNull(spec.getId());
        assertNull(spec.getSummary());
        
        assertEquals(2, topLevel.getRules().size());
        List<QueryEntity> rules = topLevel.getRules();
        assertEquals(2, rules.size());
        verifyQueryTerm(rules.get(0), EntityEnum.PASSENGER,
                PassengerMapping.DEBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "IAD" });
        assertTrue(rules.get(1) instanceof QueryObject);
        QueryObject embedded = (QueryObject) (rules.get(1));
        assertEquals(QueryConditionEnum.AND.toString(), embedded.getCondition());
        rules = embedded.getRules();
        assertEquals(2, rules.size());
        verifyQueryTerm(rules.get(0), EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "Jones" });
        verifyQueryTerm(rules.get(1), EntityEnum.PASSENGER,
                PassengerMapping.EMBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "DBY" });
    }

    @Test
    public void testOrAnd() {
        UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec3("jpjones", "Test OR of AND", "OR condition with two AND children");
        assertNotNull(spec);
        assertNotNull(spec.getDetails());
        QueryObject topLevel = spec.getDetails();
        assertEquals(QueryConditionEnum.OR.toString(), topLevel.getCondition());
        assertNull(spec.getId());
        
        assertEquals(2, topLevel.getRules().size());
        List<QueryEntity> rules = topLevel.getRules();
        assertEquals(2, rules.size());
        
        QueryEntity entity = rules.get(0);
        assertTrue(entity instanceof QueryObject);
        QueryObject qobj = (QueryObject) entity;
        rules = qobj.getRules();
        assertEquals(2, rules.size());      
        verifyQueryTerm(rules.get(0), EntityEnum.PASSENGER,
                PassengerMapping.DOB.getFieldName(),
                TypeEnum.DATE, CriteriaOperatorEnum.EQUAL,
                new String[] {  DateCalendarUtils.formatJsonDate(new Date()) });
        verifyQueryTerm(rules.get(1), EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] {  "Jones" });
        
        assertTrue(topLevel.getRules().get(1) instanceof QueryObject);
        QueryObject qobj2 = (QueryObject)topLevel.getRules().get(1);
        assertEquals(QueryConditionEnum.AND.toString(), qobj2.getCondition());
        rules = qobj2.getRules();
        assertEquals(2, rules.size());
        verifyQueryTerm(rules.get(0), EntityEnum.PASSENGER,
                PassengerMapping.EMBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.IN,
                new String[] { "DBY", "PKY", "FLT"});
        verifyQueryTerm(rules.get(1), EntityEnum.PASSENGER,
                PassengerMapping.DEBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "IAD" });
    }
    private void verifyQueryTerm(QueryEntity obj, EntityEnum entity,
            String attr, TypeEnum type, CriteriaOperatorEnum op,
            String[] val) {
        assertTrue(obj instanceof QueryTerm);
        QueryTerm term = (QueryTerm)obj;
        assertEquals("verifyQueryTerm - entity does not match", entity.getEntityName(), term.getEntity());
        assertEquals("verifyQueryTerm - attribute does not match", attr, term.getField());
        assertEquals("verifyQueryTerm - type does not match", type.toString(), term.getType());
        assertEquals("verifyQueryTerm - operator does not match", op.toString(), term.getOperator());
        assertEquals("verifyQueryTerm - value does not match", val[0], term.getValue()[0]);
    }
}
