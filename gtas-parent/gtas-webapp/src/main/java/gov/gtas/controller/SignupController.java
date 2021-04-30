package gov.gtas.controller;

import gov.gtas.enumtype.SignupRequestStatus;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.SignupLocation;
import gov.gtas.repository.SignupLocationRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.SignupRequestService;
import gov.gtas.services.dto.SignupRequestAprroveDTO;
import gov.gtas.services.dto.SignupRequestDTO;
import gov.gtas.services.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@ConditionalOnProperty(prefix = "enable.email.notification", name = "service")
public class SignupController {

	private final Logger logger = LoggerFactory.getLogger(SignupController.class);

	@Autowired()
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
				return new JsonServiceResponse(Status.FAILURE,"A sign up request with the same email or username already exists", signupRequestDTO);
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
				} catch (Exception e) {
					String message = "Failed sending sign up confirmation email to:  " + signupRequestDTO.getEmail();
					logger.error(message, e);
				} 

				try {
					logger.debug("sending notification email to admin");
					signupRequestService.sendEmailNotificationToAdmin(signupRequestDTO);
				} catch (Exception  e) {
					logger.error("Sending email notification to admin failed.", e);
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
	
	@GetMapping(value = "/api/signup-requests")
	public ResponseEntity<Object> getSignupRequests(@RequestParam Map<String, Object> params) {
		
		String status = (String) params.get("status");
		String locationName = (String) params.get("location");
		SignupLocation location = signupLocationRepository.findLocationByName(locationName);
		
		if (location != null) {
			Long signupLocationId = location.getId();
			params.put("signupLocationId", signupLocationId);
			params.remove("location");
		}
		
		
		if (status != null) {
			SignupRequestStatus statusEnum = SignupRequestStatus.valueOf(status);
			params.put("status", statusEnum);
		}
		
		List<SignupRequestDTO> requests = this.signupRequestService.search(params);
		return new ResponseEntity<>(requests, HttpStatus.OK);
		
		
	}

	@GetMapping(value = "/user/allNewSignupRequests")
	public ResponseEntity<Object> signupRequests() {

		List<SignupRequestDTO> requests = this.signupRequestService.getAllNewSignupRequests();

		return new ResponseEntity<>(requests, HttpStatus.OK);
	}

	@PostMapping(value = "/signupRequest/approve")
	public JsonServiceResponse approveSignupRequest(@RequestBody SignupRequestAprroveDTO request) {

		try {
			signupRequestService.approve(request.getRequestId(), request.getRoles(), GtasSecurityUtils.fetchLoggedInUserId());
			
			String message =  "Request with Id (" + request.getRequestId() + ") has been approved!";
			return new JsonServiceResponse(Status.SUCCESS, message);
			
		}  catch (MailSendException e) {
			logger.error("Sending email failed", e);
			
			String message = "Failed to send approval email to the user";
			return new JsonServiceResponse(Status.FAILURE, message);
			
		}catch (Exception  e) {
			logger.error("Sign up approval failed", e);	
			
			String message = "Something went wrong when approving a request from request id: " + request.getRequestId();
			return new JsonServiceResponse(Status.FAILURE, message);
			
		}
		

	}

	@PutMapping(value = "/signupRequest/reject/{requestId}")
	public JsonServiceResponse rejectSignupRequest(@PathVariable Long requestId) {

		try {
			signupRequestService.reject(requestId, GtasSecurityUtils.fetchLoggedInUserId());
			
			String message =  "Request with Id (" + requestId + ") has been rejected!";
			return new JsonServiceResponse(Status.SUCCESS, message);
			
		} catch (Exception e) {
			logger.error("Sign up rejection failed.", e);
			
			String message = "Something went wrong when rejecting a request from request Id: " + requestId;
			return new JsonServiceResponse(Status.FAILURE, message);
		}

		
	}
	
}
