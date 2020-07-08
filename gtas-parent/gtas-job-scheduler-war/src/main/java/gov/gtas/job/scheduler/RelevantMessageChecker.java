package gov.gtas.job.scheduler;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Passenger;
import gov.gtas.model.Pnr;

import java.util.Date;

public class RelevantMessageChecker {
    private Date apisCutOffDate;
    private Date pnrCutOffDate;
    private Passenger p;
    private boolean relevantAPIS;
    private boolean relevantPnr;

    public RelevantMessageChecker(Date apisCutOffDate, Date pnrCutOffDate, Passenger p) {
        this.apisCutOffDate = apisCutOffDate;
        this.pnrCutOffDate = pnrCutOffDate;
        this.p = p;
    }

    public boolean isRelevantAPIS() {
        return relevantAPIS;
    }

    public boolean isRelevantPnr() {
        return relevantPnr;
    }

    public RelevantMessageChecker invoke() {
        relevantAPIS = false;
        relevantPnr = false;
        for (ApisMessage apis : p.getApisMessage()) {
            if (apis.getCreateDate().after(apisCutOffDate)) {
                relevantAPIS = true;
                break;
            }
        }
        for (Pnr pnr : p.getPnrs()) {
            if (pnr.getCreateDate().after(pnrCutOffDate)) {
                relevantPnr = true;
                break;
            }
        }
        return this;
    }
}

