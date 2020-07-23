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

import dtos.PasswordChangeDTO;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserDisplayData;
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
	public List<UserDisplayData> getAllUsers() {
		return userService.findAll();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/users/nonarchived")
	public List<UserDisplayData> getAllNonArchivedUsers() {
		return userService.findAllNonArchivedUsers();
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
	
	@PutMapping(value="user/change-password")
	public JsonServiceResponse changePassword(@RequestBody PasswordChangeDTO passChangeDto) {
		
		String userId = GtasSecurityUtils.fetchLoggedInUserId(); 
		UserData user = userService.findById(userId);
		Validator validator = this.new Validator();
		String savedPassword = user.getPassword();
		String oldPassword = passChangeDto.getOldPassword();
		String newPassword = passChangeDto.getNewPassword();
		String confirmPassword = passChangeDto.getConfirmPassword();
		
		if (newPassword == null ) {
			return new JsonServiceResponse(Status.FAILURE, "Invalid Password! Password cannot be null.", passChangeDto);
		}
		if (newPassword.equals(oldPassword)) {
			return new JsonServiceResponse(Status.FAILURE, "Please choose password different from the current one!", passChangeDto);
		}
		
		if (!newPassword.equals(confirmPassword)) {
			return new JsonServiceResponse(Status.FAILURE, "The passwords you entered do not match!", passChangeDto);
		}
		
		if (!userService.matchUserPassword(savedPassword, oldPassword)) {
			return new JsonServiceResponse(Status.FAILURE, "Your current password is not correct. Please enter the correct password!", passChangeDto);
		}
		
		if (!validator.isValid(passChangeDto.getNewPassword(), userId)) {
			return new JsonServiceResponse(Status.FAILURE, validator.getErrMessage(), passChangeDto);
		}
		
		//change password
		user.setPassword(newPassword);
		userService.update(user);
		
		return new JsonServiceResponse(Status.SUCCESS, "Your password has successfully been changed!");
		
		
	}
	@ResponseBody
	@RequestMapping(method = RequestMethod.DELETE, value = "/users/{id}")
	public JsonServiceResponse deleteOrArchiveUser(@PathVariable(value = "id") String paxId){
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		boolean isAdmin = userService.isAdminUser(userId);
		if (!isAdmin) {
			logger.error("The logged in user does not have a permission to delete another user");
			return new JsonServiceResponse(Status.FAILURE, "Not Authorized to delete the user with that ID", paxId);
		}
		//Make attempt to delete, due to constraints potentially on our Users and wanting to preserve history of those
		//constrained elements user will become archived instead
		try {
			userService.delete(paxId);
		} catch (Exception e){ //TODO change to appropriate exception
			logger.info("The User with id " +paxId+" was unable to be deleted. Attempting to archive instead");
			UserData tmpUser = userService.findById(paxId);
			tmpUser.setArchived(Boolean.TRUE);
			userService.updateByAdmin(tmpUser);
			return new JsonServiceResponse(Status.SUCCESS_WITH_WARNING, "Failed To Delete User, Archived User Instead");
		}
		return new JsonServiceResponse(Status.SUCCESS, "Successfully Deleted User", paxId);
	};

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
