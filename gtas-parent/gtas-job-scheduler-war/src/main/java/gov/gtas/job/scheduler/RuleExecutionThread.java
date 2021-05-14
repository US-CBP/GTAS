package gov.gtas.job.scheduler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.rule.KIEAndLastUpdate;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.RuleResults;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;
import gov.gtas.svc.util.TargetingResultUtils;

@Component
@Scope("prototype")
public class RuleExecutionThread implements Callable<RuleResultsWithMessageStatus> {

	private RuleExecutionContext ruleExecutionContext;

	private Logger logger = LoggerFactory.getLogger(RuleExecutionThread.class);

	@Autowired
	private ApplicationContext applicationContext;
	
	private Map<String, KIEAndLastUpdate> rules = new HashMap<>();

	@Override
	public RuleResultsWithMessageStatus call() throws Exception {
		RuleResultsWithMessageStatus ruleResults = new RuleResultsWithMessageStatus();
		ruleResults.setMessageStatusList(ruleExecutionContext.getSource());
		ruleResults.setNumber(ruleExecutionContext.getNumber());
		
		if (ruleExecutionContext.getRuleServiceRequest() == null
				|| ruleExecutionContext.getRuleServiceRequest().getRequestObjects().isEmpty()) {
			return ruleResults;
		}
		
		try {
		long start = System.nanoTime();
		TargetingService targetingService = applicationContext.getBean(TargetingService.class);
		ruleResults = targetingService.analyzeLoadedMessages(ruleExecutionContext, rules);
		logger.debug("generating hit details");
		logger.debug("Rules and Watchlist ran in {} m/s.", (System.nanoTime() - start) / 1000000);
		} catch (Exception e) {
			for (MessageStatus ms : ruleExecutionContext.getSource()) {
				ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
			}
		}
		return ruleResults;
	}

	public RuleExecutionContext getRuleExecutionContext() {
		return ruleExecutionContext;
	}

	public void setRuleExecutionContext(RuleExecutionContext ruleExecutionContext) {
		this.ruleExecutionContext = ruleExecutionContext;
	}

	public Map<String, KIEAndLastUpdate> getRules() {
		return rules;
	}

	public void setRules(Map<String, KIEAndLastUpdate> rules) {
		this.rules = rules;
	}
	
	
}
