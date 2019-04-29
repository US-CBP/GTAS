/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.validators.Validatable;

public class ApisMessageVo extends MessageVo implements Validatable {
    /** type of message: new pax, update, delete, etc */
    private String messageCode;

    private List<ReportingPartyVo> reportingParties = new ArrayList<>();
    private List<PassengerVo> passengers = new ArrayList<>();

    public ApisMessageVo() { }

    public void addBagVo(BagVo b) {bagVos.add(b);}
    public void addFlight(FlightVo f) {
        flights.add(f);
    }
    public void addPax(PassengerVo p) {
        passengers.add(p);
    }
    public void addReportingParty(ReportingPartyVo rp) {
        reportingParties.add(rp);
    }
    public List<PassengerVo> getPassengers() {
        return passengers;
    }
    public List<ReportingPartyVo> getReportingParties() {
        return reportingParties;
    }
    public String getMessageCode() {
        return messageCode;
    }
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public void setReportingParties(List<ReportingPartyVo> reportingParties) {
        this.reportingParties = reportingParties;
    }

    public void setPassengers(List<PassengerVo> passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }

    @Override
    public boolean isValid() {
        if(StringUtils.isBlank(this.getHashCode()) ){
            return false;
        }
        return true;
    }
}
