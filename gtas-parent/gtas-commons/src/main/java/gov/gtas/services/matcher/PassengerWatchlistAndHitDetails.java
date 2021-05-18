package gov.gtas.services.matcher;

import java.util.HashSet;
import java.util.Set;

import gov.gtas.model.HitDetail;
import gov.gtas.model.PassengerWLTimestamp;

public class PassengerWatchlistAndHitDetails {

	Set<PassengerWLTimestamp> savingPassengerSet = new HashSet<>();
	Set<HitDetail> partialWatchlistHits = new HashSet<>();

	PassengerWatchlistAndHitDetails(){}
	PassengerWatchlistAndHitDetails(Set<PassengerWLTimestamp> savingPassengerSet, Set<HitDetail> partialWatchlistHits) {
		this.savingPassengerSet = savingPassengerSet;
		this.partialWatchlistHits = partialWatchlistHits;
	}

	public Set<PassengerWLTimestamp> getSavingPassengerSet() {
		return savingPassengerSet;
	}

	public void setSavingPassengerSet(Set<PassengerWLTimestamp> savingPassengerSet) {
		this.savingPassengerSet = savingPassengerSet;
	}

	public Set<HitDetail> getPartialWatchlistHits() {
		return partialWatchlistHits;
	}

	public void setPartialWatchlistHits(Set<HitDetail> partialWatchlistHits) {
		this.partialWatchlistHits = partialWatchlistHits;
	}

}
