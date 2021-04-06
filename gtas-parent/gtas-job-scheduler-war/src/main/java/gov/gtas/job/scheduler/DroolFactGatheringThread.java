package gov.gtas.job.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.util.RuleExecutionContext;

@Component
@Scope("prototype")
public class DroolFactGatheringThread implements Callable<RuleExecutionContext> {

	@Autowired
	ApplicationContext applicationContext;

	private Logger logger = LoggerFactory.getLogger(DroolFactGatheringThread.class);

	private List<MessageStatus> messageStatuses = new ArrayList<>();
	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public RuleExecutionContext call() {

		RuleExecutionContext rec = new RuleExecutionContext();
		rec.setNumber(number);
		rec.setSource(messageStatuses);

		try {
			if (messageStatuses.isEmpty()) {
				rec.setSource(messageStatuses);
				rec.setNumber(number);
				return rec;
			}			
			
			TargetingService targetingService = applicationContext.getBean(TargetingService.class);
			rec = targetingService.createRuleExecutionContext(messageStatuses);
			rec.setNumber(number);
			rec.setSource(messageStatuses);
		} catch (Exception e) {
			logger.error("Critical error in Drool Fact Gathering Thread!", e);
			for (MessageStatus ms : messageStatuses) {
				ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
			}
			rec.setSource(messageStatuses);
		}
		return rec;
	}

	public List<MessageStatus> getMessageStatuses() {
		return messageStatuses;
	}

	public void setMessageStatuses(List<MessageStatus> messageStatuses) {
		this.messageStatuses = messageStatuses;
	}
}
