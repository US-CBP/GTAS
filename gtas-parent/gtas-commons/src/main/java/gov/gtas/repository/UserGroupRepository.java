/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.repository;

import gov.gtas.model.User;
import gov.gtas.model.UserGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

	Set<UserGroup> findDistinctByGroupMembersIn(Set<User> userSet);

}
