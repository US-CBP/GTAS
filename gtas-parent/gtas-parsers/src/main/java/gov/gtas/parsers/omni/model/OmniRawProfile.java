/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OmniRawProfile {
    @JsonProperty("unique_id")
    private String uniqueId;

    @JsonProperty("passenger_num")
    private Long passengerNumber;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age_bin")
    private String ageBin;

    @JsonProperty("eta")
    private Long eta;

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

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Long getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(Long passengerNumber) {
        this.passengerNumber = passengerNumber;
    }

    public String getAgeBin() {
        return ageBin;
    }

    public void setAgeBin(String ageBin) {
        this.ageBin = ageBin;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getEta() {
        return eta;
    }

    public void setEta(Long eta) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OmniRawProfile {\n");

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
