package gov.gtas.rest.response;

import java.util.List;

import gov.gtas.rest.model.Passenger;

public class PassengerResponse extends BaseResponse<Passenger>{
	
	protected List<Passenger> passengers;
	
	

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

	
	
}
