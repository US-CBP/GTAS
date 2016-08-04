/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

/**
 * A Data structure to capture GTAS Loader Run statistics.
 */
public class LoaderStatistics {
	private int numFilesProcessed = 0;
	private int numFilesAborted = 0;
	private int numMessagesProcessed = 0;
	private int numMessagesFailed = 0;

	public int getNumFilesProcessed() {
		return numFilesProcessed;
	}

	/**
	 * Increment num files processed.
	 */
	public void incrementNumFilesProcessed() {
		this.numFilesProcessed++;
	}

	public int getNumFilesAborted() {
		return numFilesAborted;
	}

	/**
	 * Increment num files aborted.
	 */
	public void incrementNumFilesAborted() {
		this.numFilesAborted++;
	}

	public int getNumMessagesProcessed() {
		return numMessagesProcessed;
	}

	/**
	 * Increment num messages processed.
	 *
	 * @param incr
	 *            the incr
	 */
	public void incrementNumMessagesProcessed(int incr) {
		this.numMessagesProcessed += incr;
	}

	public int getNumMessagesFailed() {
		return numMessagesFailed;
	}

	/**
	 * Increment num messages failed.
	 *
	 * @param incr
	 *            the incr
	 */
	public void incrementNumMessagesFailed(int incr) {
		this.numMessagesFailed += incr;
	}

}
