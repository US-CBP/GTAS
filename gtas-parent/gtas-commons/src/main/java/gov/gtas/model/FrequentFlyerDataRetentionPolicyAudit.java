package gov.gtas.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "frequent_flyer_data_retention_policy_audit")
public class FrequentFlyerDataRetentionPolicyAudit extends BaseEntityRetention{

    @ManyToOne(optional = false)
    @JoinColumn(name = "frequent_flyer_id", referencedColumnName = "id")
    private FrequentFlyer frequentFlyer;

    public FrequentFlyer getFrequentFlyer() {
        return frequentFlyer;
    }

    public void setFrequentFlyer(FrequentFlyer frequentFlyer) {
        this.frequentFlyer = frequentFlyer;
    }
}
