package gov.gtas.controller;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;
import gov.gtas.services.security.UserService;
import gov.gtas.validator.PasswordValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
public class ResetController {

    private static final String INVALID_CREDENTIALS_REQUEST_ERROR = "Invalid request. Please try again.";
    private static final String MATCHING_PASSWORDS_ERROR = "The confirmation password does not match the new password entered.";
    private static final String SUCCESS_NOTIFICATION = "Password was successfully reset.";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, value = "/password-reset")
    public JsonServiceResponse reset(@RequestParam String username, @RequestParam String password,
                              @RequestParam String confirmPassword, @RequestParam String resetToken) {

        if(isBlank(username)) {
            return new JsonServiceResponse(Status.FAILURE, INVALID_CREDENTIALS_REQUEST_ERROR);
        }

        User user = userRepository.findOne(username);
       if(user == null
               || isBlank(resetToken)
               || !StringUtils.equals(user.getResetToken(), resetToken)
               || StringUtils.equals(password, user.getPassword())) {
           return new JsonServiceResponse(Status.FAILURE, INVALID_CREDENTIALS_REQUEST_ERROR);
       }

       String[] errors = PasswordValidator.validate(password);
       if(errors.length > 0) {
           return new JsonServiceResponse(Status.FAILURE, errors[0]);
       }

       if(isBlank(confirmPassword)
               || !StringUtils.equals(password, confirmPassword)) {
           return new JsonServiceResponse(Status.FAILURE, MATCHING_PASSWORDS_ERROR);
       }

       user.setResetToken(null);
       user.setPassword(new BCryptPasswordEncoder().encode(password));
       userService.resetFailedLoginAttemptCount(user.getUserId());
       userRepository.save(user);

       return new JsonServiceResponse(Status.SUCCESS, SUCCESS_NOTIFICATION);
    }
}
