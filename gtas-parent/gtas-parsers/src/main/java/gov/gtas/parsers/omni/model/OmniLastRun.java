/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The time the passenger derog information was sent to kaizen
 */
public class OmniLastRun {
  @JsonProperty("time_millisecs")
  private Long timeMillisecs = null;

  public Long getTimeMillisecs() {
    return timeMillisecs;
  }

  public void setTimeMillisecs(Long timeMillisecs) {
    this.timeMillisecs = timeMillisecs;
  }

  /**
   * An integer from the epock, which represents the time GTAS sent derop passenger information updates to Kaizen
   * @return timeMillisecs
   **/

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OmniLastRun omniLastRun = (OmniLastRun) o;
    return Objects.equals(this.timeMillisecs, omniLastRun.timeMillisecs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeMillisecs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OmniLastRun {\n");
    sb.append("    timeMillisecs: ").append(toIndentedString(timeMillisecs)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
