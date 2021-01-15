/* All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).*/
package gov.gtas.controller;

import gov.gtas.model.MessageStatus;
import gov.gtas.repository.HitViewStatusRepository;
import gov.gtas.repository.UserRepository;
import gov.gtas.vo.ErrorMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}