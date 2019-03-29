/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
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
    /*
    Etd is a field with a timestamp in the parser.
    Importantly the label made on the bucket/thread used in the loader will not be with a timestamp.
    It will be only the date.
    ETD date with a timestamp exist within mutable flight object.
    * */
    private Date etd;
    private Date eta;
    private String marketingFlightNumber;
    private boolean isCodeShareFlight=false;
    private boolean isMarketingFlight=false;
    
    
    public boolean isMarketingFlight() {
		return isMarketingFlight;
	}
	public void setMarketingFlight(boolean isMarketingFlight) {
		this.isMarketingFlight = isMarketingFlight;
	}
	public String getMarketingFlightNumber() {
		return marketingFlightNumber;
	}
	public void setMarketingFlightNumber(String mingFlightNumber) {
		this.marketingFlightNumber = mingFlightNumber;
	}
	public boolean isCodeShareFlight() {
		return isCodeShareFlight;
	}
	public void setCodeShareFlight(boolean isCodeShareFlight) {
		this.isCodeShareFlight = isCodeShareFlight;
	}
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
                && StringUtils.isNotBlank(this.carrier)
                && this.etd != null;
    }  
}
