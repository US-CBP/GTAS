/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.Filter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilterData {

    private static final long serialVersionUID = 1L;
    // e.g. 2015-10-02T18:33:03.412Z
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private final String userId;
    private final String flightDirection;
    private Set<String> originAirports = new HashSet<String>();
    private Set<String> destinationAirports = new HashSet<String>();
    private final int etaStart;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private final int etaEnd;

    public FilterData() {
        userId = null;
        flightDirection = null;
        etaStart = 0;
        etaEnd = 0;
    }

    /**
     * 
     * @param userId
     * @param flightDirection
     * @param originAirports
     * @param destinationAirports
     * @param etaStart
     * @param etaEnd
     */

    public FilterData(@JsonProperty("userId") String userId, @JsonProperty("flightDirection") String flightDirection,
            @JsonProperty("originAirports") Set<String> originAirports,
            @JsonProperty("destinationAirports") Set<String> destinationAirports,
            @JsonProperty("etaStart") int etaStart, @JsonProperty("etaEnd") int etaEnd) {
        this.userId = userId;
        this.flightDirection = flightDirection;
        this.originAirports = originAirports;
        this.destinationAirports = destinationAirports;
        this.etaStart = etaStart;
        this.etaEnd = etaEnd;
    }

    @JsonProperty("userId")
    public final String getUserId() {
        return userId;
    }

    @JsonProperty("flightDirection")
    public final String getFlightDirection() {
        return flightDirection;
    }

    @JsonProperty("destinationAirports")
    public final Set<String> getDestinationAirports() {
        return destinationAirports;
    }

    @JsonProperty("originAirports")
    public final Set<String> getOriginAirports() {
        return originAirports;
    }

    @JsonProperty("etaStart")
    public final int getEtaStart() {
        return etaStart;
    }

    @JsonProperty("etaEnd")
    public final int getEtaEnd() {
        return etaEnd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.flightDirection, this.userId, this.destinationAirports, this.originAirports,
                this.etaStart, this.etaEnd);
    }

    @Override
    public boolean equals(Object target) {

        if (this == target) {
            return true;
        }

        if (!(target instanceof FilterData)) {
            return false;
        }

        FilterData dataTarget = ((FilterData) target);

        return new EqualsBuilder().append(this.etaStart, dataTarget.getEtaStart())
                .append(this.etaEnd, dataTarget.getEtaEnd()).append(this.originAirports, dataTarget.getOriginAirports())
                .append(this.flightDirection, dataTarget.getFlightDirection())
                .append(this.destinationAirports, dataTarget.getDestinationAirports())
                .append(this.userId, dataTarget.getUserId()).isEquals();
    }

    @Override
    public String toString() {
        return "Filter [  eta Start =" + etaStart + ", eta End =" + etaEnd + ", Origin Airports=" + originAirports
                + ", Desitnation Airports=" + destinationAirports + ", User =" + userId + ", Flight Direction="
                + flightDirection + "]";
    }

}
