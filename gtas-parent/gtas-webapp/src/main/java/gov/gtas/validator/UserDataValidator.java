/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.validator;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;

@Component
public class UserDataValidator implements Validator {
    private static final String USERID_REQUIRED = "UserId required .";
    private static final String CRED_REQUIRED = "Password required.";
    private static final String FIRST_NAME_REQUIRED = "First Name required.";
    private static final String LAST_NAME_REQUIRED = "Last Name required.";
    private static final String ACTIVE_FLAG_REQUIRED = "Active Flag required.";
    private static final String ROLE_REQUIRED = "Roles Required";

    @Override
    public boolean supports(Class<?> clazz) {
        return UserData.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UserData userDataRequest = (UserData) target;
        Set<RoleData> roles = userDataRequest.getRoles();
        ValidationUtils.rejectIfEmpty(errors, "userId", USERID_REQUIRED);
        ValidationUtils.rejectIfEmpty(errors, "password", CRED_REQUIRED);
        ValidationUtils.rejectIfEmpty(errors, "firstName", FIRST_NAME_REQUIRED);
        ValidationUtils.rejectIfEmpty(errors, "lastName", LAST_NAME_REQUIRED);
        ValidationUtils.rejectIfEmpty(errors, "active", ACTIVE_FLAG_REQUIRED);
        if (roles == null)
            errors.rejectValue("roles", ROLE_REQUIRED);

    }

}
