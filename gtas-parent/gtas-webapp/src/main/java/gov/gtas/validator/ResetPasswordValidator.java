package gov.gtas.validator;

import dtos.PasswordResetDto;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ResetPasswordValidator implements Validator {

    public static final String INVALID_CREDENTIALS_REQUEST_ERROR = "Invalid request. Please try again.";
    public static final String MATCHING_PWORDS_ERROR = "The confirmation password does not match the new password entered.";
    public static final String USER_NAME = "username";

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(Class clazz) {
        return PasswordResetDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors e) {
        PasswordResetDto dto = (PasswordResetDto) target;

        User user = userRepository.findOne(dto.getUsername());

        if(user == null) {
            e.reject(USER_NAME, INVALID_CREDENTIALS_REQUEST_ERROR);
            return;
        }

        if(!StringUtils.equals(user.getResetToken(), dto.getResetToken())
                || StringUtils.equals(dto.getPassword(), user.getPassword())) {
            e.reject(USER_NAME, INVALID_CREDENTIALS_REQUEST_ERROR);
        }

        String[] errors = PasswordValidator.validate(dto.getPassword());
        if(errors.length > 0) {
            e.reject(USER_NAME, errors[0]);
        }

        if(!StringUtils.equals(dto.getPassword(), dto.getPasswordConfirm())) {
            e.reject(USER_NAME, MATCHING_PWORDS_ERROR);
        }

    }

}