package gov.gtas.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import freemarker.template.TemplateException;
import gov.gtas.model.SignupRequest;
import gov.gtas.services.dto.SignupRequestDTO;
import gov.gtas.services.security.RoleData;

public interface SignupRequestService {
	
	SignupRequest save(SignupRequestDTO signupRequest);
	
	SignupRequest findById(Long id);
	
	Boolean signupRequestExists(SignupRequestDTO signupRequest);
	
	void sendConfirmationEmail(SignupRequestDTO signupRequestDTO) throws MessagingException, IOException, TemplateException;
	
	void sendEmailNotificationToAdmin(SignupRequestDTO signupRequestDTO) throws MessagingException, IOException, TemplateException;
		
	List<SignupRequestDTO> getAllNewSignupRequests();
	
	List<SignupRequestDTO> search(Map<String, Object> queryParameters);

	void approve(Long requestId, Set<RoleData> roles, String approvedBy) throws IOException, TemplateException, MessagingException;

	void reject(Long requestId, String rejectedBy) throws MessagingException, IOException, TemplateException;
}
