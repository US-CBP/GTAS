package gov.gtas.job.scheduler.summary;

import java.util.ArrayList;
import java.util.List;

public class PassengerGroupSummary {
    List<PassengerSummary> passengerSummaryList = new ArrayList<>();

    public List<PassengerSummary> getPassengerSummaryList() {
        return passengerSummaryList;
    }

    public void setPassengerSummaryList(List<PassengerSummary> passengerSummaryList) {
        this.passengerSummaryList = passengerSummaryList;
    }

}
