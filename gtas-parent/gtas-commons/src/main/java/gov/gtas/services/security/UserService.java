/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;

import gov.gtas.constant.GtasSecurityConstants;
import gov.gtas.model.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import gov.gtas.model.UserGroup;
import javassist.NotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;

import freemarker.template.TemplateException;

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
	@PreAuthorize(PRIVILEGE_ADMIN)
	UserData create(UserData user);

	/**
	 * Delete.
	 *
	 * @param id the user id
	 */
	@PreAuthorize(PRIVILEGE_ADMIN)
	void delete(String id);

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@PreAuthorize(GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)

	public List<UserDisplayData> findAll();

	/**
	 * Update the user.
	 *
	 * @param userData the user data
	 * @return the updated user data
	 */

	UserData update(UserData user);

	/**
	 * Update the user.
	 *
	 * @param userData the user data
	 * @return the updated user data
	 */
	@PreAuthorize(PRIVILEGE_ADMIN)
	UserData updateByAdmin(UserData user);

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the user data
	 */
	UserData findById(String id);

	/**
	 * Fetch user.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	User fetchUser(final String userId);

	/**
	 * Fetch non-archived users.
	 *
	 * @return list of non-archived users
	 */
	@PreAuthorize(PRIVILEGE_ADMIN)
	List<UserDisplayData> findAllNonArchivedUsers();

	Set<UserGroup> fetchUserGroups(final String userId);

	UserData updatePassword(String userId, String password);

	boolean isAdminUser(String userId);

	boolean treatAsOneDay(String userId);

	boolean matchUserPassword(String savedPassword, String newPassword);

	void forgotPassword(User user) throws IOException, TemplateException, MessagingException, URISyntaxException;

	boolean isValidToken(String token);

	void forgotUsername(String userEmail) throws NotFoundException, MessagingException, IOException, TemplateException;

}
