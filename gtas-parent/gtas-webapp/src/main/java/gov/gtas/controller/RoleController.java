/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.RoleService;

@RestController
public class RoleController {

    @Autowired
    RoleService roleService;

    @RequestMapping(method = RequestMethod.GET, value = "/roles/")
    public Set<RoleData> getAllRoles() {

        return roleService.findAll();
    }

}
