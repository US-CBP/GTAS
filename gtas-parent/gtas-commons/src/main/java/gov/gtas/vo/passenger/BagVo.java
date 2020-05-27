/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.Bag;
import gov.gtas.model.BookingDetail;
import gov.gtas.model.PIIObject;

import java.util.HashSet;
import java.util.Set;

public class BagVo  {
	private String bagId;
	private Long flightId;
	private Long bookingDetailId;
	private String data_source;
	private String destination;
	private double average_bag_weight;
	private double bag_weight;
	private int bag_count = 0;
	private Long passengerId;
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

	public static Set<BagVo> fromBags(Set<Bag> bags) {
		Set<BagVo> bagVos = new HashSet<>();
		for (Bag bag : bags) {
			if (bag.isPrimeFlight()) {
				BagVo bagVo = BagVo.fromBag(bag);
				bagVos.add(bagVo);
			}
			for (BookingDetail detail : bag.getBookingDetail()) {
				BagVo bagVo = BagVo.fromBag(bag);
				bagVo.setBookingDetailId(detail.getId());
				bagVos.add(bagVo);
			}
		}
		return bagVos;
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

	public int getBag_count() {
		return bag_count;
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
}
