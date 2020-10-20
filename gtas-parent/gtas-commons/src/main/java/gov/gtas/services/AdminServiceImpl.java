/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.HitDetail;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.services.dto.AppStatisticsDTO;
import gov.gtas.services.dto.ApplicationStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminServiceImpl implements AdminService {

	private MessageService messageService;

	private HitDetailRepository hitDetailRepository;

	@Autowired
	public AdminServiceImpl(MessageService messageService, HitDetailRepository hitDetailRepository) {
		this.messageService = messageService;
		this.hitDetailRepository = hitDetailRepository;
	}

	@Deprecated
	@Override
	public ApplicationStatisticsDTO createApplicationStatisticsDto() {
		List<Message> messageList = messageService.getMostRecent500Messages();
		if (messageList.isEmpty()) {
			return new ApplicationStatisticsDTO();
		}
		int passengerCount = getPassengerCount(messageList);
		ApplicationStatisticsDTO applicationStatisticsDTO = new ApplicationStatisticsDTO();
		applicationStatisticsDTO.setPassengerCount(passengerCount);
		messageList.stream().findFirst()
				.ifPresent(m -> applicationStatisticsDTO.setLastMessageInSystem(m.getCreateDate()));
		List<MessageStatus> msList = messageList.stream().map(Message::getStatus).collect(Collectors.toList());
		populateApplicationStatistics(applicationStatisticsDTO, msList);
		HitDetail mostRecentHitDetail = hitDetailRepository.findFirstByOrderByIdDesc();
		if (mostRecentHitDetail != null) {
			applicationStatisticsDTO.setMostRecentRuleHit(mostRecentHitDetail.getCreatedDate());
		}
		return applicationStatisticsDTO;
	}

	@Deprecated
	@SuppressWarnings("WeakerAccess") // For test.
	protected void populateApplicationStatistics(ApplicationStatisticsDTO applicationStatisticsDTO,
			List<MessageStatus> msList) {
		applicationStatisticsDTO.setLastMessageAnalyzedByDrools(Date.from(Instant.EPOCH));
		for (MessageStatus ms : msList) {
			switch (ms.getMessageStatusEnum()) {
			case RECEIVED:
			case PARSED:
				applicationStatisticsDTO.setParsed(applicationStatisticsDTO.getParsed() + 1);
				break;
			case LOADED:
				applicationStatisticsDTO.setLoadedCount(applicationStatisticsDTO.getLoadedCount() + 1);
				break;
			case RUNNING_RULES:
				applicationStatisticsDTO.setRunningRules(applicationStatisticsDTO.getRunningRules() + 1);
				break;
			case ANALYZED:
				if (ms.getAnalyzedTimestamp() != null
						&& ms.getAnalyzedTimestamp().after(applicationStatisticsDTO.getLastMessageAnalyzedByDrools())) {
					applicationStatisticsDTO.setLastMessageAnalyzedByDrools(ms.getAnalyzedTimestamp());
				}
				applicationStatisticsDTO.setAnalyzedCount(applicationStatisticsDTO.getAnalyzedCount() + 1);
				break;
			case NEO_LOADED:
				applicationStatisticsDTO.setLoadedInNeo4JCount(applicationStatisticsDTO.getLoadedInNeo4JCount() + 1);
				break;
			case NEO_ANALYZED:
				applicationStatisticsDTO.setNeoAnalyzedCount(applicationStatisticsDTO.getNeoAnalyzedCount() + 1);
				break;
			case FAILED_PRE_PARSE:
			case FAILED_PRE_PROCESS:
			case FAILED_PARSING:
				applicationStatisticsDTO.setFailedParsingCount(applicationStatisticsDTO.getFailedParsingCount() + 1);
				break;
			case FAILED_LOADING:
				applicationStatisticsDTO.setFailedLoadCount(applicationStatisticsDTO.getFailedLoadCount() + 1);
				break;
			case FAILED_ANALYZING:
				applicationStatisticsDTO.setFailedAnalyzedCount(applicationStatisticsDTO.getFailedAnalyzedCount() + 1);
				break;
			case FAILED_NEO_4_J:
				applicationStatisticsDTO.setFailedNeo4jCount(applicationStatisticsDTO.getFailedNeo4jCount() + 1);
				break;
			case PARTIAL_ANALYZE:
				applicationStatisticsDTO
						.setPartialAnalyzedCount(applicationStatisticsDTO.getPartialAnalyzedCount() + 1);
				break;
			default:
				throw new RuntimeException("Un-used field!");
			}
		}
	}
	
	protected int getPassengerCount(List<Message> messageList) {
		int count = 0;
		for (Message m : messageList) {
			if (m.getPassengerCount() != null) {
				count += m.getPassengerCount();
			}
		}
		return count;
	}
	
	@Override
	public AppStatisticsDTO getAppStatistics() {

		List<Message> messageList = messageService.getMostRecent500Messages();
		if (messageList.isEmpty()) {
			return new AppStatisticsDTO();
		}
		int passengerCount = getPassengerCount(messageList);
		AppStatisticsDTO applicationStatisticsDTO = new AppStatisticsDTO();
		applicationStatisticsDTO.setPassengerCount(passengerCount);
		messageList.stream().findFirst()
				.ifPresent(m -> applicationStatisticsDTO.setLastMessageInSystem(m.getCreateDate().getTime()));
		
		List<MessageStatus> msList = messageList.stream().map(Message::getStatus).collect(Collectors.toList());
		
		getApplicationStatistics(applicationStatisticsDTO, msList);
		HitDetail mostRecentHitDetail = hitDetailRepository.findFirstByOrderByIdDesc();
		
		if (mostRecentHitDetail != null) {
			applicationStatisticsDTO.setMostRecentRuleHit(mostRecentHitDetail.getCreatedDate().getTime());
		}
		return applicationStatisticsDTO;
	}
	
	private void getApplicationStatistics(AppStatisticsDTO dto, List<MessageStatus> msList) {
		dto.setLastMessageAnalyzedByDrools(Date.from(Instant.EPOCH).getTime());
		
		for (MessageStatus ms : msList) {
			switch (ms.getMessageStatusEnum()) {
			case ANALYZED:
				if (ms.getAnalyzedTimestamp() != null
				&& ms.getAnalyzedTimestamp().after(new Date(dto.getLastMessageAnalyzedByDrools()))) {
					dto.setLastMessageAnalyzedByDrools(ms.getAnalyzedTimestamp().getTime());
				}
				break;
			case FAILED_PARSING:
				dto.setTotalLoadingParsingErrors(dto.getTotalLoadingParsingErrors() + 1);
				break;
			case FAILED_LOADING:
				dto.setTotalLoadingParsingErrors(dto.getTotalLoadingParsingErrors() + 1);
				break;
			case DUPLICATE_MESSAGE:
				dto.setTotalLoadingParsingErrors(dto.getTotalLoadingParsingErrors() + 1);
				break;
			case NEO_LOADED:
				dto.setTotalRuleErros(dto.getTotalRuleErros() + 1);
				break;
			case FAILED_ANALYZING:
				dto.setTotalRuleErros(dto.getTotalRuleErros() + 1);
				break;
			case PARTIAL_ANALYZE:
				dto.setTotalRuleErros(dto.getTotalRuleErros() + 1);
				break;
			
			default:
				break;
			
			}
		}
		
		
	}
}
