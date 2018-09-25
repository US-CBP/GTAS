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
import java.util.Set;

import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Pnr;
import gov.gtas.svc.util.RuleExecutionContext;

/**
 * The API for the Targeting Service.
 */
public interface TargetingService {
	/**
	 * Invokes the Targeting service for an API message.
	 * 
	 * @param message
	 *            the API message.
	 * @return the result of the invocation.
	 */
	RuleServiceResult analyzeApisMessage(ApisMessage message);

	/**
	 * Invokes the Targeting service for an API message.
	 *
	 * @param messageId
	 *            the message id
	 * @return the result of the invocation.
	 */
	RuleServiceResult analyzeApisMessage(long messageId);

	/**
	 * Invokes the Targeting service for all unprocessed API messages.
	 * 
	 * @return the result of the invocation.
	 */
	List<RuleHitDetail> analyzeLoadedApisMessage();

	/**
	 * Invokes the Targeting service for all unprocessed PNR messages.
	 * 
	 * @return the result of the invocation.
	 */
	List<RuleHitDetail> analyzeLoadedPnr();

	/**
	 * Invokes the Targeting service for all unprocessed PNR and APIS messages.
	 * 
	 * @param updateProcesssedMessageStatus
	 *            it true, then the Targeting Service will update the status of
	 *            each processed message.
	 * @return the result of the invocation.
	 */
	RuleExecutionContext analyzeLoadedMessages(final boolean updateProcesssedMessageStatus);

	/**
	 * Invokes the Rule Engine on an arbitrary list of objects using the
	 * specified DRL rules string
	 * 
	 * @param request
	 *            The rule request containing an arbitrary list of request
	 *            objects to be inserted into the Rule Engine context.
	 * @param drlRules
	 *            The DROOLS rules to apply on the request objects.
	 * @return the result of the invocation.
	 */
	RuleServiceResult applyRules(RuleServiceRequest request, String drlRules);

	/**
	 * Running Rule Engine through Scheduler.
	 * @return 
	 * 
	 */
	public Set<Long> runningRuleEngine();

	/**
	 * Update the rule and watchlist hit counts for a set of flight id's.
	 * 
	 * Initially I did not want to create a separate method for this and just
	 * include the logic in the rule runner, but after some testing on large
	 * amounts of data, it seems that hibernate should flush the hits to the
	 * hits_summary table prior to computing the updated hit counts; otherwise
	 * we get inaccurate results.
	 * 
	 * @param flights
	 *            set of flight id's to update
	 */
	public void updateFlightHitCounts(Set<Long> flights);

	/**
	 * retrieve ApisMessage from db.
	 *
	 * @param messageStatus
	 *            the message status
	 * @return the list
	 */
	public List<ApisMessage> retrieveApisMessage(MessageStatus messageStatus);

	/**
	 * update ApisMessage with message status.
	 *
	 * @param message
	 *            the message
	 * @param messageStatus
	 *            the message status
	 */
	public void updateApisMessage(ApisMessage message, MessageStatus messageStatus);

	/**
	 * retrieve ApisMessage from db.
	 *
	 * @param messageStatus
	 *            the message status
	 * @return the list
	 */
	public List<Pnr> retrievePnr(MessageStatus messageStatus);

	/**
	 * update Pnr with message status.
	 *
	 * @param message
	 *            the message
	 * @param messageStatus
	 *            the message status
	 */
	public void updatePnr(Pnr message, MessageStatus messageStatus);

}
