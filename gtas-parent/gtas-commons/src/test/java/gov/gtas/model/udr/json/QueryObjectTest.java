/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.error.CommonServiceException;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;
import gov.gtas.querybuilder.mappings.PassengerMapping;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QueryObjectTest {
    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected=CommonServiceException.class)
    public void testEmptyError() {
        UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.OR);

        QueryObject qobj = builder.build().getDetails();
        qobj.createFlattenedList();//List<List<QueryTerm>> flatList
    }

    @Test
    public void testOneTerm() {
        UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.AND);
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.CITIZENSHIP_COUNTRY.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL, new String[]{"Jones"});
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"});
        QueryObject qobj = builder.build().getDetails();
        List<List<QueryTerm>> flatList = qobj.createFlattenedList();
        assertNotNull(flatList);
        assertEquals(1,flatList.size());
        List<QueryTerm> mintermList = flatList.get(0);
        assertEquals(2,mintermList.size());
    }
    @Test
    public void testAndOr() {
        UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.AND);
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL, new String[]{"Jones"});
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"});
        builder.addNestedQueryObject(QueryConditionEnum.OR);
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.EMBARKATION.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL, new String[]{"DBY"});
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.CITIZENSHIP_COUNTRY.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.NOT_EQUAL, new String[]{"USA"});
        
        QueryObject qobj = builder.build().getDetails();
        List<List<QueryTerm>> flatList = qobj.createFlattenedList();
        assertNotNull(flatList);
        assertEquals(2,flatList.size());
        List<QueryTerm> mintermList = flatList.get(0);
        assertEquals(3,mintermList.size());
        verifyTerm(mintermList.get(0), EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Jones"} );
        verifyTerm(mintermList.get(1), EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"} );
        verifyTerm(mintermList.get(2), EntityEnum.PASSENGER, PassengerMapping.EMBARKATION.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"DBY"} );
        
        mintermList = flatList.get(1);
        assertEquals(3,mintermList.size());
        verifyTerm(mintermList.get(0), EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Jones"} );
        verifyTerm(mintermList.get(1), EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"} );
        verifyTerm(mintermList.get(2), EntityEnum.PASSENGER, PassengerMapping.CITIZENSHIP_COUNTRY.getFieldName(), CriteriaOperatorEnum.NOT_EQUAL, new String[]{"USA"} );
    }
    private void verifyTerm(QueryTerm trm, EntityEnum entity, String attr, CriteriaOperatorEnum op, String[] val){
        assertEquals(entity.getEntityName(), trm.getEntity());
        assertEquals(attr, trm.getField());
        assertEquals(op.toString(), trm.getOperator());
        assertEquals(val[0], trm.getValue()[0]);
        
    }

    @Test
    public void testFourLevel() {
        UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.AND);
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL, new String[]{"Jones"});
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"});
        builder.addNestedQueryObject(QueryConditionEnum.OR);
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.EMBARKATION.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL, new String[]{"DBY"});
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.CITIZENSHIP_COUNTRY.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.NOT_EQUAL, new String[]{"USA"});
        builder.addNestedQueryObject(QueryConditionEnum.AND);
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.MIDDLE_NAME.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL, new String[]{"Paul"});
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.FIRST_NAME.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.NOT_EQUAL, new String[]{"John"});
        builder.addNestedQueryObject(QueryConditionEnum.OR);
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.EMBARKATION_COUNTRY.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL, new String[]{"GBR"});
        builder.addTerm(EntityEnum.PASSENGER, PassengerMapping.DEBARKATION.getFieldName(), 
                TypeEnum.STRING, CriteriaOperatorEnum.NOT_EQUAL, new String[]{"Timbuktu"});

        QueryObject qobj = builder.build().getDetails();
        List<List<QueryTerm>> flatList = qobj.createFlattenedList();
        assertNotNull(flatList);
        assertEquals(4,flatList.size());
        List<QueryTerm> mintermList = flatList.get(0);
        assertEquals(3,mintermList.size());
        verifyTerm(mintermList.get(0), EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Jones"} );
        verifyTerm(mintermList.get(1), EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"} );
        verifyTerm(mintermList.get(2), EntityEnum.PASSENGER, PassengerMapping.EMBARKATION.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"DBY"} );
        
        mintermList = flatList.get(1);
        assertEquals(3,mintermList.size());
        verifyTerm(mintermList.get(0), EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Jones"} );
        verifyTerm(mintermList.get(1), EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"} );
        verifyTerm(mintermList.get(2), EntityEnum.PASSENGER, PassengerMapping.CITIZENSHIP_COUNTRY.getFieldName(), CriteriaOperatorEnum.NOT_EQUAL, new String[]{"USA"} );

        mintermList = flatList.get(2);
        assertEquals(5,mintermList.size());
        verifyTerm(mintermList.get(0), EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Jones"} );
        verifyTerm(mintermList.get(1), EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"} );
        verifyTerm(mintermList.get(2), EntityEnum.PASSENGER, PassengerMapping.MIDDLE_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Paul"} );
        verifyTerm(mintermList.get(3), EntityEnum.PASSENGER, PassengerMapping.FIRST_NAME.getFieldName(), CriteriaOperatorEnum.NOT_EQUAL, new String[]{"John"} );
        verifyTerm(mintermList.get(4), EntityEnum.PASSENGER, PassengerMapping.EMBARKATION_COUNTRY.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"GBR"} );

        mintermList = flatList.get(3);
        assertEquals(5,mintermList.size());
        verifyTerm(mintermList.get(0), EntityEnum.PASSENGER, PassengerMapping.LAST_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Jones"} );
        verifyTerm(mintermList.get(1), EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(), CriteriaOperatorEnum.LESS, new String[]{"1978-12-24"} );
        verifyTerm(mintermList.get(2), EntityEnum.PASSENGER, PassengerMapping.MIDDLE_NAME.getFieldName(), CriteriaOperatorEnum.EQUAL, new String[]{"Paul"} );
        verifyTerm(mintermList.get(3), EntityEnum.PASSENGER, PassengerMapping.FIRST_NAME.getFieldName(), CriteriaOperatorEnum.NOT_EQUAL, new String[]{"John"} );
        verifyTerm(mintermList.get(4), EntityEnum.PASSENGER, PassengerMapping.DEBARKATION.getFieldName(), CriteriaOperatorEnum.NOT_EQUAL, new String[]{"Timbuktu"} );
                
    }
}
