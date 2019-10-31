package gov.gtas.email.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HitEmailDTO {

    private String firstName;
    private String lastName;
    private String flightNumber;
    private Date dob;
    private String gender;
    private UUID passengerUUID;
    private String flightOrigin;
    private String flightDestination;
    private String carrier;
    private List<DocumentDTO> documentDTOs = new ArrayList<>();
    private List<CategoryDTO> categoryDTOs = new ArrayList<>();
    private String timeRemaining;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getFlightOrigin() {
        return flightOrigin;
    }

    public void setFlightOrigin(String flightOrigin) {
        this.flightOrigin = flightOrigin;
    }

    public String getFlightDestination() {
        return flightDestination;
    }

    public void setFlightDestination(String flightDestination) {
        this.flightDestination = flightDestination;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCategoryDTOs(List<CategoryDTO> categoryDTOs) {
        this.categoryDTOs  = categoryDTOs;
    }

    public List<CategoryDTO> getCategoryDTOs() {
        return categoryDTOs;
    }

    public UUID getPassengerUUID() {
        return passengerUUID;
    }

    public void setPassengerUUID(UUID passengerUUID) {
        this.passengerUUID = passengerUUID;
    }

    public void addCategory(CategoryDTO categoryDTO) {
        if(categoryDTOs == null) {
            categoryDTOs = new ArrayList<>();
        }
        categoryDTOs.add(categoryDTO);
    }

    public void addCategory(String severity, String categoryName, String rule) {
        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setSeverity(severity);
        categoryDTO.setCategoryName(categoryName);
        categoryDTO.setRule(rule);

        categoryDTOs.add(categoryDTO);
    }

    public void setDocumentDTOs(List<DocumentDTO> documentDTOs) {
        this.documentDTOs = documentDTOs;
    }

    public List<DocumentDTO> getDocumentDTOs() {
        return documentDTOs;
    }

    public void addDocument(String documentNumber, String documentType) {
        DocumentDTO documentDTO = new DocumentDTO();

        documentDTO.setDocumentNumber(documentNumber);
        documentDTO.setDocumentType(documentType);

        documentDTOs.add(documentDTO);
    }

    public String getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(String timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HitEmailDTO otherDTO = (HitEmailDTO) o;
        return Objects.equals(passengerUUID, otherDTO.getPassengerUUID()) &&
                Objects.equals(flightNumber, otherDTO.getFlightNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(passengerUUID, flightNumber);
    }

}