/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

public enum TypeEnum {
    STRING ("string"),
    INTEGER ("integer"),
    LONG("long"),
    DOUBLE ("double"),
    DATE ("date"), 
    TIME ("time"),
    DATETIME ("datetime"),
    BOOLEAN ("boolean");
    
    private String type;

    private TypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    
    public static TypeEnum getEnum(String value) {
        
        for (TypeEnum typeEnum : TypeEnum.values()) {
             if(typeEnum.name().equalsIgnoreCase(value)) {
                 return typeEnum;
             }
         }
        
        throw new IllegalArgumentException();
    }
    
}
