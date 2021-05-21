/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.gtas.enumtype.LookoutStatusEnum;

import java.util.Objects;

public class LookoutLaneDTO {

    private Long id;
    private String displayName;
    private Integer ord;
    private LookoutStatusEnum status;


    public LookoutLaneDTO(@JsonProperty("id") Long id, @JsonProperty("displayName") String displayName,
                          @JsonProperty("ord") Integer ord, @JsonProperty("status") LookoutStatusEnum status) {
        this.id = id;
        this.displayName = displayName;
        this.ord = ord;
        this.status = status;
    }


    public Long getId() { return id; }

    public String getDisplayName() { return displayName; }

    public Integer getOrd() { return ord; }

    public LookoutStatusEnum getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LookoutLaneDTO that = (LookoutLaneDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
