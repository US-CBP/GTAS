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
 * OnmiRetrieveUpdateDerogLastRunResponse
 */

public class OmniRetrieveUpdateDerogLastRunResponse {
  @JsonProperty("support_info")
  private OmniSupportInfo supportInfo = null;

  @JsonProperty("last_run")
  private OmniLastRun lastRun = null;

  @JsonProperty("error")
  private OmniErrorResponse error = null;

  @JsonProperty("status")
  private String status = null;


  /**
   * Get supportInfo
   * @return supportInfo
  **/
  public OmniSupportInfo getSupportInfo() {
    return supportInfo;
  }

  public void setSupportInfo(OmniSupportInfo supportInfo) {
    this.supportInfo = supportInfo;
  }

   /**
   * Get lastRun
   * @return lastRun
  **/

  public OmniLastRun getLastRun() {
    return lastRun;
  }

  public void setLastRun(OmniLastRun lastRun) {
    this.lastRun = lastRun;
  }

  /**
   * Get error
   * @return error
   **/
  public OmniErrorResponse getError() {
    return error;
  }

  public void setError(OmniErrorResponse error) {
    this.error = error;
  }

  /**
   * Get status
   * @return status
   **/
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {this.status = status;}


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OmniRetrieveUpdateDerogLastRunResponse retrieveUpdateDerogLastRunResponse = (OmniRetrieveUpdateDerogLastRunResponse) o;
    return Objects.equals(this.supportInfo, retrieveUpdateDerogLastRunResponse.supportInfo) &&
        Objects.equals(this.lastRun, retrieveUpdateDerogLastRunResponse.lastRun) &&
        Objects.equals(this.status, retrieveUpdateDerogLastRunResponse.status) &&
        Objects.equals(this.error, retrieveUpdateDerogLastRunResponse.error);
  }

  @Override
  public int hashCode() {
    return Objects.hash(supportInfo, lastRun, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OmniRetrieveUpdateDerogLastRunResponse {\n");
    sb.append("    supportInfo: ").append(toIndentedString(supportInfo)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    lastRun: ").append(toIndentedString(lastRun)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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
