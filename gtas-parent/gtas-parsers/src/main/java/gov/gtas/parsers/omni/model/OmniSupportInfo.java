/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Information included in responses to aid in debugging and tracking
 */
public class OmniSupportInfo {
  @JsonProperty("internal_trace_id")
  private String internalTraceId = null;

  /**
   * A unique, internal id generated for async requests that can be used to trace a request through the system
   * @return internalTraceId
  **/
  public String getInternalTraceId() {
    return internalTraceId;
  }

  public void setInternalTraceId(String internalTraceId) {
    this.internalTraceId = internalTraceId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OmniSupportInfo supportInfo = (OmniSupportInfo) o;
    return Objects.equals(this.internalTraceId, supportInfo.internalTraceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(internalTraceId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OmniSupportInfo {\n");
    sb.append("    internalTraceId: ").append(toIndentedString(internalTraceId)).append("\n");
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
