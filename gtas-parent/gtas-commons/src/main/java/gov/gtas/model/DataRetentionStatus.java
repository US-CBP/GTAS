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

    public boolean isMaskedAPIS() {
        return maskedAPIS;
    }

    public void setMaskedAPIS(boolean maskedAPIS) {
        this.maskedAPIS = maskedAPIS;
    }

    public boolean isDeletedAPIS() {
        return deletedAPIS;
    }

    public void setDeletedAPIS(boolean deletedAPIS) {
        this.deletedAPIS = deletedAPIS;
    }


    public boolean isMaskedPNR() {
        return maskedPNR;
    }

    public void setMaskedPNR(boolean maskedPNR) {
        this.maskedPNR = maskedPNR;
    }

    public boolean isDeletedPNR() {
        return deletedPNR;
    }

    public void setDeletedPNR(boolean deletedPNR) {
        this.deletedPNR = deletedPNR;
    }
}
