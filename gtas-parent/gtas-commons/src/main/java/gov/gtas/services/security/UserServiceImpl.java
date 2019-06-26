/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.error.ErrorHandlerFactory;
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

	@Autowired
	private UserServiceUtil userServiceUtil;

	@Autowired
	private RoleServiceUtil roleServiceUtil;

	private Pattern BCRYPT_PATTERN = Pattern
			.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
	
	private static final Logger logger = LoggerFactory
			.getLogger(UserServiceImpl.class);

	@Override
	@Transactional
	public UserData create(UserData userData) {
		User userEntity = userServiceUtil.mapUserEntityFromUserData(userData);
		userEntity.setPassword((new BCryptPasswordEncoder()).encode(userEntity
				.getPassword()));
		if (userData.getRoles() != null) {
			Set<Role> roleCollection = roleServiceUtil
					.mapEntityCollectionFromRoleDataSet(userData.getRoles());
			userEntity.setRoles(roleCollection);
		}
		User newUserEntity = userRepository.save(userEntity);
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
	public List<UserData> findAll() {
		Iterable<User> usersCollection = userRepository.findAll();
		return userServiceUtil
				.getUserDataListFromEntityCollection(usersCollection);
	}

	@Override
	@Transactional
	public UserData update(UserData data) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		User entity = userRepository.findOne(data.getUserId());
		User mappedEnity = userServiceUtil.mapUserEntityFromUserData(data);
		if (entity != null) {
			entity.setFirstName(mappedEnity.getFirstName());
			entity.setLastName(mappedEnity.getLastName());
			if (!BCRYPT_PATTERN.matcher(mappedEnity.getPassword()).matches()) {
				entity.setPassword(passwordEncoder.encode(mappedEnity
						.getPassword()));
			} else {
				entity.setPassword(mappedEnity.getPassword());
			}

			entity.setActive(mappedEnity.getActive());
			if (data.getRoles() != null && !data.getRoles().isEmpty()) {
				Set<Role> oRoles = entity.getRoles();
				oRoles.clear();
				Set<Role> roleCollection = roleServiceUtil
						.mapEntityCollectionFromRoleDataSet(data.getRoles());
				oRoles.addAll(roleCollection);
				entity.setRoles(oRoles);
			}

			User savedEntity = userRepository.save(entity);
			return userServiceUtil.mapUserDataFromEntity(savedEntity);
		}
		return null;
	}

	@Override
	@Transactional
	public UserData findById(String id) {
		User userEntity = userRepository.findOne(id);
		UserData userData = null;
		if (userEntity != null) {
			userData = userServiceUtil.mapUserDataFromEntity(userEntity);
		}
		return userData;

	}

	/**
	 * Fetches the user object and throws an unchecked exception if the user
	 * cannot be found.
	 * 
	 * @param userId
	 *            the ID of the user to fetch.
	 * @return the user fetched from the DB.
	 */
	@Override
	@Transactional
	public User fetchUser(final String userId) {
		UserData userData = findById(userId);
		if (userData == null) {
			throw ErrorHandlerFactory.getErrorHandler().createException(
					CommonErrorConstants.INVALID_USER_ID_ERROR_CODE, userId);
		}
		return userServiceUtil.mapUserEntityFromUserData(userData);
	}

	
	
	/**
	 * Returns true if the user has an Admin Role
	 * 
	 * @param userId
	 *            the ID of the user to fetch.
	 * @return 
	 * 			true if the user has Admin role
	 * 			flase otherwise
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

	@Override
	public UserData updateByAdmin(UserData data) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		User entity = userRepository.findOne(data.getUserId());
		
		User mappedEnity = userServiceUtil.mapUserEntityFromUserData(data);
		if (entity != null) {
			entity.setFirstName(mappedEnity.getFirstName());
			entity.setLastName(mappedEnity.getLastName());
			
			if(data.getPassword()!=null && !data.getPassword().isEmpty())
			{
				if (!BCRYPT_PATTERN.matcher(mappedEnity.getPassword()).matches()) {
					entity.setPassword(passwordEncoder.encode(mappedEnity
						.getPassword()));
				} else {
					entity.setPassword(mappedEnity.getPassword());
				}
			}

			entity.setActive(mappedEnity.getActive());
			if (data.getRoles() != null) {// && !data.getRoles().isEmpty()) {
				if(!data.getRoles().isEmpty()) {
					Set<Role> oRoles = entity.getRoles();
					oRoles.clear();
					Set<Role> roleCollection = roleServiceUtil
							.mapEntityCollectionFromRoleDataSet(data.getRoles());
					oRoles.addAll(roleCollection);
					entity.setRoles(oRoles);
				}
				else {
					entity.setRoles(new HashSet<Role>());
				}
			}

			User savedEntity = userRepository.save(entity);
			logger.debug("Updated by Admin successfully in " + this.getClass().getName());
			
			return userServiceUtil.mapUserDataFromEntity(savedEntity);
		}
		return null;
	}

}
