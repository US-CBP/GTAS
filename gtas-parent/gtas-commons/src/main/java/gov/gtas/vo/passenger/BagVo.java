/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.Bag;
import gov.gtas.model.PIIObject;

public class BagVo implements PIIObject {
	private String bagId;
	private Long flightId;
	private Long bookingDetailId;
	private String data_source;
	private String destination;
	private double average_bag_weight;
	private double bag_weight;
	private int bag_count = 0;
	private Long passengerId;
	private String passFirstName;
	private String passLastName;
	private boolean headPool;
	private boolean isPrime;

	public BagVo() {
	}

	public static BagVo fromBag(Bag bag) {
		BagVo bagVo = new BagVo();
		bagVo.setBagId(bag.getBagId());
		if (bag.getBagMeasurements() != null) {
			if (bag.getBagMeasurements().getWeight() == null) {
				bagVo.setBag_weight(0);
			} else {
				bagVo.setBag_weight(bag.getBagMeasurements().getWeight());
			}
			if (bag.getBagMeasurements().getBagCount() == null) {
				bagVo.setBag_count(0);
			} else {
				bagVo.setBag_count(bag.getBagMeasurements().getBagCount());
			}
		}
		bagVo.setPassengerId(bag.getPassengerId());
		bagVo.setData_source(bag.getData_source());
		bagVo.setDestination(bag.getDestinationAirport());
		bagVo.setPrime(bag.isPrimeFlight());
		bagVo.setHeadPool(bag.isHeadPool());
		bagVo.setFlightId(bag.getFlight().getId());
		return bagVo;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getBagId() {
		return bagId;
	}

	public void setBagId(String bagId) {
		this.bagId = bagId;
	}

	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public Long getBookingDetailId() {
		return bookingDetailId;
	}

	public void setBookingDetailId(Long bookingDetailId) {
		this.bookingDetailId = bookingDetailId;
	}

	public String getData_source() {
		return data_source;
	}

	public void setData_source(String data_source) {
		this.data_source = data_source;
	}

	public void setAverage_bag_weight(double average_bag_weight) {
		this.average_bag_weight = average_bag_weight;
	}

	public void setBag_weight(double bag_weight) {
		this.bag_weight = bag_weight;
	}

	public void setBag_count(int bag_count) {
		this.bag_count = bag_count;
	}

	public void setPassFirstName(String passFirstName) {
		this.passFirstName = passFirstName;
	}

	public int getBag_count() {
		return bag_count;
	}

	public String getPassFirstName() {
		return passFirstName;
	}

	public String getPassLastName() {
		return passLastName;
	}

	public void setPassLastName(String passLastName) {
		this.passLastName = passLastName;
	}

	public double getBag_weight() {
		return bag_weight;
	}

	public double getAverage_bag_weight() {
		return average_bag_weight;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public boolean isHeadPool() {
		return headPool;
	}

	public void setHeadPool(boolean headPool) {
		this.headPool = headPool;
	}

	public boolean isPrime() {
		return isPrime;
	}

	public void setPrime(boolean prime) {
		isPrime = prime;
	}

	@Override
	public PIIObject deletePII() {
		this.passFirstName = "DELETED";
		this.passLastName = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.passFirstName = "MASKED";
		this.passLastName = "MASKED";
		return this;
	}
}
