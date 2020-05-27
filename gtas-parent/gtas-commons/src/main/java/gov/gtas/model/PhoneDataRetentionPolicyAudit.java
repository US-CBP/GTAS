package gov.gtas.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "phone_data_retention_policy_audit")
public class PhoneDataRetentionPolicyAudit extends BaseEntityRetention {

    @ManyToOne(optional = false)
    @JoinColumn(name = "phone_id", referencedColumnName = "id")
    private Phone phone;

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }
}
