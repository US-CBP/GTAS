package gov.gtas.services.dto;


public class AppStatisticsDTO {
	private Long lastMessageAnalyzedByDrools;
	private Long lastMessageInSystem;
	private Long mostRecentRuleHit;
	private int passengerCount;
	private int totalLoadingParsingErrors;
	private int totalRuleErros;
	
	public Long getLastMessageAnalyzedByDrools() {
		return lastMessageAnalyzedByDrools;
	}
	public void setLastMessageAnalyzedByDrools(Long lastMessageAnalyzedByDrools) {
		this.lastMessageAnalyzedByDrools = lastMessageAnalyzedByDrools;
	}
	public Long getLastMessageInSystem() {
		return lastMessageInSystem;
	}
	public void setLastMessageInSystem(Long lastMessageInSystem) {
		this.lastMessageInSystem = lastMessageInSystem;
	}
	public Long getMostRecentRuleHit() {
		return mostRecentRuleHit;
	}
	public void setMostRecentRuleHit(Long mostRecentRuleHit) {
		this.mostRecentRuleHit = mostRecentRuleHit;
	}
	public int getPassengerCount() {
		return passengerCount;
	}
	public void setPassengerCount(int passengerCount) {
		this.passengerCount = passengerCount;
	}
	public int getTotalLoadingParsingErrors() {
		return totalLoadingParsingErrors;
	}
	public void setTotalLoadingParsingErrors(int totalLoadingParsingErrors) {
		this.totalLoadingParsingErrors = totalLoadingParsingErrors;
	}
	public int getTotalRuleErros() {
		return totalRuleErros;
	}
	public void setTotalRuleErros(int totalRuleErros) {
		this.totalRuleErros = totalRuleErros;
	}
	
	

}
