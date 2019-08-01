/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Message;
import gov.gtas.repository.MessageRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    MessageRepository messageRepository;

    @SuppressWarnings("unchecked") //findTop500ByOrderByIdDesc will always return a subclass of message.
    public List<Message> getMostRecent500Messages() {
        return (List<Message>) messageRepository.findTop500ByOrderByIdDesc();
    }


}
