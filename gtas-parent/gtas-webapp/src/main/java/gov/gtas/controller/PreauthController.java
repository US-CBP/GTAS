/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.enumtype.SignupRequestStatus;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.SignupLocation;
import gov.gtas.model.User;
import gov.gtas.repository.SignupLocationRepository;
import gov.gtas.services.SignupRequestService;
import gov.gtas.services.dto.SignupRequestDTO;
import gov.gtas.services.security.UserService;
import gov.gtas.services.TranslationService;
import gov.gtas.vo.TranslationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Preauth Controller handles requests that do not require authorization and which are made only from pages
 * outside the login. All urls beginning with "/api/preauth/" are excluded from spring security (See AppSecurityConfig.java).
 * 
 * Only those handlers serving the preauth pages should be placed here, currently:
 * forgotPassword
 * forgotUsername
 * signup
 * translations (GET only)
 */
@RestController
public class PreauthController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
	private UserService userService;

	@Autowired(required = false)
	private SignupLocationRepository signupLocationRepository;

	@Autowired(required = false)
	private SignupRequestService signupRequestService;

  @Autowired
  private TranslationService translationservice;

	@RequestMapping(method = RequestMethod.POST, value = "/api/preauth/forgotpassword")
	public JsonServiceResponse forgotPassword(@RequestParam String userId) {

		try {
			User user = userService.fetchUser(userId);
			userService.forgotPassword(user);

			return new JsonServiceResponse(Status.SUCCESS,
					"A temporary password has been sent to your email");
		} catch (Exception e) {
			return new JsonServiceResponse(Status.FAILURE,
					"The provided user ID (" + userId + ") is not on the system! ");
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/api/preauth/forgotusername")
	public JsonServiceResponse forgotUsername (@RequestParam String userEmail) {
		try {
			userService.forgotUsername(userEmail);

			return new JsonServiceResponse(Status.SUCCESS,
					"Your username has been sent to your email");
		} catch (Exception e) {
			return new JsonServiceResponse(Status.FAILURE,
					"The provided email (" + userEmail + ") is not on the system!");
		}
	}

	@PostMapping(value = "/api/preauth/signup")
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
				return new JsonServiceResponse(Status.FAILURE, "A sign up request with the same email or username already exists", signupRequestDTO);
			} else if (isExistingUser) {
				logger.debug("The username is already taken - {}",
						signupRequestDTO.getUsername());
				return new JsonServiceResponse(Status.FAILURE, "The username is already taken - " + signupRequestDTO.getUsername(), signupRequestDTO);

			} else {

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
				} catch (Exception e) {
					logger.error("Sending email notification to admin failed.", e);
				}

				return new JsonServiceResponse(Status.SUCCESS, "The request has been submitted");
			}
		}
	}
    
	@GetMapping(value = "/api/preauth/locations")
	public ResponseEntity<Object> getAllActivePhysicalLocations() {

		List<SignupLocation> physicalLocations = this.signupLocationRepository.findAllActiveSignupLocations();

		return new ResponseEntity<>(physicalLocations, HttpStatus.OK);
	}

  @GetMapping(value = "api/logout")
  public JsonServiceResponse logout(HttpSession session) {
    session.invalidate();
		return new JsonServiceResponse(Status.SUCCESS, "You have been logged out");
  }

  // Allows translation fetch for the unauthed pages without exposing other translation endpoints
  @RequestMapping(method = RequestMethod.GET, value = "/api/preauth/translation/{language}")
  public List<TranslationVo> getTranslationsByLang(@PathVariable String language) throws IOException {

    return translationservice.getTranslationsByLang(language);
  }

}

