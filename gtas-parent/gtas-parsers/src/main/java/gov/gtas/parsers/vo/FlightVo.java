/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.validators.Validatable;

public class FlightVo implements Validatable {
    private String carrier;
    private String flightNumber;
    private String origin;
    private String destination;
    private Date flightDate;
    private Date etd;
    private Date eta;
    
    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public Date getFlightDate() {
        return flightDate;
    }
    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }
    public Date getEtd() {
        return etd;
    }
    public void setEtd(Date etd) {
        this.etd = etd;
    }
    public Date getEta() {
        return eta;
    }
    public void setEta(Date eta) {
        this.eta = eta;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }

    @Override
    public boolean isValid() {
         return StringUtils.isNotBlank(this.destination) 
                && StringUtils.isNotBlank(this.origin) 
                && StringUtils.isNotBlank(this.flightNumber)
                && this.flightDate != null 
                && StringUtils.isNotBlank(this.carrier);
    }  
}
