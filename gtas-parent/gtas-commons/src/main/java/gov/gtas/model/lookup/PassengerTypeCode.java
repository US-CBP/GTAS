/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

public enum PassengerTypeCode {
    P("Passenger"), // passenger
    C("Crew"), // crew
    I("In-transit Passenger");  // In-transit pax
    
    private String passengerTypeName;
    private PassengerTypeCode(final String name){
        this.passengerTypeName = name;
    }
    /**
     * @return the passengerTypeName
     */
    public String getPassengerTypeName() {
        return passengerTypeName;
    }
    
}
