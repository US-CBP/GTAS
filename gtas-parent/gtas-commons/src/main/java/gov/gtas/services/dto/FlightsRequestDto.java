/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FlightsRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // e.g. 2015-10-02T18:33:03.412Z
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private int pageNumber;
    private int pageSize;
        
    private String flightNumber;
    
    private String direction;
    
    private Set<String> originAirports ;
    
    private Set<String> destinationAirports;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)        
    private Date etaStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)        
    private Date etaEnd;
    
    private List<SortOptionsDto> sort;
    
    public FlightsRequestDto() { }
    
    public int getPageNumber() {
        return pageNumber;
    }
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public Date getEtaStart() {
        return etaStart;
    }
    public void setEtaStart(Date etaStart) {
        this.etaStart = etaStart;
    }
    public Date getEtaEnd() {
        return etaEnd;
    }
    public void setEtaEnd(Date etaEnd) {
        this.etaEnd = etaEnd;
    }        
   
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public List<SortOptionsDto> getSort() {
        return sort;
    }
    public void setSort(List<SortOptionsDto> sort) {
        this.sort = sort;
    }

    public Set<String> getOriginAirports() {
        return originAirports;
    }

    public void setOriginAirports(Set<String> originAirports) {
        this.originAirports = originAirports;
    }

    public Set<String> getDestinationAirports() {
        return destinationAirports;
    }

    public void setDestinationAirports(Set<String> destinationAirports) {
        this.destinationAirports = destinationAirports;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }
}

