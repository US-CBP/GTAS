/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.model;

import gov.gtas.model.udr.json.QueryObject;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserQueryRequest {
    
    private int id;
    @NotNull(message = "Title is required")
    @Size(max=50)
    private String title;
    @Size(max=250)
    private String description;
    @NotNull(message = "Query is required")
    private QueryObject query;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public QueryObject getQuery() {
        return query;
    }
    
    public void setQuery(QueryObject query) {
        this.query = query;
    }
    
}
