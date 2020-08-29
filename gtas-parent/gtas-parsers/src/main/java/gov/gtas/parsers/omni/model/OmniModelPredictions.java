/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OmniModelPredictions {
    @JsonProperty("passenger_num")
    private Long passengerNumber;

    @JsonProperty("unique_id")
    private String uniqueId;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age_bin")
    private String ageBin;

    @JsonProperty("eta")
    private String eta;

    @JsonProperty("passenger_citizen_country")
    private List<String> passengerCitizenCountry;

    @JsonProperty("documentation_country")
    private List<String> documentationCountry;

    @JsonProperty("documentation_type")
    private List<String> documentationType;

    @JsonProperty("flight_number")
    private String flightNumber;

    @JsonProperty("carrier")
    private String carrier;

    @JsonProperty("flight_origin_country")
    private String flightOriginCountry;

    @JsonProperty("flight_origin_airport")
    private String flightOriginAirport;

    @JsonProperty("flight_arrival_country")
    private String flightArrivalCountry;

    @JsonProperty("flight_arrival_airport")
    private String flightArrivalAirport;

    @JsonProperty("cat1_prob")
    private List<Double> cat1Prob;

    @JsonProperty("cat2_prob")
    private List<Double> cat2Prob;

    @JsonProperty("cat3_prob")
    private List<Double> cat3Prob;

    @JsonProperty("cat4_prob")
    private List<Double> cat4Prob;

    @JsonProperty("cat5_prob")
    private List<Double> cat5Prob;

    @JsonProperty("cat6_prob")
    private List<Double> cat6Prob;

    @JsonProperty("label_any_prob")
    private List<Double> labelAnyProb;

    @JsonProperty("label_any_pred")
    private Double labelAnyPred;

    @JsonProperty("anomaly_pred")
    private Double anomalyPrediction;

    @JsonProperty("anomaly_score")
    private Double anomalyScore;

    public Long getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(Long passengerNumber) {
        this.passengerNumber = passengerNumber;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeBin() {
        return ageBin;
    }

    public void setAgeBin(String ageBin) {
        this.ageBin = ageBin;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public List<String> getPassengerCitizenCountry() {
        return passengerCitizenCountry;
    }

    public void setPassengerCitizenCountry(List<String> passengerCitizenCountry) {
        this.passengerCitizenCountry = passengerCitizenCountry;
    }

    public List<String> getDocumentationCountry() {
        return documentationCountry;
    }

    public void setDocumentationCountry(List<String> documentationCountry) {
        this.documentationCountry = documentationCountry;
    }

    public List<String> getDocumentationType() {
        return documentationType;
    }

    public void setDocumentationType(List<String> documentationType) {
        this.documentationType = documentationType;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFlightOriginCountry() {
        return flightOriginCountry;
    }

    public void setFlightOriginCountry(String flightOriginCountry) {
        this.flightOriginCountry = flightOriginCountry;
    }

    public String getFlightOriginAirport() {
        return flightOriginAirport;
    }

    public void setFlightOriginAirport(String flightOriginAirport) {
        this.flightOriginAirport = flightOriginAirport;
    }

    public String getFlightArrivalCountry() {
        return flightArrivalCountry;
    }

    public void setFlightArrivalCountry(String flightArrivalCountry) {
        this.flightArrivalCountry = flightArrivalCountry;
    }

    public String getFlightArrivalAirport() {
        return flightArrivalAirport;
    }

    public void setFlightArrivalAirport(String flightArrivalAirport) {
        this.flightArrivalAirport = flightArrivalAirport;
    }

    public List<Double> getCat1Prob() {
        return cat1Prob;
    }

    public void setCat1Prob(List<Double> cat1Prob) {
        this.cat1Prob = cat1Prob;
    }

    public List<Double> getCat2Prob() {
        return cat2Prob;
    }

    public void setCat2Prob(List<Double> cat2Prob) {
        this.cat2Prob = cat2Prob;
    }

    public List<Double> getCat3Prob() {
        return cat3Prob;
    }

    public void setCat3Prob(List<Double> cat3Prob) {
        this.cat3Prob = cat3Prob;
    }

    public List<Double> getCat4Prob() {
        return cat4Prob;
    }

    public void setCat4Prob(List<Double> cat4Prob) {
        this.cat4Prob = cat4Prob;
    }

    public List<Double> getCat5Prob() {
        return cat5Prob;
    }

    public void setCat5Prob(List<Double> cat5Prob) {
        this.cat5Prob = cat5Prob;
    }

    public List<Double> getCat6Prob() {
        return cat6Prob;
    }

    public void setCat6Prob(List<Double> cat6Prob) {
        this.cat6Prob = cat6Prob;
    }

    public List<Double> getLabelAnyProb() {
        return labelAnyProb;
    }

    public void setLabelAnyProb(List<Double> labelAnyProb) {
        this.labelAnyProb = labelAnyProb;
    }

    public Double getLabelAnyPred() {
        return labelAnyPred;
    }

    public void setLabelAnyPred(Double labelAnyPred) {
        this.labelAnyPred = labelAnyPred;
    }

    public Double getAnomalyPrediction() {
        return anomalyPrediction;
    }

    public void setAnomalyPrediction(Double anomalyPrediction) {
        this.anomalyPrediction = anomalyPrediction;
    }

    public Double getAnomalyScore() {
        return anomalyScore;
    }

    public void setAnomalyScore(Double anomalyScore) {
        this.anomalyScore = anomalyScore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OmniModelPredictions {\n");

        sb.append(" ======= Passenger and Flight Information =======\n");

        sb.append("    passengerNumber: ").append(toIndentedString(passengerNumber)).append("\n");
        sb.append("    uniqueId: ").append(toIndentedString(uniqueId)).append("\n");
        sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
        sb.append("    ageBin: ").append(toIndentedString(ageBin)).append("\n");
        sb.append("    eta: ").append(toIndentedString(eta)).append("\n");
        sb.append("    passengerCitizenCountry: ").append(toIndentedString(passengerCitizenCountry)).append("\n");
        sb.append("    documentationCountry: ").append(toIndentedString(documentationCountry)).append("\n");
        sb.append("    documentationType: ").append(toIndentedString(documentationType)).append("\n");
        sb.append("    flightNumber: ").append(toIndentedString(flightNumber)).append("\n");
        sb.append("    carrier: ").append(toIndentedString(carrier)).append("\n");
        sb.append("    flightOriginCountry: ").append(toIndentedString(flightOriginCountry)).append("\n");
        sb.append("    flightOriginAirport: ").append(toIndentedString(flightOriginAirport)).append("\n");
        sb.append("    flightArrivalCountry: ").append(toIndentedString(flightArrivalCountry)).append("\n");
        sb.append("    flightArrivalAirport: ").append(toIndentedString(flightArrivalAirport)).append("\n");

        sb.append(" ======= Model Prediction Scores =======\n");

        sb.append("    anomalyScore: ").append(toIndentedString(anomalyScore)).append("\n");
        sb.append("    anomalyPrediction: ").append(toIndentedString(anomalyPrediction)).append("\n");
        sb.append("    labelAnyPred: ").append(toIndentedString(labelAnyPred)).append("\n");
        sb.append("    labelAnyProb: ").append(toIndentedString(labelAnyProb)).append("\n");
        sb.append("    cat1Prob: ").append(toIndentedString(cat1Prob)).append("\n");
        sb.append("    cat2Prob: ").append(toIndentedString(cat2Prob)).append("\n");
        sb.append("    cat3Prob: ").append(toIndentedString(cat3Prob)).append("\n");
        sb.append("    cat4Prob: ").append(toIndentedString(cat4Prob)).append("\n");
        sb.append("    cat5Prob: ").append(toIndentedString(cat5Prob)).append("\n");
        sb.append("    cat6Prob: ").append(toIndentedString(cat6Prob)).append("\n");
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
