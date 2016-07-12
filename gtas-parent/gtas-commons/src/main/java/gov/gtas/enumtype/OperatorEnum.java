/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;

public enum OperatorEnum {
    
    EQUAL ("="),
    NOT_EQUAL ("!="),
    IN ("in"),
    NOT_IN ("not in"),
    LESS ("<"),
    LESS_OR_EQUAL ("<="),
    GREATER (">"),
    GREATER_OR_EQUAL (">="),
    BETWEEN ("BETWEEN"),
    BEGINS_WITH ("LIKE"),
    NOT_BEGINS_WITH ("NOT LIKE"),
    CONTAINS ("LIKE"),
    NOT_CONTAINS ("NOT LIKE"),
    ENDS_WITH ("LIKE"),
    NOT_ENDS_WITH ("NOT LIKE"),
    IS_EMPTY ("IS EMPTY"),
    IS_NOT_EMPTY ("IS NOT EMPTY"),
    IS_NULL ("IS NULL"),
    IS_NOT_NULL ("IS NOT NULL");
    
    private String operator;

    private OperatorEnum(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
    
    public static OperatorEnum getEnum(String value) {
        
        for (OperatorEnum opEnum : OperatorEnum.values()) {
             if(opEnum.name().equalsIgnoreCase(value)) {
                 return opEnum;
             }
         }
        
        throw new IllegalArgumentException();
    }

}
