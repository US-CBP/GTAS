/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.enumtype;


public enum CriteriaOperatorEnum {
  EQUAL("==", "is", false),
  NOT_EQUAL("!=", "is not", false),
  GREATER(">", "is greater than", false),
  LESS("<", "is less than", false),
  GREATER_OR_EQUAL(">=", "is greater than or equal to", false),
  LESS_OR_EQUAL("<=", "is lesss than or equal to", false),
  IN("in", "is one of", true),
  NOT_IN("not in", "is not one of", true),
  BETWEEN("", "is between", true),
  NOT_BETWEEN("", "is not between", true),
  BEGINS_WITH("str[startsWith]", "begins with", false),
  NOT_BEGINS_WITH("not matches", "does not begin with", false),
  CONTAINS("matches", "contains", false),
  NOT_CONTAINS("not matches", "does not contain", false),
  ENDS_WITH("str[endsWith]", " ends with", false),
  NOT_ENDS_WITH("not matches", "does not end with", false),
  IS_EMPTY("== null", "is empty", false),
  IS_NOT_EMPTY("!= null", "is not empty", false),
  IS_NULL("== null", "is null", false),
  IS_NOT_NULL("!= null", "is not null", false),
  MEMBER_OF("memberOf", "is member of", true),
  NOT_MEMBER_OF("not memberOf", "is not member of", true);
  
  private final String operatorString;
  private final String displayName;
  private final boolean takesMultipleArguements;
  
  /**
 * @return the operatorString
 */
public String getOperatorString() {
    return operatorString;
}

/**
 * @return the displayName
 */
public String getDisplayName() {
    return displayName;
}

/**
 * @return the takesMultipleArguements
 */
public boolean isTakesMultipleArguements() {
    return takesMultipleArguements;
}

private CriteriaOperatorEnum(final String opString, final String displayName, final boolean isMultivalued){
      this.operatorString = opString;
      this.displayName = displayName;
      this.takesMultipleArguements = isMultivalued;
  }

public static CriteriaOperatorEnum getEnum(String value) {  
    for (CriteriaOperatorEnum opEnum : CriteriaOperatorEnum.values()) {
         if(opEnum.name().equalsIgnoreCase(value)) {
             return opEnum;
         }
     }  
    throw new IllegalArgumentException();
}

}
