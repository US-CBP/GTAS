package gov.gtas.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import freemarker.template.TemplateException;
import gov.gtas.enumtype.SignupRequestStatus;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.SignupLocation;
import gov.gtas.repository.SignupLocationRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.SignupRequestService;
import gov.gtas.services.dto.SignupRequestDTO;
import gov.gtas.services.security.UserService;

@RestController
public class SignupController {

	private final Logger logger = LoggerFactory.getLogger(SignupController.class);

	@Autowired
	private SignupRequestService signupRequestService;

	@Autowired
	private SignupLocationRepository signupLocationRepository;
	
	@Autowired
	private UserService userService;

	@PostMapping(value = "/user/signup/new")
	public JsonServiceResponse signup(@RequestBody @Valid SignupRequestDTO signupRequestDTO, BindingResult result) {

		if (result.hasErrors()) {
			List<String> errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
					.collect(Collectors.toList());
			return new JsonServiceResponse(Status.FAILURE, Arrays.toString(errors.toArray()), signupRequestDTO);
		} else {

			// Check if a user already exists
			boolean isExistingUser = userService.findById(signupRequestDTO.getUsername()) != null;
			boolean isExistingRequest = this.signupRequestService.signupRequestExists(signupRequestDTO);
			if (isExistingRequest) {

				logger.debug("A sign up request with the same email or username already exists - {}",
						signupRequestDTO.getEmail());
				return new JsonServiceResponse(Status.FAILURE,"A sign up request with the same email or email already exists", signupRequestDTO);
			} 
			else if (isExistingUser) {
				logger.debug("The username is already taken - {}",
						signupRequestDTO.getUsername());
				return new JsonServiceResponse(Status.FAILURE, "The username is already taken - " + signupRequestDTO.getUsername(), signupRequestDTO);
				
			}else {

				signupRequestDTO.setStatus(SignupRequestStatus.NEW);
				logger.debug("persisting sign up request");
				signupRequestService.save(signupRequestDTO);

				try {
					logger.debug("sending confirmation email to {}", signupRequestDTO.getEmail());
					signupRequestService.sendConfirmationEmail(signupRequestDTO);
				} catch (MessagingException | IOException | TemplateException e) {
					logger.error("Sending sign up confirmation email failed.", e);
				} catch (Exception e) {
					logger.error("Failed sending email messages");
				}

				try {
					logger.debug("sending notification email to admin");
					signupRequestService.sendEmailNotificationToAdmin(signupRequestDTO);
				} catch (MessagingException | IOException | TemplateException e) {
					logger.error("Sending email notification to admin failed.", e);
				} catch (Exception e) {
					logger.error("Failed sending email messages");
				}

				return new JsonServiceResponse(Status.SUCCESS, "The request has been submited");
			}
		}

	}

	@GetMapping(value = "/user/signup/physiclLocations")
	public ResponseEntity<Object> getAllActivePhysicalLocations() {

		List<SignupLocation> physicalLocations = this.signupLocationRepository.findAllActiveSignupLocations();

		return new ResponseEntity<>(physicalLocations, HttpStatus.OK);
	}

	@GetMapping(value = "/user/allNewSignupRequests")
	public ResponseEntity<Object> signupRequests() {

		List<SignupRequestDTO> requests = this.signupRequestService.getAllNewSignupRequests();

		return new ResponseEntity<>(requests, HttpStatus.OK);
	}

	@PostMapping(value = "/signupRequest/approve")
	public JsonServiceResponse approveSignupRequest(@RequestBody @Valid SignupRequestDTO signupRequestDTO) {

		try {
			signupRequestService.approve(signupRequestDTO, GtasSecurityUtils.fetchLoggedInUserId());
			
			String message = signupRequestDTO.getUsername() + "'s request is approved!";
			return new JsonServiceResponse(Status.SUCCESS, message);
			
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error("Sign up approval failed", e);	
			
			String message = "Something went wrong when approving a request from username: " + signupRequestDTO.getUsername();
			return new JsonServiceResponse(Status.FAILURE, message);
			
		} catch (MailSendException e) {
			logger.error("Sending email failed", e);
			
			String message = "Could not send approval email to: " + signupRequestDTO.getUsername();
			return new JsonServiceResponse(Status.FAILURE, message);
		}

	}

	@PostMapping(value = "/signupRequest/reject")
	public JsonServiceResponse rejectSignupRequest(@RequestBody @Valid SignupRequestDTO signupRequestDTO) {

		try {
			signupRequestService.reject(signupRequestDTO, GtasSecurityUtils.fetchLoggedInUserId());
			
			String message = signupRequestDTO.getUsername() + "'s request is rejected!";
			return new JsonServiceResponse(Status.SUCCESS, message);
			
		} catch (MessagingException | IOException | TemplateException e) {
			logger.error("Sign up rejection failed.", e);
			
			String message = "Something went wrong when rejecting a request from username: " + signupRequestDTO.getUsername();
			return new JsonServiceResponse(Status.FAILURE, message);
		}

		
	}
}
