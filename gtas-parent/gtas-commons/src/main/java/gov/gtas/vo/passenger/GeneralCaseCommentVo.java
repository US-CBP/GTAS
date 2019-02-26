package gov.gtas.vo.passenger;

import java.util.Date;
import java.util.Objects;

public class GeneralCaseCommentVo {

    private Date createdAt;

    private String createdBy;

    private Date updatedAt;

    private String updatedBy;

    private String comment;

    public GeneralCaseCommentVo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneralCaseCommentVo that = (GeneralCaseCommentVo) o;
        return getCreatedAt().equals(that.getCreatedAt()) &&
                getCreatedBy().equals(that.getCreatedBy()) &&
                Objects.equals(getUpdatedAt(), that.getUpdatedAt()) &&
                Objects.equals(getUpdatedBy(), that.getUpdatedBy()) &&
                getComment().equals(that.getComment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreatedAt(), getCreatedBy(), getUpdatedAt(), getUpdatedBy(), getComment());
    }

    @Override
    public String toString() {
        return "GeneralCaseCommentVo{" +
                "createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", updatedAt=" + updatedAt +
                ", updatedBy='" + updatedBy + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
