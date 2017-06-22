/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.querybuilder.model;

public class QueryPassengerResult extends BaseQueryResult {

    private String firstName;
    private String lastName;
    private String passengerType;
    private String gender;
    private String dob;
    private String citizenship;
    private String documentNumber;
    private String documentType;
    private String documentIssuanceCountry;
    private String seat;
    private boolean isRuleHit;
    private boolean isOnWatchList;
    
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
    
    public String getPassengerType() {
        return passengerType;
    }
    
    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getDob() {
        return dob;
    }
    
    public void setDob(String dob) {
        this.dob = dob;
    }
    
    public String getCitizenship() {
        return citizenship;
    }
    
    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }
    
    public String getDocumentNumber() {
        return documentNumber;
    }
    
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    public String getDocumentIssuanceCountry() {
        return documentIssuanceCountry;
    }

    public void setDocumentIssuanceCountry(String documentIssuanceCountry) {
        this.documentIssuanceCountry = documentIssuanceCountry;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public boolean isRuleHit() {
        return isRuleHit;
    }
    
    public void setRuleHit(boolean isRuleHit) {
        this.isRuleHit = isRuleHit;
    }

    public boolean isOnWatchList() {
        return isOnWatchList;
    }

    public void setOnWatchList(boolean isOnWatchList) {
        this.isOnWatchList = isOnWatchList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((citizenship == null) ? 0 : citizenship.hashCode());
        result = prime * result + ((dob == null) ? 0 : dob.hashCode());
        result = prime
                * result
                + ((documentIssuanceCountry == null) ? 0
                        : documentIssuanceCountry.hashCode());
        result = prime * result
                + ((documentNumber == null) ? 0 : documentNumber.hashCode());
        result = prime * result
                + ((documentType == null) ? 0 : documentType.hashCode());
        result = prime * result
                + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + (isOnWatchList ? 1231 : 1237);
        result = prime * result + (isRuleHit ? 1231 : 1237);
        result = prime * result
                + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result
                + ((passengerType == null) ? 0 : passengerType.hashCode());
        result = prime * result + ((seat == null) ? 0 : seat.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryPassengerResult other = (QueryPassengerResult) obj;
        if (citizenship == null) {
            if (other.citizenship != null)
                return false;
        } else if (!citizenship.equals(other.citizenship))
            return false;
        if (dob == null) {
            if (other.dob != null)
                return false;
        } else if (!dob.equals(other.dob))
            return false;
        if (documentIssuanceCountry == null) {
            if (other.documentIssuanceCountry != null)
                return false;
        } else if (!documentIssuanceCountry
                .equals(other.documentIssuanceCountry))
            return false;
        if (documentNumber == null) {
            if (other.documentNumber != null)
                return false;
        } else if (!documentNumber.equals(other.documentNumber))
            return false;
        if (documentType == null) {
            if (other.documentType != null)
                return false;
        } else if (!documentType.equals(other.documentType))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (gender == null) {
            if (other.gender != null)
                return false;
        } else if (!gender.equals(other.gender))
            return false;
        if (isOnWatchList != other.isOnWatchList)
            return false;
        if (isRuleHit != other.isRuleHit)
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (passengerType == null) {
            if (other.passengerType != null)
                return false;
        } else if (!passengerType.equals(other.passengerType))
            return false;
        if (seat == null) {
            if (other.seat != null)
                return false;
        } else if (!seat.equals(other.seat))
            return false;
        return true;
    }

}
