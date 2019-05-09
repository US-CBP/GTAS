package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "case_comment")
public class CaseComment extends BaseEntityAudit  {

    @Column(name = "cc_comment", length = 20000)
    private String comment;

    @Column(name = "cc_type")
    @Enumerated(EnumType.STRING)
    private CommentType commentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cc_id", insertable=false, updatable=false)
    @JsonIgnore
    private Case aCase;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public CommentType getCommentType() {
        return commentType;
    }

    public void setCommentType(CommentType commentType) {
        this.commentType = commentType;
    }

    public Case getaCase() {
        return aCase;
    }

    public void setaCase(Case aCase) {
        this.aCase = aCase;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CaseComment)) return false;

        CaseComment that = (CaseComment) o;
        return Objects.equals(comment, that.comment) &&
                commentType == that.commentType &&
                Objects.equals(getCreatedAt(), that.getCreatedAt()) &&
                Objects.equals(getCreatedBy(), that.getCreatedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment, commentType, getCreatedAt(), getCreatedBy());
    }

    @Override
    public String toString() {
        return "CaseComment{" +
                "comment='" + comment + '\'' +
                ", commentType=" + commentType +
                " createdBy" + getCreatedBy() +
                '}';
    }
}
