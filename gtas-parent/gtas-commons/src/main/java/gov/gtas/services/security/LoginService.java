package gov.gtas.services.security;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.mail.MessagingException;

import freemarker.template.TemplateException;

public interface LoginService {

    void resetFailedLoginAttemptCount(String userId);

    void addToFailAttempts(String userId) throws IOException, TemplateException, MessagingException, URISyntaxException;

    Integer getUserLoginAttempts(String userId);

}
