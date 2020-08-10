/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import gov.gtas.model.Role;
import gov.gtas.model.User;

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
	public List<UserDisplayData> getUserDataListFromEntityCollection(Iterable<User> userEntities) {

		Stream<User> aStream = StreamSupport.stream(userEntities.spliterator(), false);

		List<UserDisplayData> users = aStream.map(user -> {

			Set<RoleData> roles = user.getRoles().stream().map(new Function<Role, RoleData>() {

				@Override
				public RoleData apply(Role role) {
					return new RoleData(role.getRoleId(), role.getRoleDescription());
				}

			}).collect(Collectors.toSet());

			UserDisplayData userData = new UserDisplayData(user.getUserId().toUpperCase(), user.getFirstName(),
					user.getLastName(), user.getActive(), roles, user.getEmail(), user.getEmailEnabled(), user.getHighPriorityHitsEmailNotification());

			userData.setEmail(user.getEmail());
			return userData;
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
				entity.getActive(), roles, entity.getEmail(), entity.getEmailEnabled(), entity.getHighPriorityHitsEmailNotification(), entity.getArchived());
	}

	/**
	 * Map user entity from user data.
	 *
	 * @param userData
	 *            the user data
	 * @return the user
	 */
	public User mapUserEntityFromUserData(UserData userData) {

		Set<Role> roles = userData.getRoles().stream()
				.map(roleData -> new Role(roleData.getRoleId(), roleData.getRoleDescription()))
				.collect(Collectors.toSet());

		return new User(userData.getUserId().toUpperCase(), userData.getPassword(), userData.getFirstName(),
				userData.getLastName(), userData.getActive(), roles, userData.getEmail(), userData.getEmailEnabled(),
				userData.getHighPriorityEmail(), userData.getArchived());
	}

	/**
	 * Generates a ten-characters password;
	 * <p>
	 * A minimum of four lowercase characters, two uppercase characters, two digits
	 * and two special characters;
	 * <p>
	 * The password will meets the criteria of the validator at {@link UserController.Validator}
	 *
	 * @return a random password
	 */
	public String generateRandomPassword() {
		Stream<Character> passwordStream = Stream.concat(getRandomNumbersChars(2), Stream.concat(
				getRandomSpecialChars(2), Stream.concat(getRandomUpperCaseChars(2), getRandomLowerCaseChars(4))));

		List<Character> charList = passwordStream.collect(Collectors.toList());
		Collections.shuffle(charList);

		return charList.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
	}

	/**
	 * Returns a given {@code count} of special characters
	 *
	 * @param count
	 * @return
	 */
	private Stream<Character> getRandomSpecialChars(int count) {
		return this.specialChars(count, 33, 38);
	}

	/**
	 * Returns a given {@code count} of lowercase characters
	 *
	 * @param count
	 * @return
	 */
	private Stream<Character> getRandomLowerCaseChars(int count) {
		return this.specialChars(count, 97, 122);
	}

	/**
	 * Returns a given {@code count} of random uppercase characters
	 *
	 * @param count
	 * @return
	 */
	private Stream<Character> getRandomUpperCaseChars(int count) {
		return this.specialChars(count, 65, 90);
	}

	/**
	 * Returns a given {@code count} of random number characters
	 *
	 * @param count
	 * @return
	 */
	private Stream<Character> getRandomNumbersChars(int count) {
		return this.specialChars(count, 48, 57);
	}

	/**
	 * Returns a given {@code count} of characters
	 *
	 * @param count
	 * @param randomNumberOrigin
	 * @param randomNumberBound
	 * @return
	 */
	private Stream<Character> specialChars(int count, int randomNumberOrigin, int randomNumberBound) {
		Random random = new SecureRandom();
		IntStream characters = random.ints(count, randomNumberOrigin, randomNumberBound);
		return characters.mapToObj(data -> (char) data);
	}
}
