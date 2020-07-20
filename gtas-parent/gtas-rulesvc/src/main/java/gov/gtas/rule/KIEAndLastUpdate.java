package gov.gtas.rule;

import org.kie.api.KieBase;

import java.util.Date;

public class KIEAndLastUpdate {

    Date updated = new Date(0L);

    public KieBase getKieBase() {
        return kieBase;
    }

    public void setKieBase(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    KieBase kieBase;


    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
