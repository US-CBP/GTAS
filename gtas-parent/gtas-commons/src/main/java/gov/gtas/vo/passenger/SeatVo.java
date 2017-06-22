/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

public class SeatVo {
    private String number;
    private Boolean apis = Boolean.valueOf(false);
    private String flightNumber;
    private String firstName;
    private String lastName;
    
    public String getNumber() {
        return number;
    }
    public Boolean getApis() {
        return apis;
    }
    public void setApis(Boolean apis) {
        this.apis = apis;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
