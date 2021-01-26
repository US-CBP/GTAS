package gov.gtas.services.email;

import freemarker.template.TemplateException;
import gov.gtas.model.PasswordResetToken;
import gov.gtas.services.GtasEmailService;
import gov.gtas.services.dto.EmailDTO;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class UserEmailService {

    private static final String RESET_EMAIL_SUBJECT = "Unlock Instructions";

    @Value("${reset.link.url}")
    private String resetLinkUrlBase;
    
    @Value("${reset.password.link.url}")
    private String passwordResetLinkBase;

    @Autowired
    private EmailTemplateLoader emailTemplateLoader;

    @Autowired
    private GtasEmailService emailService;

    @Transactional
    public void sendAccountLockedResetEmail(String email, String resetToken) throws IOException, TemplateException, MessagingException, URISyntaxException {
        String resetLinkUrl = new URIBuilder(resetLinkUrlBase).addParameter("token", resetToken).build().toString();
        String htmlContent = emailTemplateLoader.generateAccountLockedResetPasswordHtmlContent(email, resetLinkUrl);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setSubject(RESET_EMAIL_SUBJECT);
        emailDTO.setTo(new String[] {email});
        emailDTO.setBody(htmlContent);

        emailService.sendHTMLEmail(emailDTO);
    }
    
    @Transactional
    public void sendPasswordResetEmail(String userId, String email, PasswordResetToken resetToken) throws IOException, TemplateException, MessagingException, URISyntaxException {
        String resetLinkUrl = new URIBuilder(passwordResetLinkBase + "/" + userId + "/" + resetToken.getToken()).build().toString();
        String htmlContent = emailTemplateLoader.forgotPasswordEmailHtmlString(resetLinkUrl, userId);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setSubject("Reset Your Password");
        emailDTO.setTo(new String[] {email});
        emailDTO.setBody(htmlContent);

        emailService.sendHTMLEmail(emailDTO);
    }

	public void sedUsername(String userEmail, String userId) throws IOException, TemplateException, MessagingException {
		EmailDTO emailDTO = new EmailDTO();
		 String emailBody = emailTemplateLoader.forgotUsernameEmailHtmlString(userId);
		 
		 emailDTO.setSubject("Recover User Id");
	     emailDTO.setTo(new String[] {userEmail});
	     emailDTO.setBody(emailBody);
		
	     emailService.sendHTMLEmail(emailDTO);
		
	}
    
}
