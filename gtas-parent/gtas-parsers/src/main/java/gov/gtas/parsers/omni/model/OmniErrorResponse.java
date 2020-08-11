/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
/**
 * OmniErrorResponse
 */

public class OmniErrorResponse {

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("title")
  private String title = null;

  @JsonProperty("error_code")
  private Integer errorCode = null;

  /**
   * Get Description
   * @return description
   **/
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get title
   * @return title
   **/
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Get error code
   * @return error code
   **/
  public Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OmniErrorResponse errorResponse = (OmniErrorResponse) o;
    return Objects.equals(this.description, errorResponse.description) &&
            Objects.equals(this.title, errorResponse.title) &&
            Objects.equals(this.errorCode, errorResponse.errorCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, title, errorCode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OmniErrorResponse {\n");

    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
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
