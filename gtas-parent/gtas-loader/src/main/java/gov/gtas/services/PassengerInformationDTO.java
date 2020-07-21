package gov.gtas.services;

import gov.gtas.model.Passenger;

import java.util.HashSet;
import java.util.Set;

public class PassengerInformationDTO {

	private Set<Passenger> newPax = new HashSet<>();

	public Set<Passenger> getOldPax() {
		return oldPax;
	}

	public void setOldPax(Set<Passenger> oldPax) {
		this.oldPax = oldPax;
	}

	private Set<Passenger> oldPax = new HashSet<>();

	public Set<Passenger> getNewPax() {
		return newPax;
	}

	public void setNewPax(Set<Passenger> newPax) {
		this.newPax = newPax;
	}

}
