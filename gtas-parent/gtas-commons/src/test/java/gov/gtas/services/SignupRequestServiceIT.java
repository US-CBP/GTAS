package gov.gtas.services;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import freemarker.template.TemplateException;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.enumtype.SignupRequestStatus;
import gov.gtas.model.SignupRequest;
import gov.gtas.services.dto.SignupRequestDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@Rollback
public class SignupRequestServiceIT {

	@Autowired
	private SignupRequestService signupRequestService;

	@Test
	public void testCreateNewSignupRequest() {
		SignupRequestDTO signupRequestDTO = new SignupRequestDTO();
		signupRequestDTO.setEmail("tsegay.gtas@outlook.com");
		signupRequestDTO.setUsername("testusername");
		signupRequestDTO.setPhysicalLocation("test");
		signupRequestDTO.setSupervisor("John");
		signupRequestDTO.setStatus(SignupRequestStatus.NEW);
		SignupRequest request = this.signupRequestService.save(signupRequestDTO);
		assertEquals(SignupRequestStatus.NEW, request.getStatus());
	}

	@Test
	public void testSignupRequestConfirmationEmail() throws MessagingException, IOException, TemplateException {
		SignupRequestDTO signupRequestDTO = new SignupRequestDTO();
		signupRequestDTO.setEmail("tsegay.gtas@outlook.com");
		signupRequestDTO.setUsername("testusername");
		signupRequestDTO.setStatus(SignupRequestStatus.NEW);
		this.signupRequestService.sendConfirmationEmail(signupRequestDTO);
	}

	@Test
	public void testSignupRequestNotificationEmailToAdmin() throws MessagingException, IOException, TemplateException {
		SignupRequestDTO signupRequestDTO = new SignupRequestDTO();
		signupRequestDTO.setEmail("tsegay.gtas@outlook.com");
		signupRequestDTO.setUsername("testusername");
		signupRequestDTO.setPhysicalLocation("tsst");
		signupRequestDTO.setSupervisor("John Doe");
		signupRequestDTO.setStatus(SignupRequestStatus.NEW);
		this.signupRequestService.sendEmailNotificationToAdmin(signupRequestDTO);
	}
}
