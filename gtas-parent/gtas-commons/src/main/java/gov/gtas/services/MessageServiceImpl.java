/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.MessageType;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Message;
import gov.gtas.model.Pnr;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.MessageRepository;
import gov.gtas.repository.PnrRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
public class MessageServiceImpl implements MessageService {

	@Resource
	MessageRepository messageRepository;

	@Resource
	PnrRepository pnrRepository;

	@Resource
	ApisMessageRepository apisMessageRepository;

	@SuppressWarnings("unchecked") // findTop500ByOrderByIdDesc will always return a subclass of message.
	public List<Message> getMostRecent500Messages() {
		return (List<Message>) messageRepository.findTop500ByOrderByIdDesc();
	}

	@Override
	public RecentMessageInformation mostRecentId(Long passengerId, Long flightId) {
		Set<Pnr> pnrMessages = pnrRepository.getPnrsByPassengerIdAndFlightId(passengerId, flightId);
		List<ApisMessage> apisMessages = apisMessageRepository.findByFlightIdAndPassengerId(passengerId, flightId);
		Message m = null;
		RecentMessageInformation rmi = new RecentMessageInformation();
		if (!pnrMessages.isEmpty()) {
			for (Message pnr : pnrMessages) {
				if (m == null) {
					m = pnr;
					rmi.setMessageId(m.getId());
					rmi.setMessageType(MessageType.PNR);
				} else if (m.getCreateDate().before(pnr.getCreateDate())){
					m = pnr;
					rmi.setMessageId(m.getId());
					rmi.setMessageType(MessageType.PNR);
				}
			}
		}
		if (!apisMessages.isEmpty()) {
			for (Message apis : apisMessages) {
				if (m == null) {
					m = apis;
					rmi.setMessageId(m.getId());
					rmi.setMessageType(MessageType.APIS);
				} else if (m.getCreateDate().before(apis.getCreateDate())){
					m = apis;
					rmi.setMessageId(m.getId());
					rmi.setMessageType(MessageType.APIS);
				}
			}
		}
		return rmi;
	}
}
