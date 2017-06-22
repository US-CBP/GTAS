/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilderMapping {

    private String label;
    private List<Column> columns = new ArrayList<>();
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public List<Column> getColumns() {
        return columns;
    }
    
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }
    
}
