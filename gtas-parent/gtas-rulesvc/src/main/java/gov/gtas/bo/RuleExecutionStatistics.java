/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.bo;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A Java bean class that keeps useful statistics of a rule engine run (i.e.
 * session).
 */
public class RuleExecutionStatistics implements Serializable {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1396889006615410141L;
	/* A count of all the rules that fired for this execution. */
	private int totalRulesFired;
	/*
	 * The sum of the number of objects in the working memory that were
	 * inserted, deleted or modified.
	 */
	private int totalObjectsModified;
	/*
	 * A sequence of rule names, representing the order in which rules fired for
	 * this execution.
	 */
	private final List<String> ruleFiringSequence;
	/* The class names of all the objects that were modified. */
	private final List<String> modifiedObjectClassNameList;
	/* The class names of all the objects that were inserted. */
	private final List<String> insertedObjectClassNameList;
	/* The class names of all the objects that were deleted. */
	private final List<String> deletedObjectClassNameList;

	/**
	 * Constructor to initialize the member lists.
	 */
	public RuleExecutionStatistics() {
		this.ruleFiringSequence = new LinkedList<>();
		this.modifiedObjectClassNameList = new LinkedList<>();
		this.insertedObjectClassNameList = new LinkedList<>();
		this.deletedObjectClassNameList = new LinkedList<>();
	}

	/**
	 * Resets the lists and counts in this statistics object.
	 */
	public void resetStatistics() {
		this.ruleFiringSequence.clear();
		this.modifiedObjectClassNameList.clear();
		this.insertedObjectClassNameList.clear();
		this.deletedObjectClassNameList.clear();
		this.totalRulesFired = 0;
		this.totalObjectsModified = 0;
	}

	/**
	 * @return add one to the totalRulesFired and return the result.
	 */
	public int incrementTotalRulesFired() {
		return ++totalRulesFired;
	}

	/**
	 * @return add one to the totalRulesFired and return the result.
	 */
	public int incrementTotalObjectsModified() {
		return ++totalObjectsModified;
	}

	/**
	 * Adds the fired rule name to the sequence of fired rules.
	 * 
	 * @param ruleName
	 *            the name of the rule
	 */
	public void addRuleFired(final String ruleName) {
		ruleFiringSequence.add(ruleName);
	}

	/**
	 * Adds the inserted object class name to the sequence.
	 * 
	 * @param insertedObject
	 *            the name of the object class
	 */
	public void addInsertedObject(final Object insertedObject) {
		insertedObjectClassNameList.add(insertedObject.getClass().getName());
	}

	/**
	 * Adds the modified object class name to the sequence.
	 * 
	 * @param modifiedObject
	 *            the name of the object class
	 */
	public void addModifiedObject(final Object modifiedObject) {
		modifiedObjectClassNameList.add(modifiedObject.getClass().getName());
	}

	/**
	 * Adds the deleted object class name to the sequence.
	 * 
	 * @param deletedObject
	 *            the name of the object class
	 */
	public void addDeletedObject(final Object deletedObject) {
		deletedObjectClassNameList.add(deletedObject.getClass().getName());
	}

	/**
	 * @return the totalRulesFired
	 */
	public int getTotalRulesFired() {
		return totalRulesFired;
	}

	/**
	 * @param totalRulesFired
	 *            the totalRulesFired to set
	 */
	public void setTotalRulesFired(int totalRulesFired) {
		this.totalRulesFired = totalRulesFired;
	}

	/**
	 * @return the totalObjectsModified
	 */
	public int getTotalObjectsModified() {
		return totalObjectsModified;
	}

	/**
	 * @param totalObjectsModified
	 *            the totalObjectsModified to set
	 */
	public void setTotalObjectsModified(int totalObjectsModified) {
		this.totalObjectsModified = totalObjectsModified;
	}

	/**
	 * @return the ruleFiringSequence
	 */
	public List<String> getRuleFiringSequence() {
		return Collections.unmodifiableList(ruleFiringSequence);
	}

	/**
	 * @return the modifiedObjectClassNameList
	 */
	public List<String> getModifiedObjectClassNameList() {
		return Collections.unmodifiableList(modifiedObjectClassNameList);
	}

	/**
	 * @return the insertedObjectClassNameList
	 */
	public List<String> getInsertedObjectClassNameList() {
		return Collections.unmodifiableList(insertedObjectClassNameList);
	}

	/**
	 * @return the deletedObjectClassNameList
	 */
	public List<String> getDeletedObjectClassNameList() {
		return Collections.unmodifiableList(deletedObjectClassNameList);
	}
}
