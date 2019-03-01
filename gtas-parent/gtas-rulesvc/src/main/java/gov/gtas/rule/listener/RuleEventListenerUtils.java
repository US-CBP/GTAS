/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.listener;

import gov.gtas.bo.RuleExecutionStatistics;

import java.util.EventListener;
import java.util.LinkedList;
import java.util.List;

import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rule Engine Event Listener utility functions.
 */
public class RuleEventListenerUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(RuleEventListenerUtils.class);

    /**
     * Adds events listeners to the Kie Session.
     * 
     * @param ksession
     *            the session to add listeners to.
     * @param eventListenerList
     *            the list of event listeners.
     */
    public static void addEventListenersToKieSEssion(
            final StatelessKieSession ksession,
            final List<EventListener> eventListenerList) {

        // iterate thru the list and add the listeners
        if (eventListenerList != null) {
            for (EventListener el : eventListenerList) {
                if (el instanceof DefaultAgendaEventListener) {
                    ksession.addEventListener((DefaultAgendaEventListener) el);
                } else if (el instanceof DefaultRuleRuntimeEventListener) {
                    ksession.addEventListener((DefaultRuleRuntimeEventListener) el);
                }
            }
        }
    }

    /**
     * Adds default events listeners to the Kie Session.
     * 
     * @param ksession
     *            the session to add listeners to.
     * @param stats
     *            the object to collect statistics.
     */
    public static void addEventListenersToKieSEssion(
            final StatelessKieSession ksession,
            final RuleExecutionStatistics stats) {

        if (ksession != null && stats != null) {
            ksession.addEventListener(new GtasAgendaEventListener(stats));
            ksession.addEventListener(new GtasRuleRuntimeEventListener(stats));
        }
    }

    /**
     * Creates a list of KieSession event listeners.
     * 
     * @param stats
     *            the data structure to accumulate rule execution statistics.
     * @return list of event listeners.
     */
    public static List<EventListener> createEventListeners(
            final RuleExecutionStatistics stats) {
        logger.debug("Entering createEventListeners().");
        List<EventListener> eventListenerList = new LinkedList<EventListener>();

        eventListenerList.add(new GtasAgendaEventListener(stats));
        eventListenerList.add(new GtasRuleRuntimeEventListener(stats));
        return eventListenerList;

    }

}
