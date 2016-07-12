/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.model;

import gov.gtas.model.User;
import gov.gtas.querybuilder.constants.Constants;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_query")
@NamedQueries({
    @NamedQuery(name = Constants.IS_VALID_USER, query = "select count(u.userId) from User u where u.userId = :userId"),
    @NamedQuery(name = Constants.UNIQUE_TITLE_QUERY, query = "select q.id from UserQuery q where q.deletedDt is null and q.createdBy = :createdBy and q.title = :title"),
    @NamedQuery(name = Constants.LIST_QUERY, query = "select q from UserQuery q where q.createdBy.userId = :createdBy and q.deletedDt is null order by q.id"),
})
public class UserQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
        
    @Column(name = "created_dt", nullable = false)
    private Date createdDt;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    @NotNull
    private User createdBy;
    
    @Column(name = "deleted_dt")
    private Date deletedDt;
    
    @ManyToOne
    @JoinColumn(name = "deleted_by")
    private User deletedBy;
    
    @Column(name = "query_title", length = 20, nullable = false)
    @NotNull
    @Size(min = 1, max = 20)
    private String title;
    
    @Column(name = "query_description", length = 100)
    @Size(max = 100)
    private String description;
    
    @Column(name = "query_text", nullable = false, columnDefinition = "LONGTEXT")
    @NotNull
    @Size(min = 1)
    private String queryText;
    
    public UserQuery() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDeletedDt() {
        return deletedDt;
    }

    public void setDeletedDt(Date deletedDt) {
        this.deletedDt = deletedDt;
    }

    public User getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(User deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

}
