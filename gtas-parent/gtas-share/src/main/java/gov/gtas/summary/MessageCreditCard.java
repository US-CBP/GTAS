package gov.gtas.summary;

import java.util.Date;

public class MessageCreditCard {

    private String cardType;

    private String number;

    private Date expiration;

    private String accountHolder;

    private String accountHolderAddress;

    private String accountHolderPhone;

    private String flightIdTag;

    private String messageIdTag;

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
    public String getMessageIdTag() {
        return messageIdTag;
    }

    public void setMessageIdTag(String messageIdTag) {
        this.messageIdTag = messageIdTag;
    }

    public String getFlightIdTag() {
        return flightIdTag;
    }

    public void setFlightIdTag(String flightIdTag) {
        this.flightIdTag = flightIdTag;
    }
}
