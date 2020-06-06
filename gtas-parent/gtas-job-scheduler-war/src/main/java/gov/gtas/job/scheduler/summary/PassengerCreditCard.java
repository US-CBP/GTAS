package gov.gtas.job.scheduler.summary;


import gov.gtas.model.CreditCard;
import org.springframework.beans.BeanUtils;

import java.util.Date;

public class PassengerCreditCard {

    private String cardType;

    private String number;

    private Date expiration;

    private String accountHolder;

    private String accountHolderAddress;

    private String accountHolderPhone;

    private Long flightId;

    private Long passengerId;

    public static PassengerCreditCard from(Long passengerId, CreditCard cc) {
        PassengerCreditCard pcc = new PassengerCreditCard();
        BeanUtils.copyProperties(cc, pcc);
        pcc.setPassengerId(passengerId);
        return pcc;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getAccountHolderAddress() {
        return accountHolderAddress;
    }

    public void setAccountHolderAddress(String accountHolderAddress) {
        this.accountHolderAddress = accountHolderAddress;
    }

    public String getAccountHolderPhone() {
        return accountHolderPhone;
    }

    public void setAccountHolderPhone(String accountHolderPhone) {
        this.accountHolderPhone = accountHolderPhone;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }
}
