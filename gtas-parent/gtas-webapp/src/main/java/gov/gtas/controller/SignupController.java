package gov.gtas.controller;

import java.io.IOException;
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
import gov.gtas.model.SignupLocation;
import gov.gtas.repository.SignupLocationRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.SignupRequestService;
import gov.gtas.services.dto.SignupRequestDTO;

@RestController
public class SignupController {

	private final Logger logger = LoggerFactory.getLogger(SignupController.class);

	@Autowired
	private SignupRequestService signupRequestService;

	@Autowired
	private SignupLocationRepository signupLocationRepository;

	@PostMapping(value = "/user/signup/new")
	public ResponseEntity<Object> signup(@RequestBody @Valid SignupRequestDTO signupRequestDTO, BindingResult result) {

		if (result.hasErrors()) {
			List<String> errors = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
					.collect(Collectors.toList());
			return new ResponseEntity<>(errors, HttpStatus.OK);
		} else {

			// Check if a user already exists
			if (this.signupRequestService.signupRequestExists(signupRequestDTO)) {

				logger.debug("A sign up request with the same email or username already exists - {}",
						signupRequestDTO.getEmail());
				return new ResponseEntity<>(
						Collections.singletonList("A sign up request with the same email or email already exists"),
						HttpStatus.OK);
			} else {

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

				return new ResponseEntity<>(HttpStatus.OK);
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
	public ResponseEntity<Object> approveSignupRequest(@RequestBody @Valid SignupRequestDTO signupRequestDTO) {

		try {
			signupRequestService.approve(signupRequestDTO, GtasSecurityUtils.fetchLoggedInUserId());
		} catch (IOException | TemplateException | MessagingException e) {
			logger.error("Sign up approval failed", e);
		} catch (MailSendException e) {
			logger.error("Sening email failed", e);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/signupRequest/reject")
	public ResponseEntity<Object> rejectSignupRequest(@RequestBody @Valid SignupRequestDTO signupRequestDTO) {

		try {
			signupRequestService.reject(signupRequestDTO, GtasSecurityUtils.fetchLoggedInUserId());
		} catch (MessagingException | IOException | TemplateException e) {
			logger.error("Sign up rejection failed.", e);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
