package gov.gtas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "intra_organization")
public class IntraOrganization extends BaseEntityAudit {

        @Column(name = "intra_organization_name")
        String organizationName;

        public String getOrganizationName() {
                return organizationName;
        }

        public void setOrganizationName(String organizationName) {
                this.organizationName = organizationName;
        }

}
