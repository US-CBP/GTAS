/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
/**
 * 
 */
package gov.gtas.svc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.gtas.model.HitDetail;
import gov.gtas.model.MessageStatus;
import gov.gtas.rule.KIEAndLastUpdate;
import gov.gtas.svc.util.RuleResults;
import gov.gtas.svc.util.RuleResultsWithMessageStatus;

/**
 * The API for the Targeting Service.
 */
public interface TargetingService {

	RuleResultsWithMessageStatus analyzeLoadedMessages(List<MessageStatus> messageStatuses, Map<String, KIEAndLastUpdate> rules);

	void saveMessageStatuses(List<MessageStatus> setMessagesToAnalyzed);

	Set<HitDetail> generateHitDetails(RuleResults ruleRunningResult);

}
