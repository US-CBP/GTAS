package gov.gtas.model;

import gov.gtas.enumtype.MessageType;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "passenger_details_from_message")
public class PassengerDetailFromMessage extends BaseEntityAudit {

    @SuppressWarnings("unused")
    public PassengerDetailFromMessage() {
    }

    @ManyToOne
    @JoinColumn(name = "pdfm_message_id", columnDefinition = "bigint unsigned")
    private Message message;

    @Column(name = "pdfm_message_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
    private Long messageId;

    @Column(name = "pdfm_message_type")
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    public PassengerDetailFromMessage(Passenger passenger) {
        this.passenger = passenger;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    @Column(name = "pdfm_passenger_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
    private Long passengerId;

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pdfm_passenger_id")
    Passenger passenger;

    @Column(name = "pdfm_passenger_type", length = 3, nullable = false)
    private String passengerType;

    @Column(name = "pdfm_title")
    private String title;

    @Column(name = "pdfm_first_name")
    private String firstName;

    @Column(name = "pdfm_middle_name")
    private String middleName;

    @Column(name = "pdfm_last_name")
    private String lastName;

    @Column(name = "pdfm_suffix")
    private String suffix;

    @Column(name = "pdfm_gender", length = 2)
    private String gender;

    @Column(name = "pdfm_nationality")
    private String nationality;

    @Column(name = "pdfm_residency_country")
    private String residencyCountry;

    @Temporal(TemporalType.DATE)
    private Date dob;

    /** calculated field */
    @Column(name = "pdfm_age")
    private Integer age;

    @Column(name = "pdfm_deleted", nullable = false)
    private Boolean deleted = Boolean.FALSE;


    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(String passengerType) {
        this.passengerType = passengerType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getResidencyCountry() {
        return residencyCountry;
    }

    public void setResidencyCountry(String residencyCountry) {
        this.residencyCountry = residencyCountry;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassengerDetailFromMessage that = (PassengerDetailFromMessage) o;
        return Objects.equals(messageId, that.messageId) &&
                Objects.equals(passengerId, that.passengerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, passengerId);
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
