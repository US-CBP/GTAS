package gov.gtas.model;


import javax.persistence.*;

@Entity(name = "address_data_retention_policy_audit")
public class AddressDataRetentionPolicyAudit extends BaseEntityRetention {

    @ManyToOne(optional = false)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
