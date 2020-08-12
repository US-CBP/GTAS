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
import java.util.stream.Collectors;

import javax.validation.Valid;

import gov.gtas.services.security.RoleData;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import dtos.PasswordChangeDTO;
import dtos.PasswordResetDto;
import gov.gtas.email.dto.UserEmailDTO;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.User;
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

	@GetMapping(value = "/users/emails")
	public List<UserEmailDTO> getAllUsersEmail() {
		List<UserEmailDTO> usersEmails = userService.findAll().stream().filter(user -> user.getEmail() != null)
				.map(this::fetchUsernameAndEmail).collect(Collectors.toList());

		return usersEmails;
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

		// Prevent user from disabling themselves OR removing admin role from themselves
		if (userId.equals(userData.getUserId())) {
			if (userData.getActive() == 0) {
				return new JsonServiceResponse(Status.FAILURE,
						"Not Authorized: Logged In User may not disable  themsel");
			}
			Boolean retainsAdmin = false;
			for (RoleData role : userData.getRoles()) {
				if (role.getRoleId() == 1) {
					retainsAdmin = true;
				}
			}
			if (!retainsAdmin) {
				return new JsonServiceResponse(Status.FAILURE,
						"Not Authorized: Logged In User may not remove Admin role from themselves");
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

		// Prevent user from disabling themselves OR removing admin role from themselves
		if (userId.equals(userData.getUserId())) {
			if (userData.getActive() == 0) {
				return new JsonServiceResponse(Status.FAILURE,
						"Not Authorized: Logged In User may not disable themselves");
			}
			Boolean retainsAdmin = false;
			for (RoleData role : userData.getRoles()) {
				if (role.getRoleId() == 1) {
					retainsAdmin = true;
				}
			}
			if (!retainsAdmin) {
				return new JsonServiceResponse(Status.FAILURE,
						"Not Authorized: Logged In User may not remove Admin role from themselves");
			}
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

	@PutMapping(value = "user/change-password")
	public JsonServiceResponse changePassword(@RequestBody PasswordChangeDTO passChangeDto) {

		String loggedinUserId = GtasSecurityUtils.fetchLoggedInUserId();
		UserData loggedinUser = userService.findById(loggedinUserId);
		String savedPassword = loggedinUser.getPassword();

		return changePassword(loggedinUser, passChangeDto, savedPassword);

	}

	@PutMapping(value = "user/change-password/{userId}")
	public JsonServiceResponse changePasswordByAdmin(@RequestBody PasswordChangeDTO passChangeDto,
			@PathVariable(value = "userId") String userId) {
		UserData user = userService.findById(userId);

		return changePassword(user, passChangeDto, null);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/forgot-password")
	public JsonServiceResponse forgotPassword(@RequestParam String userId) {

		try {
			User user = userService.fetchUser(userId);
			userService.forgotPassword(user);

			return new JsonServiceResponse(Status.SUCCESS,
					"A temporary  password has been sent to your email. Please use your temporary password to access your account!");

		} catch (Exception e) {
			return new JsonServiceResponse(Status.FAILURE,
					"The provided user ID (" + userId + ") is not on the system!");

		}

	}

	@RequestMapping(method = RequestMethod.GET, value = "/reset-password")
	public JsonServiceResponse isValidToken(@RequestParam String token) {
		boolean isValidToken = userService.isValidToken(token);

		if (isValidToken) {
			return new JsonServiceResponse(Status.SUCCESS, "Valid token provided");
		}

		return new JsonServiceResponse(Status.FAILURE, "Ivalid token provided");

	}

	@RequestMapping(method = RequestMethod.POST, value = "/reset-password")
	public JsonServiceResponse resetPassword(@Valid @RequestBody PasswordResetDto dto, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {

			String errorMessage = bindingResult.getFieldErrors().stream().map(error -> error.getDefaultMessage())
					.collect(Collectors.joining(",  "));

			return new JsonServiceResponse(Status.FAILURE, errorMessage);
		}

		UserData user = userService.findById(dto.getUsername());
		Validator validator = this.new Validator();

		if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
			return new JsonServiceResponse(Status.FAILURE, "The passwords you entered do not match!", dto);
		}
		if (!validator.isValid(dto.getPassword(), dto.getUsername())) {
			return new JsonServiceResponse(Status.FAILURE, validator.getErrMessage(), dto);
		}

		user.setPassword(dto.getPassword());
		userService.update(user);

		return new JsonServiceResponse(Status.SUCCESS, "Your password has been reset!");

	}

	private UserEmailDTO fetchUsernameAndEmail(UserDisplayData user) {
		UserEmailDTO dto = new UserEmailDTO();
		dto.setEmail(user.getEmail());
		dto.setUsername(user.getUserId());

		return dto;
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.DELETE, value = "/users/{id}")
	public JsonServiceResponse deleteOrArchiveUser(@PathVariable(value = "id") String paxId) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		if (userId.equals(paxId)) {
			return new JsonServiceResponse(Status.FAILURE, "Not Authorized to delete the user with that ID. "
					+ "User ID is the same as the currently logged in user", paxId);
		}
		boolean isAdmin = userService.isAdminUser(userId);
		if (!isAdmin) {
			logger.error("The logged in user does not have a permission to delete another user");
			return new JsonServiceResponse(Status.FAILURE, "Not Authorized to delete the user with that ID", paxId);
		}
		// Make attempt to delete, due to constraints potentially on our Users and
		// wanting to preserve history of those
		// constrained elements user will become archived instead
		try {
			userService.delete(paxId);
			logger.info("The User with Id " + paxId + " was successfully deleted.");
		} catch (Exception e) { // TODO change to appropriate exception
			logger.info("The User with id " + paxId + " was unable to be deleted. Attempting to archive instead");
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

	private JsonServiceResponse changePassword(UserData user, PasswordChangeDTO passChangeDto, String savedPassword) {

		Validator validator = this.new Validator();
		String newPassword = passChangeDto.getNewPassword();
		String confirmPassword = passChangeDto.getConfirmPassword();
		String oldPassword = passChangeDto.getOldPassword();

		if (newPassword == null) {
			return new JsonServiceResponse(Status.FAILURE, "Invalid Password! Password cannot be null.", passChangeDto);
		}

		if (newPassword.equals(oldPassword)) {// if oldPassword is provided (users change their own password)
			return new JsonServiceResponse(Status.FAILURE, "Please choose password different from the current one!",
					passChangeDto);
		}

		if (!newPassword.equals(confirmPassword)) {
			return new JsonServiceResponse(Status.FAILURE, "The passwords you entered do not match!", passChangeDto);
		}

		// applies only when users change their own password
		if (savedPassword != null && !userService.matchUserPassword(savedPassword, oldPassword)) {
			return new JsonServiceResponse(Status.FAILURE,
					"Your current password is not correct. Please enter the correct password!", passChangeDto);
		}

		if (!validator.isValid(passChangeDto.getNewPassword(), user.getUserId())) {
			return new JsonServiceResponse(Status.FAILURE, validator.getErrMessage(), passChangeDto);
		}

		// change password
		user.setPassword(newPassword);
		userService.update(user);

		return new JsonServiceResponse(Status.SUCCESS, user.getUserId() + "'s password has successfully been changed!");
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
