/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Message;
import gov.gtas.model.Pnr;
import gov.gtas.svc.request.builder.RuleEngineRequestBuilder;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TargetingServiceUtils {
    
    private static final Logger logger = LoggerFactory
            .getLogger(TargetingServiceUtils.class);
    
    /**
     * Creates a request from a API message.
     * 
     * @param req
     *            the API message.
     * @return RuleServiceRequest object.
     */
    public static RuleExecutionContext createApisRequest(final ApisMessage req) {
        Collection<ApisMessage> apisMessages = new LinkedList<ApisMessage>();
        apisMessages.add(req);
        return createPnrApisRequestContext(apisMessages, null);
    }

    /**
     * Creates a request from a PNR message.
     * 
     * @param req
     *            the PNR message.
     * @return RuleServiceRequest object.
     */
    public static RuleExecutionContext createPnrRequestContext(final Pnr req) {
        Collection<Pnr> pnrs = new LinkedList<Pnr>();
        pnrs.add(req);
        return createPnrApisRequestContext(null, pnrs);
    }

    /**
     * Create a rule engine request message from a list of message objects from
     * the domain model.
     * 
     * @param reqList
     *            the message objects.
     * @return the constructed rule engine request suitable for Rule Engine
     *         invocation.
     */
    public static RuleExecutionContext createApisRequestContext(
            final List<ApisMessage> reqList) {
        return createPnrApisRequestContext(reqList, null);
    }

    /**
     * Create a rule engine request message from a list of message objects from
     * the domain model.
     * 
     * @param reqList
     *            the message objects.
     * @return the constructed rule engine request suitable for Rule Engine
     *         invocation.
     */
    public static RuleExecutionContext createPnrRequestContext(
            final List<Pnr> reqList) {
        return createPnrApisRequestContext(null, reqList);
    }

    /**
     * Creates a Rule Engine request containing data from a collection of APIS
     * and PNR messages.
     * 
     * @param apisMessages
     * @param pnrs
     * @return the rule engine request object.
     */
    public static RuleExecutionContext createPnrApisRequestContext(
            final Collection<ApisMessage> apisMessages,
            final Collection<Pnr> pnrs) {
        RuleEngineRequestBuilder bldr = new RuleEngineRequestBuilder();
        if (pnrs != null) {
            for (Pnr msg : pnrs) {
                bldr.addPnr(msg);
            }
        }
        if (apisMessages != null) {
            for (ApisMessage msg : apisMessages) {
                bldr.addApisMessage(msg);
            }
        }
        RuleExecutionContext context = new RuleExecutionContext();
        context.setPaxFlightTuples(bldr.getPassengerFlightSet());
        context.setRuleServiceRequest(bldr.build());
        return context;
    }

    /**
     * Creates a Rule Engine request containing data from a List of Messages.
     * 
     * @param loadedMessages
     *            List of Messages
     * @return the rule engine request object.
     */
    public static RuleExecutionContext createPnrApisRequestContext(
            final List<Message> loadedMessages) {
        logger.info("Entering createPnrApisRequestContext().");
        RuleEngineRequestBuilder bldr = new RuleEngineRequestBuilder();

        if (loadedMessages != null) {
            for (Message message : loadedMessages) {
                if (message instanceof ApisMessage) {
                    bldr.addApisMessage((ApisMessage) message);
                } else if (message instanceof Pnr) {
                    bldr.addPnr((Pnr) message);
                }
            }
        }
        RuleExecutionContext context = new RuleExecutionContext();
        context.setPaxFlightTuples(bldr.getPassengerFlightSet());
        context.setRuleServiceRequest(bldr.build());
        return context;
    }
}
