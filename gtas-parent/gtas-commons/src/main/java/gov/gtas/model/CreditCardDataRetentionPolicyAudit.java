package gov.gtas.model;

import javax.persistence.*;

@Entity(name = "credit_card_data_retention_policy_audit")
public class CreditCardDataRetentionPolicyAudit extends BaseEntityRetention {

    @ManyToOne(optional = false)
    @JoinColumn(name = "credit_card_id", referencedColumnName = "id")
    CreditCard creditCard;

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
