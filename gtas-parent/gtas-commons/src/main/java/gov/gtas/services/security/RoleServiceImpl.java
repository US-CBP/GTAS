/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.Role;
import gov.gtas.repository.RoleRepository;

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

        // filter out the SysAdmin role; it should not be a choice on the front end.
        List<Role> filteredRoleList = StreamSupport.stream(roleEntityCollection.spliterator(), false)
        	      .filter(r -> (r.getRoleId() != 6))
        	      .collect(Collectors.toList());        
        
        Set<RoleData> roles = roleServiceUtil.getRoleDataSetFromEntityCollection(filteredRoleList);

        return roles;
    }
}
