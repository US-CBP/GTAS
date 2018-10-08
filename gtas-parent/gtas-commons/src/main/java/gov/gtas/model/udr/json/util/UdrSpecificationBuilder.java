/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json.util;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.MetaData;
import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.util.DateCalendarUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * A builder pattern object for creating UDR objects programmatically.
 */
public class UdrSpecificationBuilder {
    private Stack<QueryObject> queryObjectStack;
    private Long id;
    private MetaData meta;

    public UdrSpecificationBuilder(Long id) {
        this.id = id;
        queryObjectStack = new Stack<QueryObject>();
    }

    public UdrSpecificationBuilder(Long id, QueryConditionEnum condition) {
        this.id = id;
        queryObjectStack = new Stack<QueryObject>();
        this.addNestedQueryObject(condition);
    }

    public UdrSpecification build() {
        while (queryObjectStack.size() > 1) {
            QueryObject obj = queryObjectStack.pop();
            queryObjectStack.peek().getRules().add(obj);
        }
        QueryObject obj = null;
        if (queryObjectStack.size() > 0) {
            obj = queryObjectStack.pop();
        }
        return new UdrSpecification(id, obj, meta);
    }

    public UdrSpecificationBuilder addMeta(String title, String descr,
            Date startDate, Date endDate, boolean enabled, String author) {
        meta = new MetaData(title, descr, startDate, author);
        meta.setAuthor(author);
        meta.setEnabled(enabled);
        meta.setEndDate(endDate);
        return this;
    }

    public UdrSpecificationBuilder addTerm(EntityEnum entity,
            String attr, TypeEnum type, CriteriaOperatorEnum op, String[] val) {
        queryObjectStack
                .peek()
                .getRules()
                .add(new QueryTerm(entity.getEntityName(), attr, type.toString(), op
                        .toString(), val));
        return this;
    }

    public UdrSpecificationBuilder addTerm(QueryTerm queryTerm) {
        queryObjectStack
                .peek()
                .getRules()
                .add(queryTerm);
        return this;
    }
    public UdrSpecificationBuilder addNestedQueryObject(
            QueryConditionEnum condition) {
        QueryObject queryObject = new QueryObject();
        queryObject.setCondition(condition.toString());
        List<QueryEntity> rules = new LinkedList<QueryEntity>();
        queryObject.setRules(rules);
        queryObjectStack.push(queryObject);
        return this;
    }

    public UdrSpecificationBuilder addSiblingQueryObject(
            QueryConditionEnum condition) {
        if (queryObjectStack.size() <= 1) {
            throw new RuntimeException(
                    "UdrSpecificationBuilder.addSiblingQueryObject() - Cannot add sibling to Root Query Object. Stack size ="
                            + queryObjectStack.size());
        }
        // first add the previous sibling to parent
        QueryObject lastChild = queryObjectStack.pop();
        queryObjectStack.peek().getRules().add(lastChild);

        QueryObject queryObject = new QueryObject();
        queryObject.setCondition(condition.toString());
        List<QueryEntity> rules = new LinkedList<QueryEntity>();
        queryObject.setRules(rules);
        queryObjectStack.push(queryObject);
        return this;
    }

    /**
     * Pops the last child from the stack and adds it as a child to its parent.
     * 
     * @return
     */
    public UdrSpecificationBuilder endCurrentQueryObject() {
        if (queryObjectStack.size() <= 1) {
            throw new RuntimeException(
                    "UdrSpecificationBuilder.endCurrentQueryObject() - There is no child Query object to be ended. Stack size ="
                            + queryObjectStack.size());
        }
        QueryObject lastChild = queryObjectStack.pop();
        queryObjectStack.peek().getRules().add(lastChild);
        return this;
    }

    /**
     * Creates a sample UDR specification JSON object.
     * 
     * @return UDR JSON object.
     */
    public static UdrSpecification createSampleSpec() {
        return createSampleSpec("jpjones", "Test1", "Test Description");
    }

    /**
     * Creates a sample UDR specification JSON object. (This is used for
     * testing.)
     * 
     * @param userId
     * @param title
     * @param description
     * @return
     */
    public static UdrSpecification createSampleSpec(String userId,
            String title, String description) {
        final UdrSpecificationBuilder bldr = new UdrSpecificationBuilder(null,
                QueryConditionEnum.OR);
        bldr.addTerm(EntityEnum.PASSENGER, PassengerMapping.DOB.getFieldName(),
                TypeEnum.DATE,
                CriteriaOperatorEnum.EQUAL,
                new String[] { DateCalendarUtils.formatJsonDate(new Date()) });
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "Jones" });
        bldr.addNestedQueryObject(QueryConditionEnum.AND);
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.EMBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.IN, new String[] {
                        "DBY", "PKY", "FLT" });
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.DEBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "IAD" });
        bldr.addMeta(title, description, new Date(), null, true, userId);
        return bldr.build();
    }

    public static UdrSpecification createSampleSpec2(String userId,
            String title, String description) {
        final UdrSpecificationBuilder bldr = new UdrSpecificationBuilder(null,
                QueryConditionEnum.AND);
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.DOB.getFieldName(),
                TypeEnum.DATE,
                CriteriaOperatorEnum.EQUAL,
                new String[] { DateCalendarUtils.formatJsonDate(new Date()) });
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "Jones" });
        bldr.addMeta(title, description, new Date(), null, true, userId);
        return bldr.build();
    }
    public static UdrSpecification createSampleSpec3(String userId,
            String title, String description) {
        final UdrSpecificationBuilder bldr = new UdrSpecificationBuilder(null,
                QueryConditionEnum.OR);
        bldr.addNestedQueryObject(QueryConditionEnum.AND);
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.DOB.getFieldName(), TypeEnum.DATE,
                CriteriaOperatorEnum.EQUAL,
                new String[] { DateCalendarUtils.formatJsonDate(new Date()) });
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.LAST_NAME.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "Jones" });
        bldr.endCurrentQueryObject();
        bldr.addNestedQueryObject(QueryConditionEnum.AND);
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.EMBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.IN, new String[] {
                        "DBY", "PKY", "FLT" });
        bldr.addTerm(EntityEnum.PASSENGER,
                PassengerMapping.DEBARKATION.getFieldName(),
                TypeEnum.STRING, CriteriaOperatorEnum.EQUAL,
                new String[] { "IAD" });
        bldr.addMeta(title, description, new Date(), null, true, userId);
        return bldr.build();
    }
}
