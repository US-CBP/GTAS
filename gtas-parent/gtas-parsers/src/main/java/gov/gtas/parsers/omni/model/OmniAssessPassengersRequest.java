/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * OmniAssessPassengersRequest
 */

public class OmniAssessPassengersRequest {
  @JsonProperty("profiles")
  private List<OmniRawProfile> profiles = new ArrayList<OmniRawProfile>();

  @JsonProperty("message_type")
  private String messageType = "ASSESS_RISK_REQUEST";

   /**
   * Get profiles
   * @return profiles
  **/
  public List<OmniRawProfile> getProfiles() {
    return profiles;
  }

  public void setProfiles(List<OmniRawProfile> profiles) {
    this.profiles = profiles;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OmniAssessPassengersRequest omniAssessPassengersRequest = (OmniAssessPassengersRequest) o;
    return Objects.equals(this.profiles, omniAssessPassengersRequest.profiles) &&
           Objects.equals(this.messageType, omniAssessPassengersRequest.messageType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profiles, messageType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OmniAssessPassengersRequest {\n");
    sb.append("    messageType: ").append(toIndentedString(messageType)).append("\n");
    sb.append("    profiles: ").append(toIndentedString(profiles)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    return OmniDebugPrint.toIndentedString(o);
  }
}
