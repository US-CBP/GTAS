/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.watchlist.json.validation;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.WatchlistConstants;
import gov.gtas.enumtype.ConditionEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.error.CommonValidationException;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.querybuilder.validation.util.QueryValidationUtils;

/**
 * An Adapter class that uses QueryValidationUtils to validate watch list JSON
 * objects.
 */
public class WatchlistValidationAdapter {
    public static void validateWatchlistSpec(WatchlistSpec wljson) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(
                wljson, "watch list");
        if (StringUtils.isEmpty(wljson.getName())) {
            errors.rejectValue(WatchlistConstants.WL_NAME_FIELD,
                    CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
                    "Watch list name or entity name is missing.");
        }
        try {
            EntityEnum.getEnum(wljson.getEntity());
        } catch (IllegalArgumentException iae) {
            errors.rejectValue(WatchlistConstants.WL_ENTITY_FIELD,
                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                    "Watch list entity name is missing or invalid.");
        }
        if (CollectionUtils.isEmpty(wljson.getWatchlistItems())) {
            errors.rejectValue("watchlistItems",
                    CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
                    "Watch list does not contain any items.");
        }
        if (errors.hasErrors()) {
            createAndThrowValidationError(errors);
        }
        for (WatchlistItemSpec itm : wljson.getWatchlistItems()) {
            errors = new BeanPropertyBindingResult(itm, "watch list item");
            WatchlistEditEnum action = validateAction(itm, errors);
            if (itm.getTerms() == null && action != null && action != WatchlistEditEnum.D) {
                throw ErrorHandlerFactory.getErrorHandler().createException(
                        CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
                        "Watch list rule items", action.getOperationName());
            } else if(itm.getTerms() != null){
                QueryObject wlObj = new QueryObject();
                wlObj.setCondition(ConditionEnum.AND.toString());
                List<QueryEntity> terms = new LinkedList<QueryEntity>();
                for (WatchlistTerm trm : itm.getTerms()) {
                    QueryTerm t = new QueryTerm(wljson.getEntity(), trm.getField(),
                            trm.getType(), CriteriaOperatorEnum.EQUAL.toString(),
                            new String[] { trm.getValue() });
                    terms.add(t);
                }
                wlObj.setRules(terms);
                Errors err = QueryValidationUtils.validateQueryObject(wlObj);
                if (err.hasErrors()) {
                    createAndThrowValidationError(err);
                }
            }
        }
    }

    private static WatchlistEditEnum validateAction(WatchlistItemSpec itm,
            Errors errors) {
        WatchlistEditEnum action = null;
        try {
            action = WatchlistEditEnum.getEditEnumForOperationName(itm
                    .getAction());
            switch (action) {
            case C:
                if (itm.getId() != null) {
                    errors.rejectValue(
                            "id",
                            CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                            "Cannot specify id for create action - "
                                    + itm.getId());
                }
                break;
            case U:
            case D:
                if (itm.getId() == null) {
                    errors.rejectValue("id",
                            CommonErrorConstants.NULL_ARGUMENT_ERROR_CODE,
                            "No id specified for update or delete action");
                }
                break;
            }
        } catch (IllegalArgumentException iae) {
            errors.rejectValue("action",
                    CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE,
                    "Invalid action for watch list item:" + itm.getAction());
        }
        if (errors.hasErrors()) {
            createAndThrowValidationError(errors);
        }

        return action;
    }

    private static void createAndThrowValidationError(Errors error) {
        throw new CommonValidationException("Watch list JSON has Errors", error);
    }
}
