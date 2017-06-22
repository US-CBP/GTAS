/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.mappings;

public interface IEntityMapping {
    
    public boolean isDisplayField();
    
    public String getFieldName();

    public String getFriendlyName();

    public String getFieldType();
}
