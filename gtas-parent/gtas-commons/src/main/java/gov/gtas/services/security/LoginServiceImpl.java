package gov.gtas.services.security;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.email.ResetPasswordEmailService;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private UserService userService;

    @Resource
    private ResetPasswordEmailService resetPasswordEmailService;

    @Override
    @Transactional
    public 	void resetFailedLoginAttemptCount(final String userId) {
        validateUser(userId);
        User user = userRepository.findOne(userId);
        user.setConsecutiveFailedLoginAttempts(0);

    }

    @Override
    @Transactional
    public void addToFailAttempts(final String userId) {
        validateUser(userId);
        User user = userRepository.findOne(userId);
        if(user.getConsecutiveFailedLoginAttempts() == null) {
            user.setConsecutiveFailedLoginAttempts(1);
        } else if(user.getConsecutiveFailedLoginAttempts() == 4) {
            sendPasswordResetEmail(user);
            user.setConsecutiveFailedLoginAttempts(user.getConsecutiveFailedLoginAttempts() + 1);
        } else {
            user.setConsecutiveFailedLoginAttempts(user.getConsecutiveFailedLoginAttempts() + 1);
        }

    }

    @Override
    @Transactional
    public Integer getUserLoginAttempts(final String userId) {
        validateUser(userId);
        User user = userRepository.findOne(userId);
        return user.getConsecutiveFailedLoginAttempts();
    }

    private void sendPasswordResetEmail(User user) {
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        try {
            resetPasswordEmailService.sendAccountLockedResetEmail(user.getEmail(), resetToken);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void validateUser(final String userId) {
        UserData userData = userService.findById(userId);
        if (userData == null) {
            throw ErrorHandlerFactory
                    .getErrorHandler()
                    .createException(CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
        }

    }
}
