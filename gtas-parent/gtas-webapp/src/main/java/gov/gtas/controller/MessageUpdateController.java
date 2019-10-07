package gov.gtas.controller;

import gov.gtas.model.MessageStatusEnum;
import gov.gtas.repository.MessageStatusRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class MessageUpdateController {

	private final MessageStatusRepository messageStatusRepository;

	public MessageUpdateController(MessageStatusRepository messageStatusRepository) {
		this.messageStatusRepository = messageStatusRepository;
	}

	@RequestMapping(value = "/api/messages/loaded", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody void updateMessages(@RequestBody List<Long> processedMessageIds, HttpServletRequest hsr) {
		messageStatusRepository.updateMessageWithIdAndEnum(processedMessageIds, MessageStatusEnum.NEO_LOADED);
	}
}
