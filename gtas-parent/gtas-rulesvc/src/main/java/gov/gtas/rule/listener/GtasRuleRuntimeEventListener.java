/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.listener;

import gov.gtas.bo.RuleExecutionStatistics;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends DefaultRuleRuntimeEventListener that implements
 * RuleRuntimeEventListener interface with empty methods so that you only have
 * to override the methods that you are interested in
 * 
 *
 */
public class GtasRuleRuntimeEventListener extends DefaultRuleRuntimeEventListener {
	private static final Logger logger = LoggerFactory.getLogger(GtasRuleRuntimeEventListener.class);
	private static final List<ObjectInsertedEvent> insertEvents = new ArrayList<>();
	private static final List<ObjectUpdatedEvent> updateEvents = new ArrayList<>();
	private static final List<ObjectDeletedEvent> retractEvents = new ArrayList<>();

	private RuleExecutionStatistics ruleExecutionStatistics;

	/**
	 * constructor.
	 * 
	 * @param stats
	 *            the data structure to collect statistics.
	 */
	public GtasRuleRuntimeEventListener(final RuleExecutionStatistics stats) {
		this.ruleExecutionStatistics = stats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kie.api.event.rule.DefaultRuleRuntimeEventListener#objectInserted
	 * (org.kie.api.event.rule.ObjectInsertedEvent)
	 */
	@Override
	public void objectInserted(ObjectInsertedEvent event) {
		this.ruleExecutionStatistics.incrementTotalObjectsModified();
		this.ruleExecutionStatistics.addInsertedObject(event.getObject());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kie.api.event.rule.DefaultRuleRuntimeEventListener#objectUpdated(
	 * org.kie.api.event.rule.ObjectUpdatedEvent)
	 */
	@Override
	public void objectUpdated(ObjectUpdatedEvent event) {
		this.ruleExecutionStatistics.incrementTotalObjectsModified();
		this.ruleExecutionStatistics.addModifiedObject(event.getObject());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kie.api.event.rule.DefaultRuleRuntimeEventListener#objectDeleted(
	 * org.kie.api.event.rule.ObjectDeletedEvent)
	 */
	@Override
	public void objectDeleted(ObjectDeletedEvent event) {
		this.ruleExecutionStatistics.incrementTotalObjectsModified();
		this.ruleExecutionStatistics.addDeletedObject(event.getOldObject());
	}

	public static List<ObjectInsertedEvent> getInsertevents() {
		return insertEvents;
	}

	public static List<ObjectUpdatedEvent> getUpdateevents() {
		return updateEvents;
	}

	public static List<ObjectDeletedEvent> getRetractevents() {
		return retractEvents;
	}

}
