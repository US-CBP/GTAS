/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.model;

import gov.gtas.model.udr.json.QueryObject;


public class UserQueryResult implements IUserQueryResult {

    private int id;
    private String title;
    private String description;
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
