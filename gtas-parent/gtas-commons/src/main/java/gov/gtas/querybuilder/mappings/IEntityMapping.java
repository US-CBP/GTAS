/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
