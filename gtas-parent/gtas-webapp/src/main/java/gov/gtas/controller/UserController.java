/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import javax.validation.Valid;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.UsernameRule;
import org.passay.WhitespaceRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.validator.UserDataValidator;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserDataValidator userDataValidator;

	@InitBinder("userData")
	protected void intializeUserDataValidator(WebDataBinder binder) {
		binder.addValidators(userDataValidator);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/users/")
	public List<UserData> getAllUsers() {
		return userService.findAll();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse createUser(@RequestBody @Valid UserData userData) {
		UserData rUserData = userData;
		Validator validator = this.new Validator();
		if (validator.isValid(userData.getPassword(), userData.getUserId())) {
			rUserData = userService.create(userData);
			return new JsonServiceResponse(Status.SUCCESS, validator.getErrMessage(), rUserData);
		} else {
			return new JsonServiceResponse(Status.FAILURE, validator.getErrMessage(), rUserData);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse updateUser(@RequestBody @Valid UserData userData) {

		UserData rUserData = userData;
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		boolean isAdmin = userService.isAdminUser(userId);
		if (!isAdmin) {
			if (!userId.equalsIgnoreCase(userData.getUserId())) {
				logger.error("The logged in user does not have a permission to update another user's credentials");
				return new JsonServiceResponse(Status.FAILURE, "Not Authorized to update user credentials", rUserData);
			}
		}

		Validator validator = this.new Validator();
		if (validator.isValid(userData.getPassword(), userData.getUserId())) {
			rUserData = userService.update(userData);
			logger.info("The User Information is updated sucessfully for " + userData.getUserId());
			return new JsonServiceResponse(Status.SUCCESS, validator.getErrMessage(), rUserData);
		} else {
			logger.error("The User Information is not updated due to errors for " + userData.getUserId() + " "
					+ validator.getErrMessage());
			return new JsonServiceResponse(Status.FAILURE, validator.getErrMessage(), rUserData);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/manageuser/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse manageuser(@RequestBody UserData userData) {

		UserData rUserData = userData;
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		boolean isAdmin = userService.isAdminUser(userId);
		if (!isAdmin) {
			logger.error("The logged in user does not have a permission to update another user's credentials");
			return new JsonServiceResponse(Status.FAILURE, "Not Authorized to update user credentials", rUserData);
		}

		Validator validator = this.new Validator();

		if (userData.getPassword() != null && !userData.getPassword().isEmpty()) {
			if (validator.isValid(userData.getPassword(), userData.getUserId())) {
				rUserData = userService.updateByAdmin(userData);
				logger.info("The User Information is updated sucessfully for " + userData.getUserId());
				return new JsonServiceResponse(Status.SUCCESS, validator.getErrMessage(), rUserData);
			} else {
				logger.error("The User Information is not updated due to errors for " + userData.getUserId() + " "
						+ validator.getErrMessage());
				return new JsonServiceResponse(Status.FAILURE, validator.getErrMessage(), rUserData);
			}
		}

		else {
			rUserData = userService.updateByAdmin(userData);
			logger.info("The User Information is updated sucessfully for " + userData.getUserId());
			return new JsonServiceResponse(Status.SUCCESS, validator.getErrMessage(), rUserData);
		}
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/user")
	public UserData user(Principal principal) {
		return userService.findById(principal.getName());
	}

	private class Validator {
		private String errMessage;
		boolean status = false;

		public boolean isValid(final String password, final String userName) {
			final PasswordValidator validator = new PasswordValidator(
					Arrays.asList(new LengthRule(10, 20), new CharacterRule(EnglishCharacterData.UpperCase, 1),
							new CharacterRule(EnglishCharacterData.LowerCase, 1), new UsernameRule(),
							new CharacterRule(EnglishCharacterData.Digit, 1),
							new CharacterRule(EnglishCharacterData.Special, 1), new WhitespaceRule()));
			PasswordData pd = new PasswordData(password);
			pd.setUsername(userName);
			final RuleResult result = validator.validate(pd);
			if (result.isValid()) {
				status = true;
			} else {
				StringJoiner sj = new StringJoiner("\n");
				for (String msg : validator.getMessages(result)) {
					sj.add(msg);
				}
				errMessage = sj.toString();
			}
			return status;
		}

		public String getErrMessage() {
			return errMessage;
		}
	}
}
