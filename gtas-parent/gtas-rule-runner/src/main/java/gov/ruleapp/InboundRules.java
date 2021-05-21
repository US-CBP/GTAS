package gov.ruleapp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.constant.WatchlistConstants;
import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.MessageStatusEnum;
import gov.gtas.model.udr.KnowledgeBase;
import gov.gtas.repository.KnowledgeBaseRepository;
import gov.gtas.repository.MessageStatusRepository;
import gov.gtas.rule.KIEAndLastUpdate;
import gov.gtas.rule.RuleUtils;
import gov.gtas.svc.TargetingService;
import gov.gtas.svc.UdrService;
import gov.gtas.svc.WatchlistService;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;

@Component
public class InboundRules {

	private Map<String, KIEAndLastUpdate> cache = new ConcurrentHashMap<>();

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private KnowledgeBaseRepository knowledgeBaseRepository;

	@Value(value = "#{'${kb.list}'.split(',')}")
	private List<String> kbNames;

	@Autowired
	private WatchlistService watchlistService;

	@Autowired
	private UdrService udrService;

	@Autowired
	MessageStatusRepository messageStatusRepository;

	@Value("${inbound.queue}")
	private String ruleQueueName;

	private static Logger logger = LoggerFactory.getLogger(InboundRules.class);

	private static final ReentrantLock cacheLock = new ReentrantLock();

	private LocalDateTime cacheLastUpdatedCheckDate = LocalDateTime.MIN;

	@JmsListener(destination = "${inbound.queue}", concurrency = "10")
	@SendTo("${outbound.queue}")
	public String inboundRules(Message<?> message, javax.jms.Session session, javax.jms.Message msg) throws Exception {
		ObjectMapper om = new ObjectMapper();
		RuleResultsWithMessageStatus ruleResults = new RuleResultsWithMessageStatus();
		ruleResults.setQueueName(ruleQueueName);
		long start = System.nanoTime();
		try {
			TargetingService targetingService = applicationContext.getBean(TargetingService.class);

			logger.info("Running rules on inbound message");
			List<MessageStatus> messageStatuses = om.readValue((String) message.getPayload(),
					new TypeReference<List<MessageStatus>>() {
					});
			RuleExecutionContext rec = new RuleExecutionContext();
			rec.setSource(messageStatuses);
			try {
				if (messageStatuses.isEmpty()) {
					rec.setSource(messageStatuses);
					return "Done!";
				}
				List<Long> messageStatusIds = messageStatuses.stream().map(MessageStatus::getMessageId)
						.collect(Collectors.toList());
				logger.debug("grabbing messages");
				List<MessageStatus> source = messageStatusRepository.getMessageFromIds(messageStatusIds);
				logger.debug("grabbed messages");
				rec.setSource(source);

				rec = targetingService.createRuleExecutionContext(source);
				rec.setSource(messageStatuses);
			} catch (Exception e) {
				logger.error("Critical error in Drool Fact Gathering Thread!", e);
				for (MessageStatus ms : messageStatuses) {
					ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
				}
				rec.setSource(messageStatuses);
			}
			checkKbs(rec);

			ruleResults.setMessageStatusList(rec.getSource());
			ruleResults.setNumber(rec.getNumber());

			if (!(rec.getRuleServiceRequest() == null || rec.getRuleServiceRequest().getRequestObjects().isEmpty())) {
				ruleResults = runRules(ruleResults, messageStatuses, rec);
				Set<HitDetail> hitDetailSet = targetingService.generateHitDetails(ruleResults.getRuleResults());
				ruleResults.setHitDetails(hitDetailSet);
			}			
			
		} catch (Exception e) {
			logger.info("", e);
		} finally {
			logger.info("Rules and Watchlist ran in {} m/s.", (System.nanoTime() - start) / 1000000);
		}
		ruleResults.setQueueName(ruleQueueName);
		return om.writeValueAsString(ruleResults);
	}

	private RuleResultsWithMessageStatus runRules(RuleResultsWithMessageStatus ruleResults,
			List<MessageStatus> messageStatuses, RuleExecutionContext rec) {
		try {
			Map<String, KIEAndLastUpdate> rules = new ConcurrentHashMap<>(cache);
			TargetingService targetingService = applicationContext.getBean(TargetingService.class);
			ruleResults = targetingService.analyzeLoadedMessages(rec, rules);
			targetingService.generateHitDetails(ruleResults.getRuleResults());
			ruleResults.setMessageStatusList(messageStatuses);
			logger.debug("generating hit details");
		} catch (Exception e) {
			logger.error("ERROR!");
			for (MessageStatus ms : rec.getSource()) {
				ms.setMessageStatusEnum(MessageStatusEnum.FAILED_ANALYZING);
			}
			ruleResults.setMessageStatusList(rec.getSource());
		}
		return ruleResults;
	}

	private void checkKbs(RuleExecutionContext rec) {
		try {
			if (!checkCache()) {
				rec.setRuleServiceRequest(null);
				throw new RuntimeException("Unable to set cache up");
			}
		} catch (Exception e) {
			logger.error("Failed cache setup", e);
		}
	}

	private boolean checkCache() {
		cacheLock.lock();
		boolean noError = true;
		try {
			LocalDateTime now = LocalDateTime.now();
			if (ChronoUnit.MINUTES.between(cacheLastUpdatedCheckDate, now) >= 5) {
				cacheLastUpdatedCheckDate = now;
				List<KnowledgeBase> kbList = knowledgeBaseRepository.getByNames(kbNames);
				for (KnowledgeBase ruleKb : kbList) {
					if (cache.containsKey(ruleKb.getKbName())) {
						KIEAndLastUpdate kie = cache.get(ruleKb.getKbName());
						if (kie.getUpdated().before(ruleKb.getCreationDt())) {
							KIEAndLastUpdate kieAndLastUpdate = makeKieAndLastUpdate(ruleKb);
							if (kieAndLastUpdate == null) {
								throw new RuntimeException("ERROR! NO RULE ENGINE TO FIND");
							}
							cache.put(ruleKb.getKbName(), kieAndLastUpdate);
						}
					} else {
						KIEAndLastUpdate kieAndLastUpdate = makeKieAndLastUpdate(ruleKb);
						if (kieAndLastUpdate == null) {
							throw new RuntimeException("ERROR! NO RULE ENGINE TO FIND");
						}
						cache.put(ruleKb.getKbName(), kieAndLastUpdate);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error checking cache!", e);
			noError = false;
		} finally {
			cacheLock.unlock();
		}
		return noError;
	}

	private KIEAndLastUpdate makeKieAndLastUpdate(KnowledgeBase kb) throws IOException {
		logger.info("Creating a KB. This can take a long time...");
		boolean isWatchlist = kb.getKbName().startsWith(WatchlistConstants.WL_KNOWLEDGE_BASE_NAME);
		KIEAndLastUpdate kieAndLastUpdate = new KIEAndLastUpdate();
		KnowledgeBase newKb;
		if (isWatchlist) {
			newKb = watchlistService.createAKnowledgeBase(kb.getKbName());
		} else {
			newKb = udrService.recompileRules(kb.getKbName(), "RULE_SCHEDULER");
		}
		if (newKb != null) {
			KieBase kieBase = RuleUtils.createKieBaseFromDrlString(new String(newKb.getRulesBlob()));
			Date updatedDate = kb.getCreationDt();
			kieAndLastUpdate.setKieBase(kieBase);
			kieAndLastUpdate.setKbName(kb.getKbName());
			kieAndLastUpdate.setUpdated(updatedDate);
			logger.info("KB created!");
		} else {
			kieAndLastUpdate = null;
		}
		return kieAndLastUpdate;
	}
}
