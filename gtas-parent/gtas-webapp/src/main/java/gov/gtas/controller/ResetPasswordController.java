package gov.gtas.controller;

import dtos.PasswordResetDto;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;
import gov.gtas.services.security.LoginService;
import gov.gtas.validator.ResetPasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ResetPasswordController {

    private static final String SUCCESS_NOTIFICATION = "Password was successfully reset.";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ResetPasswordValidator resetPasswordValidator;

    @RequestMapping(method = RequestMethod.POST, value = "/password-reset")
    public JsonServiceResponse reset(@Valid @RequestBody PasswordResetDto dto, BindingResult bindingResult) {
        resetPasswordValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            return new JsonServiceResponse(Status.FAILURE, bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        User user = userRepository.findOne(dto.getUsername());
        user.setResetToken(null);
        user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));

        loginService.resetFailedLoginAttemptCount(user.getUserId());
        userRepository.save(user);

        return new JsonServiceResponse(Status.SUCCESS, SUCCESS_NOTIFICATION);
    }
}
