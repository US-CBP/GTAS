package gov.gtas.model;

import javax.persistence.*;

@Entity(name = "document_retention_policy_audit")
public class DocumentRetentionPolicyAudit extends BaseEntityRetention {

    @ManyToOne(optional = false)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
