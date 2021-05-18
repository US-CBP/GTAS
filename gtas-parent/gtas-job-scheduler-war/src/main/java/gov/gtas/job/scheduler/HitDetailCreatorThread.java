package gov.gtas.job.scheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.util.HitDetailsWithMessageStatus;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;


@Component
@Scope("prototype")
public class HitDetailCreatorThread implements Callable<HitDetailsWithMessageStatus>{
	
	private final Logger logger = LoggerFactory.getLogger(HitDetailCreatorThread.class);
	
	private RuleResultsWithMessageStatus ruleResultsWithMessageStatus;
	
	@Autowired
	ApplicationContext applicationContext;

	@Override
	public HitDetailsWithMessageStatus call() throws Exception {
		Set<HitDetail> hitDetails = new HashSet<>();
		HitDetailsWithMessageStatus hdDetailsWithMessageStatus = new HitDetailsWithMessageStatus();
		hdDetailsWithMessageStatus.setMessageStatuses(ruleResultsWithMessageStatus.getMessageStatusList());
		hdDetailsWithMessageStatus.setHitDetails(hitDetails);
		try {
		TargetingService targetingService = applicationContext.getBean(TargetingService.class);
		hitDetails = targetingService.generateHitDetails(ruleResultsWithMessageStatus.getRuleResults());
		hdDetailsWithMessageStatus.setHitDetails(hitDetails);
		} catch (Exception e) {
			logger.error("Error making hit detials!",e);
			for (MessageStatus ms : hdDetailsWithMessageStatus.getMessageStatuses()) {
				ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
			}
		}
		return hdDetailsWithMessageStatus;
	}

	public RuleResultsWithMessageStatus getRuleResultsWithMessageStatus() {
		return ruleResultsWithMessageStatus;
	}

	public void setRuleResultsWithMessageStatus(RuleResultsWithMessageStatus ruleResultsWithMessageStatus) {
		this.ruleResultsWithMessageStatus = ruleResultsWithMessageStatus;
	}

}
