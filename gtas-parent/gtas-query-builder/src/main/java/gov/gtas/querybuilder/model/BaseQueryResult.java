/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.model;

public abstract class BaseQueryResult implements IQueryResult {

	private Long id;
	private String flightNumber;
	private String carrierCode;
	private String origin;
	private String destination;
	private String departureDt;
	private String arrivalDt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFlightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDepartureDt() {
		return departureDt;
	}

	public void setDepartureDt(String departureDt) {
		this.departureDt = departureDt;
	}

	public String getArrivalDt() {
		return arrivalDt;
	}

	public void setArrivalDt(String arrivalDt) {
		this.arrivalDt = arrivalDt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrivalDt == null) ? 0 : arrivalDt.hashCode());
		result = prime * result + ((carrierCode == null) ? 0 : carrierCode.hashCode());
		result = prime * result + ((departureDt == null) ? 0 : departureDt.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((flightNumber == null) ? 0 : flightNumber.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseQueryResult other = (BaseQueryResult) obj;
		if (arrivalDt == null) {
			if (other.arrivalDt != null)
				return false;
		} else if (!arrivalDt.equals(other.arrivalDt))
			return false;
		if (carrierCode == null) {
			if (other.carrierCode != null)
				return false;
		} else if (!carrierCode.equals(other.carrierCode))
			return false;
		if (departureDt == null) {
			if (other.departureDt != null)
				return false;
		} else if (!departureDt.equals(other.departureDt))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (flightNumber == null) {
			if (other.flightNumber != null)
				return false;
		} else if (!flightNumber.equals(other.flightNumber))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		return true;
	}

}
