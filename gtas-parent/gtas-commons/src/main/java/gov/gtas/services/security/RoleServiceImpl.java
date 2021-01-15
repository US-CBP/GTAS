/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.util.*;
import java.util.stream.*;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.Role;
import gov.gtas.repository.RoleRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RoleServiceImpl implements RoleService {

	@PersistenceContext
	private EntityManager entityManager;

	@Resource
	private RoleRepository roleRepository;

	@Autowired
	private RoleServiceUtil roleServiceUtil;

	@Override
	@Transactional
	public Set<RoleData> findAll() {

		Iterable<Role> roleEntityCollection = roleRepository.findAll();

		List<Role> filteredRoleList = StreamSupport.stream(roleEntityCollection.spliterator(), false)
    .collect(Collectors.toList());

		Set<RoleData> roles = roleServiceUtil.getRoleDataSetFromEntityCollection(filteredRoleList);

		return roles;
	}

	/*
	Return a set of matching roles from the DB containing either the admin role or a collection
	of non-admin roles.
	 */
	public Set<Role> getValidRoles(Set<RoleData> roleDataSet) {
		Set<Role> validRoles = new HashSet<Role>();
		Iterable<Role> allRoles = roleRepository.findAll();
		Role admin = StreamSupport.stream(allRoles.spliterator(), false)
				.filter(r -> (r.getRoleDescription().equals("Admin"))).findFirst().get();

		// admin role
		for (RoleData rd : roleDataSet) {
			if (rd.getRoleDescription().equals(admin.getRoleDescription())) {
				validRoles.add(admin);
				return validRoles;
			}
		}

		// non-admin roles
		for (RoleData raw : roleDataSet) {
			String rawDescription = raw.getRoleDescription();
			Role validRole = StreamSupport.stream(allRoles.spliterator(), false)
					.filter(r -> r.getRoleDescription().equals(rawDescription)).findFirst().orElse(null);

			if (validRole != null) validRoles.add(validRole);
		}

		return validRoles;
	}

}
