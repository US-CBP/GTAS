/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.gtas.vo.passenger.DocumentVo;

import java.util.Date;
import java.util.Objects;

public class LookoutStatusDTO {

    private Long paxId; //For updating POEStatus on the back end
    private String paxFirstName;
    private String paxLastName;
    private DocumentVo document;
    private String  hitCategory; //Reason
    private Date flightCountdownTime;
    private String poeStatus; //Ties tile to lane

    public LookoutStatusDTO(@JsonProperty("paxId") Long paxId, @JsonProperty("paxFirstName") String paxFirstName, @JsonProperty("paxLastName") String paxLastName,
                            @JsonProperty("document") DocumentVo document, @JsonProperty("hitCategory") String hitCategory,
                            @JsonProperty("flightCountdownTime") Date flightCountdownTime, @JsonProperty("poeStatus") String poeStatus) {
        this.paxId = paxId;
        this.paxFirstName = paxFirstName;
        this.paxLastName = paxLastName;
        this.document = document;
        this.hitCategory = hitCategory;
        this.flightCountdownTime = flightCountdownTime;
        this.poeStatus = poeStatus;
    }

    public Long getPaxId() {
        return paxId;
    }

    public String getPaxFirstName() {
        return paxFirstName;
    }

    public String getPaxLastName() {
        return paxLastName;
    }

    public DocumentVo getDocument() { return document; }

    public String getHitCategory() {
        return hitCategory;
    } //TODO: RANDOM IF MULTIPLES

    public Date getFlightCountdownTime() {
        return flightCountdownTime;
    }

    public String getPoeStatus() {
        return poeStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LookoutStatusDTO that = (LookoutStatusDTO) o;
        return paxId.equals(that.paxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paxId);
    }
}
