/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import gov.gtas.model.User;

import java.util.List;

/**
 * The Interface UserService.
 */
public interface UserService {
	
	/**
	 * Creates the User.
	 *
	 * @param userData the user data
	 * @return the user data
	 */
	public UserData create(UserData user);

	/**
	 * Delete.
	 *
	 * @param id the user id
	 */
	public void delete(String id);

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	public List<UserData> findAll();

	/**
	 * Update the user.
	 *
	 * @param userData the user data
	 * @return the updated user data
	 */
	public UserData update(UserData user);

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the user data
	 */
	public UserData findById(String id);

	/**
	 * Fetch user.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	public User fetchUser(final String userId);
}
