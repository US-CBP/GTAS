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
 * OnmiAssessPassengersResponse
 */

public class OmniAssessPassengersResponse {
  @JsonProperty("support_info")
  private OmniSupportInfo supportInfo = null;

  @JsonProperty("predictions")
  private List<OmniModelPredictions> predictions = new ArrayList<OmniModelPredictions>();

  @JsonProperty("start_prediction_time_millisecs")
  private Long startPredictionTimeMillisecs = null;

  @JsonProperty("end_prediction_time_millisecs")
  private Long endPredictionTimeMillisecs = null;

  @JsonProperty("prediction_time_millisecs")
  private Long predictionTimeMillisecs = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("error")
  private OmniErrorResponse error = null;

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
   * Get predictions
   * @return predictions
  **/
  public List<OmniModelPredictions> getPredictions() {
    return predictions;
  }

  public void setPredictions(List<OmniModelPredictions> predictions) {
    this.predictions = predictions;
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

  /**
   * Get startPredictionTimeMillisecs
   * @return startPredictionTimeMillisecs
   **/
  public Long getStartPredictionTimeMillisecs() {
    return startPredictionTimeMillisecs;
  }

  public void setStartPredictionTimeMillisecs(Long startPredictionTimeMillisecs) {
    this.startPredictionTimeMillisecs = startPredictionTimeMillisecs;
  }

  /**
   * Get endPredictionTimeMillisecs
   * @return endPredictionTimeMillisecs
   **/
  public Long getEndPredictionTimeMillisecs() {
    return endPredictionTimeMillisecs;
  }

  public void setEndPredictionTimeMillisecs(Long endPredictionTimeMillisecs) {
    this.endPredictionTimeMillisecs = endPredictionTimeMillisecs;
  }

  /**
   * Get predictionTimeMillisecs
   * @return predictionTimeMillisecs
   **/
  public Long getPredictionTimeMillisecs() {
    return predictionTimeMillisecs;
  }

  public void setPredictionTimeMillisecs(Long predictionTimeMillisecs) {
    this.predictionTimeMillisecs = predictionTimeMillisecs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OmniAssessPassengersResponse assessPassengersResponse = (OmniAssessPassengersResponse) o;
    return Objects.equals(this.supportInfo, assessPassengersResponse.supportInfo) &&
        Objects.equals(this.predictions, assessPassengersResponse.predictions) &&
        Objects.equals(this.status, assessPassengersResponse.status) &&
        Objects.equals(this.error, assessPassengersResponse.error) &&
        Objects.equals(this.startPredictionTimeMillisecs, assessPassengersResponse.startPredictionTimeMillisecs) &&
        Objects.equals(this.endPredictionTimeMillisecs, assessPassengersResponse.endPredictionTimeMillisecs) &&
        Objects.equals(this.predictionTimeMillisecs, assessPassengersResponse.predictionTimeMillisecs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(supportInfo, predictions, status, error, startPredictionTimeMillisecs,
            endPredictionTimeMillisecs, predictionTimeMillisecs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OmniAssessPassengersResponse {\n");
    sb.append("    supportInfo: ").append(toIndentedString(supportInfo)).append("\n");
    sb.append("    predictions: ").append(toIndentedString(predictions)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    startPredictionTimeMillisecs: ").append(toIndentedString(startPredictionTimeMillisecs)).append("\n");
    sb.append("    endPredictionTimeMillisecs: ").append(toIndentedString(endPredictionTimeMillisecs)).append("\n");
    sb.append("    predictionTimeMillisecs: ").append(toIndentedString(predictionTimeMillisecs)).append("\n");
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
