package gov.gtas.email.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class HitEmailDTO {

    private String firstName;
    private String lastName;
    private String flightNumber;
    private Date dob;
    private String gender;
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

    public void addCategory(String severity, String category, String rule) {
        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setSeverity(severity);
        categoryDTO.setCategory(category);
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

        HitEmailDTO that = (HitEmailDTO) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(flightNumber, that.flightNumber) &&
                Objects.equals(dob, that.dob) &&
                Objects.equals(gender, that.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, flightNumber, dob, gender);
    }

}