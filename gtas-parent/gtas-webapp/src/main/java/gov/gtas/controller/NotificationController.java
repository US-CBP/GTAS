/* All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).*/
package gov.gtas.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.repository.BagRepository;
import gov.gtas.repository.MessageRepository;
import gov.gtas.vo.ErrorMessageVo;

@RestController
public class NotificationController {
	@Resource
	private MessageRepository messageRepository;
	@RequestMapping(method = RequestMethod.GET, value = "/errorMessage")
	public List<ErrorMessageVo> getErrorMessage(){
		Set<MessageStatus> errorStatuses = new HashSet<>();
		errorStatuses.add(MessageStatus.FAILED_PARSING);
		errorStatuses.add(MessageStatus.FAILED_LOADING);
		errorStatuses.add(MessageStatus.FAILED_ANALYZING);
		List<ErrorMessageVo> errorMessagesVo = new ArrayList<>();
		List<Message> messages = messageRepository.findByStatusIn(errorStatuses);
		for(Message message : messages) {
			ErrorMessageVo em = new ErrorMessageVo();
			em.setId(message.getId());
			em.setCreateDate(message.getCreateDate());
			em.setError(message.getError());
			em.setStatus(message.getStatus().name());
			errorMessagesVo.add(em);
		}
		return 	errorMessagesVo;
	}
}