/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

import gov.gtas.enumtype.EntityEnum;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for validation.
 */
public class ValidationUtils {
    public static boolean isStringInList(final String target, String... args){
        boolean ret = false;
        if(target != null){
            for(String match:args){
                if(target.equals(match)){
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }
    public static boolean isStringTruthy(final String target){
        return isStringInList(target, "TRUE","True","T","true","Yes","Y","yes","YES");
    }
    
    public static EntityEnum convertStringToEnum(String enumVal){
        if(StringUtils.isNotEmpty(enumVal)){
            for(EntityEnum ent:EntityEnum.values()){
                if(enumVal.equalsIgnoreCase(ent.toString())){
                    return ent;
                }
            }
        }
        return null;
    }
}
