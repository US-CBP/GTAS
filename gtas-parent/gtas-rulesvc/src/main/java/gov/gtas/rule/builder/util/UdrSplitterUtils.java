/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.util;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility functions to split UDRs into objects suitable for conversion to
 * Drools Rules.
 */
public class UdrSplitterUtils {
    public static List<List<QueryTerm>> createFlattenedList(QueryEntity entity) {
        if(entity instanceof QueryTerm){
            return createFlattenedList((QueryTerm)entity);
        } else if(entity instanceof QueryObject){
            return createFlattenedList((QueryObject)entity);
        } else {
            return new LinkedList<List<QueryTerm>>();
        }
    }
    public static List<List<QueryTerm>> createFlattenedList(QueryTerm term) {
        final List<QueryTerm> mintermList = new LinkedList<QueryTerm>();
        mintermList.add(term);
        final List<List<QueryTerm>> ret = new LinkedList<List<QueryTerm>>();
        ret.add(mintermList);
        return ret;
    }
    public static List<List<QueryTerm>> createFlattenedList(QueryObject qobj) {
        List<List<QueryTerm>> flatList = new LinkedList<List<QueryTerm>>();
        try {
            final QueryConditionEnum condOp = QueryConditionEnum
                    .valueOf(qobj.getCondition());
            switch (condOp) {
            case OR:
                List<QueryEntity> termList = qobj.getRules();
                if (termList == null || termList.size() < 1) {
                    throw ErrorHandlerFactory
                            .getErrorHandler()
                            .createException(
                                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                                    "rules (null or empty)", "JSON object details");
                }
                for (QueryEntity qtn : termList) {
                        flatList.addAll(createFlattenedList(qtn));
                }
                break;
            case AND:
                termList = qobj.getRules();
                if (termList == null || termList.size() < 1) {
                    throw ErrorHandlerFactory
                            .getErrorHandler()
                            .createException(
                                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                                    "rules (null or empty)", "JSON object details");
                }
                for (QueryEntity qtn : termList) {
                    if (flatList.isEmpty()) {
                        flatList.addAll(createFlattenedList(qtn));
                    } else {
                        flatList = multiplyFlatLists(flatList,
                            createFlattenedList(qtn));
                    }
                }
                break;
            default:
                throw ErrorHandlerFactory.getErrorHandler().createException(
                        CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                        "condition:"+qobj.getCondition(), "JSON object details");
            }
        } catch (IllegalArgumentException iae) {
            throw ErrorHandlerFactory.getErrorHandler().createException(
                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                    "condition:"+qobj.getCondition(), "JSON object details");
        }
        return flatList;
    }

    private static List<List<QueryTerm>> multiplyFlatLists(
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

}
