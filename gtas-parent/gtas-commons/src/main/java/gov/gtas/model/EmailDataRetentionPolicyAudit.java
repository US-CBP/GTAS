package gov.gtas.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "email_data_retention_policy_audit")
public class EmailDataRetentionPolicyAudit extends  BaseEntityRetention {

    @ManyToOne(optional = false)
    @JoinColumn(name = "email_id", referencedColumnName = "id")
    private Email email;

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

}
