/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import gov.gtas.services.Filter.FilterData;
@Component
public class FilterDataValidator implements Validator {
    

    private static final String USERID_REQUIRED = "UserId required .";

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterData.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FilterData filterDataRequest = (FilterData) target;

        ValidationUtils.rejectIfEmpty(errors, "userId", USERID_REQUIRED);
        
    }

}
