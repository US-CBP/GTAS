package gov.gtas.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "data_retention_status")
public class DataRetentionStatus extends BaseEntityAudit {

    @SuppressWarnings("unused")
    public DataRetentionStatus() {
    }

    public DataRetentionStatus(Passenger passenger) {
        this.passenger = passenger;
        this.setCreatedAt(new Date());
        this.setCreatedBy("LOADER");
    }

    @Column(name = "drs_passenger_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
    private Long passengerId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "drs_passenger_id")
    Passenger passenger;

    @Column(name = "drs_masked_apis")
    private boolean maskedAPIS = false;

    @Column(name = "drs_deleted_apis")
    private boolean deletedAPIS = false;

    @Column(name = "drs_masked_pnr")
    boolean maskedPNR = false;

    @Column(name = "drs_deleted_PNR")
    boolean deletedPNR = false;

    @Column(name = "drs_has_apis_message")
    private boolean hasApisMessage = false;

    @Column(name = "drs_has_pnr_message")
    private boolean hasPnrMessage = false;


    public boolean isHasApisMessage() {
        return hasApisMessage;
    }

    public void setHasApisMessage(boolean hasApisMessage) {
        this.hasApisMessage = hasApisMessage;
    }

    public boolean isHasPnrMessage() {
        return hasPnrMessage;
    }

    public boolean requiresMaskedPnrAndApisMessage() {
        return !(hasUnmaskedPnr() && hasUnmaskedAPIS());
    }

    private boolean hasUnmaskedAPIS() {
        return !requiresMaskedAPIS() && isHasApisMessage();
    }

    private boolean hasUnmaskedPnr() {
        return !requiresMaskedPNR() && isHasPnrMessage();
    }

    public boolean requiresDeletedPnrAndApisMessage() {
        return !((!requiresDeletedPNR() && isHasPnrMessage()) && (!requiresDeletedAPIS() && isHasApisMessage()));
    }

    public void setHasPnrMessage(boolean hasPnrMessage) {
        this.hasPnrMessage = hasPnrMessage;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }


    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public boolean requiresMaskedAPIS() {
        return maskedAPIS;
    }

    public void setMaskedAPIS(boolean maskedAPIS) {
        this.maskedAPIS = maskedAPIS;
    }

    public boolean requiresDeletedAPIS() {
        return deletedAPIS;
    }

    public void setDeletedAPIS(boolean deletedAPIS) {
        this.deletedAPIS = deletedAPIS;
    }


    public boolean requiresMaskedPNR() {
        return maskedPNR;
    }

    public void setMaskedPNR(boolean maskedPNR) {
        this.maskedPNR = maskedPNR;
    }

    public boolean requiresDeletedPNR() {
        return deletedPNR;
    }

    public void setDeletedPNR(boolean deletedPNR) {
        this.deletedPNR = deletedPNR;
    }
}
