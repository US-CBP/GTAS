/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Recursive query condition object.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class QueryObject implements QueryEntity {
    private static final long serialVersionUID = -1825443604051080662L;

    private String condition;
    private List<QueryEntity> rules;

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition
     *            the condition to set
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * @return the rules
     */
    public List<QueryEntity> getRules() {
        return rules;
    }

    /**
     * @param rules
     *            the rules to set
     */
    public void setRules(List<QueryEntity> rules) {
        this.rules = rules;
    }

    @Override
    public List<List<QueryTerm>> createFlattenedList() {
        List<List<QueryTerm>> flatList = new LinkedList<List<QueryTerm>>();
        try {
            final QueryConditionEnum condOp = QueryConditionEnum
                    .valueOf(this.condition);
            switch (condOp) {
            case OR:
                List<QueryEntity> termList = this.getRules();
                if (termList == null || termList.size() < 1) {
                    throw ErrorHandlerFactory
                            .getErrorHandler()
                            .createException(
                                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                                    "rules (null or empty)", "JSON object details");
                }
                for (QueryEntity qtn : termList) {
                    flatList.addAll(qtn.createFlattenedList());
                }
                break;
            case AND:
                termList = this.getRules();
                if (termList == null || termList.size() < 1) {
                    throw ErrorHandlerFactory
                            .getErrorHandler()
                            .createException(
                                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                                    "rules (null or empty)", "JSON object details");
                }
                for (QueryEntity qtn : termList) {
                    if (flatList.isEmpty()) {
                        flatList.addAll(qtn.createFlattenedList());
                    } else {
                        flatList = multiplyFlatLists(flatList,
                                qtn.createFlattenedList());
                    }
                }
                break;
            default:
                throw ErrorHandlerFactory.getErrorHandler().createException(
                        CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                        "condition:"+this.condition, "JSON object details");
            }
        } catch (IllegalArgumentException iae) {
            throw ErrorHandlerFactory.getErrorHandler().createException(
                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                    "condition:"+this.condition, "JSON object details");
        }
        return flatList;
    }

    private List<List<QueryTerm>> multiplyFlatLists(
            List<List<QueryTerm>> list1, List<List<QueryTerm>> list2) {
        List<List<QueryTerm>> flatList = new LinkedList<List<QueryTerm>>();
        for (List<QueryTerm> l1 : list1) {
            for (List<QueryTerm> l2 : list2) {
                List<QueryTerm> newList = new LinkedList<QueryTerm>();
                newList.addAll(l1);
                newList.addAll(l2);
                flatList.add(newList);
            }
        }
        return flatList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }   
}
