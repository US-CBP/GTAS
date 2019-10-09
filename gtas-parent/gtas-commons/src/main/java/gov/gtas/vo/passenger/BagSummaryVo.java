package gov.gtas.vo.passenger;

import gov.gtas.model.Bag;
import gov.gtas.model.BookingDetail;

import java.util.*;

public class BagSummaryVo {
	private List<BagVo> bagsByFlightLeg = new ArrayList<>();

	public static BagSummaryVo createFromFlightAndBookingDetails(Set<Bag> bagSet) {
		BagSummaryVo bagSummaryVo = new BagSummaryVo();

		for (Bag bag : bagSet) {
			if (bag.isPrimeFlight()) {
				BagVo bagVo = BagVo.fromBag(bag);
				bagSummaryVo.getBagsByFlightLeg().add(bagVo);
			}
			for (BookingDetail detail : bag.getBookingDetail()) {
				BagVo bagVo = BagVo.fromBag(bag);
				bagVo.setBookingDetailId(detail.getId());
				bagSummaryVo.getBagsByFlightLeg().add(bagVo);
			}
		}
		return bagSummaryVo;
	}

	public List<BagVo> getBagsByFlightLeg() {
		return bagsByFlightLeg;
	}

	public void setBagsByFlightLeg(List<BagVo> bagsByFlightLeg) {
		this.bagsByFlightLeg = bagsByFlightLeg;
	}
}
