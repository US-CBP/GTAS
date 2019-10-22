/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import gov.gtas.model.Role;
import gov.gtas.model.User;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

/**
 * The Class UserServiceUtil.
 */
@Component
public class UserServiceUtil {

	/**
	 * Gets the user data list from entity collection.
	 *
	 * @param userEntities
	 *            the user entities
	 * @return the user data list from entity collection
	 */
	public List<UserData> getUserDataListFromEntityCollection(Iterable<User> userEntities) {

		Stream<User> aStream = StreamSupport.stream(userEntities.spliterator(), false);

		List<UserData> users = (List<UserData>) aStream.map(new Function<User, UserData>() {

			@Override
			public UserData apply(User user) {

				Set<RoleData> roles = user.getRoles().stream().map(new Function<Role, RoleData>() {

					@Override
					public RoleData apply(Role role) {
						return new RoleData(role.getRoleId(), role.getRoleDescription());
					}

				}).collect(Collectors.toSet());
				UserData userData = new UserData(user.getUserId().toUpperCase(), user.getPassword(), user.getFirstName(),
						user.getLastName(), user.getActive(), roles);
				userData.setEmail(user.getEmail());
				return userData;
			}
		}).collect(Collectors.toList());
		aStream.close();
		return users;
	}

	/**
	 * Map user data from entity.
	 *
	 * @param entity
	 *            the entity
	 * @return the user data
	 */
	public UserData mapUserDataFromEntity(User entity) {
		Set<RoleData> roles = entity.getRoles().stream()
				.map(role -> new RoleData(role.getRoleId(), role.getRoleDescription())).collect(Collectors.toSet());
		return new UserData(entity.getUserId().toUpperCase(), entity.getPassword(), entity.getFirstName(), entity.getLastName(),
				entity.getActive(), roles);
	}

	/**
	 * Map user entity from user data.
	 *
	 * @param userData
	 *            the user data
	 * @return the user
	 */
	public User mapUserEntityFromUserData(UserData userData) {

		Set<Role> roles = userData.getRoles().stream().map(roleData -> new Role(roleData.getRoleId(), roleData.getRoleDescription())).collect(Collectors.toSet());

		return new User(userData.getUserId().toUpperCase(), userData.getPassword(), userData.getFirstName(), userData.getLastName(),
				userData.getActive(), roles);
	}
}
