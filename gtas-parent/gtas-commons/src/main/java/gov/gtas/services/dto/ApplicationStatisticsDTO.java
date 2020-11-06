/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
@Deprecated
@SuppressWarnings("unused") // Data is returned to the front end and IS used to display.
public class ApplicationStatisticsDTO {
	private static final String DATE_FORMAT = "yyyy-MM-dd@HH:mm:ss";

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date lastMessageAnalyzedByDrools;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date lastMessageInSystem;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private Date mostRecentRuleHit;
	private int parsed;
	private int failedParsingCount;
	private int failedLoadCount;
	private int loadedCount;
	private int loadedInNeo4JCount;
	private int analyzedCount;
	private int neoAnalyzedCount;
	private int runningRules;
	private int failedAnalyzedCount;
	private int failedNeo4jCount;
	private int partialAnalyzedCount;
	private int potentialFileIssue;
	private int passengerCount;
	private int totalLoadingParsingErrors;
	private int totalRuleErros;

	public ApplicationStatisticsDTO() {
	}

	public Date getLastMessageAnalyzedByDrools() {
		return lastMessageAnalyzedByDrools;
	}

	public void setLastMessageAnalyzedByDrools(Date lastMessageAnalyzedByDrools) {
		this.lastMessageAnalyzedByDrools = lastMessageAnalyzedByDrools;
	}

	public Date getLastMessageInSystem() {
		return lastMessageInSystem;
	}

	public void setLastMessageInSystem(Date lastMessageInSystem) {
		this.lastMessageInSystem = lastMessageInSystem;
	}

	public Date getMostRecentRuleHit() {
		return mostRecentRuleHit;
	}

	public void setMostRecentRuleHit(Date mostRecentRuleHit) {
		this.mostRecentRuleHit = mostRecentRuleHit;
	}

	public int getParsed() {
		return parsed;
	}

	public void setParsed(int parsed) {
		this.parsed = parsed;
	}

	public int getFailedParsingCount() {
		return failedParsingCount;
	}

	public void setFailedParsingCount(int failedParsingCount) {
		this.failedParsingCount = failedParsingCount;
	}

	public int getFailedLoadCount() {
		return failedLoadCount;
	}

	public void setFailedLoadCount(int failedLoadCount) {
		this.failedLoadCount = failedLoadCount;
	}

	public int getLoadedCount() {
		return loadedCount;
	}

	public void setLoadedCount(int loadedCount) {
		this.loadedCount = loadedCount;
	}

	public int getLoadedInNeo4JCount() {
		return loadedInNeo4JCount;
	}

	public void setLoadedInNeo4JCount(int loadedInNeo4JCount) {
		this.loadedInNeo4JCount = loadedInNeo4JCount;
	}

	public int getAnalyzedCount() {
		return analyzedCount;
	}

	public void setAnalyzedCount(int analyzedCount) {
		this.analyzedCount = analyzedCount;
	}

	public int getNeoAnalyzedCount() {
		return neoAnalyzedCount;
	}

	public void setNeoAnalyzedCount(int neoAnalyzedCount) {
		this.neoAnalyzedCount = neoAnalyzedCount;
	}

	public int getRunningRules() {
		return runningRules;
	}

	public void setRunningRules(int runningRules) {
		this.runningRules = runningRules;
	}

	public int getFailedAnalyzedCount() {
		return failedAnalyzedCount;
	}

	public void setFailedAnalyzedCount(int failedAnalyzedCount) {
		this.failedAnalyzedCount = failedAnalyzedCount;
	}

	public int getFailedNeo4jCount() {
		return failedNeo4jCount;
	}

	public void setFailedNeo4jCount(int failedNeo4jCount) {
		this.failedNeo4jCount = failedNeo4jCount;
	}

	public int getPartialAnalyzedCount() {
		return partialAnalyzedCount;
	}

	public void setPartialAnalyzedCount(int partialAnalyzedCount) {
		this.partialAnalyzedCount = partialAnalyzedCount;
	}

	public int getPotentialFileIssue() {
		return potentialFileIssue;
	}

	public void setPotentialFileIssue(int potentialFileIssue) {
		this.potentialFileIssue = potentialFileIssue;
	}

	public int getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(int passengerCount) {
		this.passengerCount = passengerCount;
	}

	public int getTotalLoadingParsingErrors() {
		totalLoadingParsingErrors = this.failedParsingCount + this.failedLoadCount;
		return totalLoadingParsingErrors;
	}

	public void setTotalLoadingParsingErrors(int totalLoadingParsingErrors) {
		this.totalLoadingParsingErrors = totalLoadingParsingErrors;
	}

	public int getTotalRuleErros() {
		this.totalRuleErros = this.failedNeo4jCount + this.failedAnalyzedCount + this.partialAnalyzedCount;
		return totalRuleErros;
	}

	public void setTotalRuleErros(int totalRuleErros) {
		this.totalRuleErros = totalRuleErros;
	}
	
	
	
}
