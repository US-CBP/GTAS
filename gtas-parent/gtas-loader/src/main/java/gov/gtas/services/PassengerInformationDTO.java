package gov.gtas.services;

import gov.gtas.model.BookingDetail;
import gov.gtas.model.Passenger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	private Map<Long, Set<BookingDetail>> bdSet = new HashMap<>();

	public Set<Passenger> getNewPax() {
		return newPax;
	}

	public void setNewPax(Set<Passenger> newPax) {
		this.newPax = newPax;
	}

	public Map<Long, Set<BookingDetail>> getBdSet() {
		return bdSet;
	}

	public void setBdSet(Map<Long, Set<BookingDetail>> bdSet) {
		this.bdSet = bdSet;
	}
}
