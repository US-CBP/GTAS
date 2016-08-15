/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import gov.gtas.model.User;

import java.util.List;

public interface UserService {
	public UserData create(UserData user);

	public void delete(String id);

	public List<UserData> findAll();

	public UserData update(UserData user);

	public UserData findById(String id);

	public User fetchUser(final String userId);
}
