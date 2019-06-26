/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.enumtype;

/**
 *
 * @author gbays
 */
public enum TripTypeEnum {
    ONEWAY ("ONE-WAY"),
    ROUNDTRIP ("ROUND-TRIP"),
    MULTICITY ("MULTI-CITY"),
    OPENJAW ("OPEN JAW");
    
    private String tripType;
    
    private TripTypeEnum (String type)
    {
       this.tripType = type; 
    }
    
        public static TripTypeEnum getEnum(String value) {
            TripTypeEnum resultType = null; 
            for (TripTypeEnum tripTypeEnum : TripTypeEnum.values()) {
                if(tripTypeEnum.name().equalsIgnoreCase(value)) {
                    resultType = tripTypeEnum;
                }
            }
            return resultType;
       }
        
        public String  toString()
        {
           String returnString = null;
           
           if (this.name().equals("ONEWAY"))
           {
               returnString = "ONE-WAY";
           }
           else if (this.name().equals("ROUNDTRIP"))
           {
              returnString = "ROUND-TRIP"; 
           }
           else if (this.name().equals("MULTICITY"))
           {
             returnString = "MULTI-CITY";
           }
           else if (this.name().equals("OPENJAW"))
           {
             returnString = "OPEN JAW";  
           }
           
           return returnString;
        }

}
