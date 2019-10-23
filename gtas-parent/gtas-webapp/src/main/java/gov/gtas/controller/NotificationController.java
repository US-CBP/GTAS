/* All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).*/
package gov.gtas.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.model.MessageStatus;
import gov.gtas.model.User;
import gov.gtas.repository.HitViewStatusRepository;
import gov.gtas.repository.UserRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.vo.ErrorMessageVo;

@RestController
public class NotificationController {
	
	@Autowired
	HitViewStatusRepository  hitViewStatusRepository;
	
	@Autowired
	UserRepository userRepository;
	
	public NotificationController() {
	}

	@RequestMapping(method = RequestMethod.GET, value = "/errorMessage")
	public List<ErrorMessageVo> getErrorMessage() {
		Set<MessageStatus> errorStatuses = new HashSet<>();
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/hitCount")
	public Integer getHitCount() {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		User loggedInUser = userRepository.userAndGroups(userId).orElseThrow(RuntimeException::new);
		Date now = new Date();
		Date etd = new Date(now.getTime() - 900000L);
		Date eta = new Date(now.getTime() + 86400000L);
		return hitViewStatusRepository.getHitViewCountWithNewStatus(loggedInUser.getUserGroups(), etd, eta);
	}
}