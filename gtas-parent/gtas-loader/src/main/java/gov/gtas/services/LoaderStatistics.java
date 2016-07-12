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
    public void incrementNumFilesProcessed() {
        this.numFilesProcessed++;
    }
    public int getNumFilesAborted() {
        return numFilesAborted;
    }
    public void incrementNumFilesAborted() {
        this.numFilesAborted++;
    }
    public int getNumMessagesProcessed() {
        return numMessagesProcessed;
    }
    public void incrementNumMessagesProcessed(int incr) {
        this.numMessagesProcessed += incr;
    }
    public int getNumMessagesFailed() {
        return numMessagesFailed;
    }
    public void incrementNumMessagesFailed(int incr) {
        this.numMessagesFailed += incr;
    }

}
