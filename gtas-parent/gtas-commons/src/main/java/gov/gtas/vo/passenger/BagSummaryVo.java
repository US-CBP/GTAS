package gov.gtas.vo.passenger;

import gov.gtas.model.Bag;
import gov.gtas.model.BookingDetail;
import java.util.*;

public class BagSummaryVo {
    private List<BagVo> primeFlightBags = new ArrayList<>();
    private Map<Long, List<BagVo>> bookingDetailBagsMappedByBdId;

    public static BagSummaryVo createFromFlightAndBookingDetails(Set<Bag> bagSet) {
        BagSummaryVo bagSummaryVo = new BagSummaryVo();
        Map<Long, List<BagVo>> bookingDetailBags = new HashMap<>();

        for (Bag bag : bagSet) {
            BagVo bagVo = BagVo.fromBag(bag);
            if (bag.isPrimeFlight()) {
                bagSummaryVo.getPrimeFlightBags().add(bagVo);
            }

            for (BookingDetail detail : bag.getBookingDetail()) {
                if (bookingDetailBags.containsKey(detail.getId())) {
                    bookingDetailBags.get(detail.getId()).add(bagVo);
                } else {
                    List<BagVo> bagVoList = new ArrayList<>();
                    bagVoList.add(bagVo);
                    bookingDetailBags.put(detail.getId(), bagVoList);
                }
            }
        }
        bagSummaryVo.setBookingDetailBagsMappedByBdId(bookingDetailBags);

        return bagSummaryVo;
    }


    public List<BagVo> getPrimeFlightBags() {
        return primeFlightBags;
    }

    public void setPrimeFlightBags(List<BagVo> primeFlightBags) {
        this.primeFlightBags = primeFlightBags;
    }

    public Map<Long, List<BagVo>> getBookingDetailBagsMappedByBdId() {
        return bookingDetailBagsMappedByBdId;
    }

    public void setBookingDetailBagsMappedByBdId(Map<Long, List<BagVo>> bookingDetailBagsMappedByBdId) {
        this.bookingDetailBagsMappedByBdId = bookingDetailBagsMappedByBdId;
    }
}
