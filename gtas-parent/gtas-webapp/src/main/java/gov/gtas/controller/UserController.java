/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.validator.UserDataValidator;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserController.class);

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
	public UserData createUser(@RequestBody @Valid UserData userData) {
		return userService.create(userData);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserData updateUser(@RequestBody @Valid UserData userData) {
		return userService.update(userData);
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping("/user")
	public UserData user(Principal principal) {
		return userService.findById(principal.getName());
	}

}
