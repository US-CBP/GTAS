package gov.gtas.model;

import gov.gtas.enumtype.RetentionPolicyAction;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntityRetention extends BaseEntityAudit {

    @Column(name = "description")
    private String description;

    @Column(name = "action")
    @Enumerated(value = EnumType.STRING)
    private RetentionPolicyAction retentionPolicyAction;

    @Column(name = "guuid")
    private UUID guid = UUID.randomUUID();
    public String getDescription() {
        return description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntityRetention that = (BaseEntityRetention) o;
        return Objects.equals(guid, that.guid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RetentionPolicyAction getRetentionPolicyAction() {
        return retentionPolicyAction;
    }

    public void setRetentionPolicyAction(RetentionPolicyAction retentionPolicyAction) {
        this.retentionPolicyAction = retentionPolicyAction;
    }

    public UUID getGuid() {
        return guid;
    }

    public void setGuid(UUID guid) {
        this.guid = guid;
    }
}
