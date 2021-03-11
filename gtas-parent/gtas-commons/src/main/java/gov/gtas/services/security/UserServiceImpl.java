/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import gov.gtas.model.UserGroup;
import gov.gtas.repository.PasswordResetTokenRepository;
import gov.gtas.repository.UserGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import gov.gtas.services.email.UserEmailService;
import gov.gtas.services.security.RoleService;
import javassist.NotFoundException;
import freemarker.template.TemplateException;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.PasswordResetToken;
import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;

/**
 * The Class UserServiceImpl.
 */
@Service
public class UserServiceImpl implements UserService {

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private UserRepository userRepository;

	@Resource
	private UserGroupRepository userGroupRepository;

	@Autowired
	private UserServiceUtil userServiceUtil;

	@Autowired
	private RoleServiceUtil roleServiceUtil;

	@Resource
	private UserEmailService userEmailService;

	@Resource
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Resource
	private RoleService roleService;

	@Value("${user.group.default}")
	private Long defaultUserGroupId;

	@Value("${reset.password.token.expiry.minutes}")
	private int expiryTimeInMinutes;

	private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	@Transactional
	public UserData create(UserData userData) {

		if (userRepository.findById(userData.getUserId()).isPresent()) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.USER_ALREADY_EXIST,
					userData.getUserId());
		}

		User userEntity = userServiceUtil.mapUserEntityFromUserData(userData);
		userEntity.setPassword((new BCryptPasswordEncoder()).encode(userEntity.getPassword()));
		userEntity.setArchived(false); // Default do not archive new users.

		Set<Role> roleCollection = roleService.getValidRoles(userData.getRoles());

		userEntity.setRoles(roleCollection);
		User newUserEntity = userRepository.save(userEntity);
		UserGroup defaultUserGroup = userGroupRepository.findById(defaultUserGroupId)
				.orElseThrow(RuntimeException::new);
		defaultUserGroup.getGroupMembers().add(newUserEntity);
		userGroupRepository.save(defaultUserGroup);
		return userServiceUtil.mapUserDataFromEntity(newUserEntity);
	}

	@Override
	@Transactional
	public void delete(String id) {
		User userToDelete = userRepository.findOne(id);
		if (userToDelete != null)
			userRepository.delete(userToDelete);
	}

	@Override
	@Transactional
	public List<UserDisplayData> findAll() {
		Iterable<User> usersCollection = userRepository.findAll();
		return userServiceUtil.getUserDataListFromEntityCollection(usersCollection);
	}

	@Deprecated
	@Override
	@Transactional
	public UserData update(UserData data) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		User entity = userRepository.findOne(data.getUserId().toUpperCase());
		User mappedEnity = userServiceUtil.mapUserEntityFromUserData(data);
		if (entity != null) {
			entity.setFirstName(mappedEnity.getFirstName());
			entity.setLastName(mappedEnity.getLastName());
			if (!BCRYPT_PATTERN.matcher(mappedEnity.getPassword()).matches()) {
				entity.setPassword(passwordEncoder.encode(mappedEnity.getPassword()));
			} else {
				entity.setPassword(mappedEnity.getPassword());
			}
			entity.setArchived(mappedEnity.getArchived());
			entity.setActive(mappedEnity.getActive());
			entity.setPhoneNumber(mappedEnity.getPhoneNumber());
			if (data.getRoles() != null && !data.getRoles().isEmpty()) {
				Set<Role> oRoles = new HashSet<Role>();
				Set<Role> roleCollection = roleServiceUtil.getAdminRoleIfExists(data.getRoles());

				if (roleCollection.isEmpty()) {
					roleCollection = roleService.getValidRoles(data.getRoles());
				}

				oRoles.addAll(roleCollection);
				entity.setRoles(oRoles);
			}

			User savedEntity = userRepository.save(entity);
			return userServiceUtil.mapUserDataFromEntity(savedEntity);
		}
		return null;
	}

	/* Update the user password only. */
	@Transactional
	public UserData updatePassword(String userId, String password) {
		User existingUser = userRepository.findOne(userId.toUpperCase());

		existingUser.setPassword(getEncodedPassword(password));

		User savedEntity = userRepository.save(existingUser);
		return userServiceUtil.mapUserDataFromEntity(savedEntity);
	}

	@Override
	@Transactional
	public UserData findById(String id) {
		String allCapsName = id.toUpperCase();
		User userEntity = userRepository.findOne(allCapsName);
		UserData userData = null;
		if (userEntity != null) {
			userData = userServiceUtil.mapUserDataFromEntity(userEntity);
		}
		return userData;

	}

	/**
	 * Fetches the user object and throws an unchecked exception if the user cannot
	 * be found.
	 * 
	 * @param userId the ID of the user to fetch.
	 * @return the user fetched from the DB.
	 */
	@Override
	public User fetchUser(final String userId) {
		UserData userData = findById(userId);
		if (userData == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(CommonErrorConstants.INVALID_USER_ID_ERROR_CODE,
					userId);
		}
		return userServiceUtil.mapUserEntityFromUserData(userData);
	}

	@Override
	@Transactional
	public Set<UserGroup> fetchUserGroups(final String userId) {
		User user = userRepository.findOne(userId);
		return userGroupRepository.findDistinctByGroupMembersIn(Collections.singleton(user));
	}

	/**
	 * Returns true if the user has an Admin Role
	 *
	 * @param userId the ID of the user to fetch.
	 * @return true if the user has Admin role false otherwise
	 */

	public boolean isAdminUser(String userId) {
		boolean isAdmin = false;

		for (RoleData r : findById(userId).getRoles()) {
			if (r.getRoleId() == 1) {
				isAdmin = true;
			}
		}

		return isAdmin;
	}

	public boolean treatAsOneDay(String userId) {

		UserData user = findById(userId);
		Set<RoleData> userRoles = user.getRoles();
		RoleData oneDay = new RoleData(7, "One Day Lookout");

		return userRoles.contains(oneDay) && userRoles.size() == 1;

	}

	/*
	 * Update all user data except the password. The password is handled by a
	 * separate function
	 */
	@Override
	@Transactional
	public UserData updateByAdmin(UserData data) {
		User entity = userRepository.findOne(data.getUserId()); // returns error if null

		User mappedEntity = userServiceUtil.mapUserEntityFromUserData(data);

		entity.setFirstName(mappedEntity.getFirstName());
		entity.setLastName(mappedEntity.getLastName());
		entity.setEmail(mappedEntity.getEmail());
		entity.setEmailEnabled(mappedEntity.getEmailEnabled());
		entity.setHighPriorityHitsEmailNotification(mappedEntity.getHighPriorityHitsEmailNotification());
		entity.setArchived(mappedEntity.getArchived());
		entity.setPhoneNumber(mappedEntity.getPhoneNumber());
		entity.setActive(mappedEntity.getActive());

		if (data.getRoles() != null && !data.getRoles().isEmpty()) {
			Set<Role> validatedRoles = roleService.getValidRoles(data.getRoles());
			entity.setRoles(validatedRoles);
		}

		User savedEntity = userRepository.save(entity);
		logger.debug("Updated by Admin successfully in " + this.getClass().getName());

		return userServiceUtil.mapUserDataFromEntity(savedEntity);
	}

	@Override
	@Transactional
	public List<UserDisplayData> findAllNonArchivedUsers() {
		Iterable<User> usersCollection = userRepository.getNonArchivedUsers();
		return userServiceUtil.getUserDataListFromEntityCollection(usersCollection);
	}

	@Override
	public boolean matchUserPassword(String savedPassword, String newPassword) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.matches(newPassword, savedPassword);
	}

	@Override
	@Transactional
	public void forgotPassword(User user) {
		user.setPasswordResetToken(generatePasswordResetToken());
		userRepository.save(user);// update reset password

		try {
			userEmailService.sendPasswordResetEmail(user.getUserId(), user.getEmail(), user.getPasswordResetToken());
		} catch (IOException | TemplateException | MessagingException | URISyntaxException e) {

			logger.info(e.getMessage());
		}

	}

	@Override
	@Transactional
	public void forgotUsername(String userEmail)
			throws NotFoundException, MessagingException, IOException, TemplateException {

		String userId = userRepository.findUserByEmail(userEmail);

		if (userId == null) {
			String message = "The provided email (" + userEmail + ") is not on the system!";
			throw new NotFoundException(message);
		}

		userEmailService.sedUsername(userEmail, userId);

	}

	@Override
	@Transactional
	public boolean isValidToken(String token) {
		PasswordResetToken prt = passwordResetTokenRepository.findByTokenValue(token).orElse(null);

		return isValidToken(prt);

	}

	private String getEncodedPassword(String password) {
		if (password == null)
			return null;

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		if (!BCRYPT_PATTERN.matcher(password).matches()) {
			return passwordEncoder.encode(password);
		}
		return password;
	}

	private Date calculateExpiryDate() {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Date().getTime());
		cal.add(Calendar.MINUTE, expiryTimeInMinutes);
		return new Date(cal.getTime().getTime());
	}

	private boolean isValidToken(PasswordResetToken prt) {
		Date now = new Date();
		return prt != null && prt.getExpiryData().after(now);
	}

	private PasswordResetToken generatePasswordResetToken() {
		PasswordResetToken token = new PasswordResetToken();
		String tokenValue = UUID.randomUUID().toString();
		Date tokenExpiryDate = calculateExpiryDate();

		token.setToken(tokenValue);
		token.setExpiryData(tokenExpiryDate);

		return token;
	}

}
