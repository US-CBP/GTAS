package gov.gtas.svc;

import gov.gtas.model.Case;
import gov.gtas.model.HitsSummary;

import java.util.List;
import java.util.Set;

public class TargetingServiceResults {

    Set<Case> getCaseSet() {
        return caseSet;
    }

    void setCaseSet(Set<Case> caseSet) {
        this.caseSet = caseSet;
    }

    List<HitsSummary> getHitsSummaryList() {
        return hitsSummaryList;
    }

    void setHitsSummaryList(List<HitsSummary> hitsSummaryList) {
        this.hitsSummaryList = hitsSummaryList;
    }

    private Set<Case> caseSet;
    private List<HitsSummary> hitsSummaryList;
}
