package gov.gtas.summary;

import gov.gtas.model.Phone;
import org.springframework.beans.BeanUtils;

public class MessagePhone {
    private String number;

    private String flightIdTag;

    private String messageIdTag;

    public static MessagePhone from(String messageIdTag, String flightIdTag, Phone phone){
        MessagePhone passengerPhone = new MessagePhone();
        BeanUtils.copyProperties(phone, passengerPhone);
        passengerPhone.setMessageIdTag(messageIdTag);
        passengerPhone.setFlightIdTag(flightIdTag);
        return passengerPhone;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFlightIdTag() {
        return flightIdTag;
    }

    public void setFlightIdTag(String flightIdTag) {
        this.flightIdTag = flightIdTag;
    }

    public String getMessageIdTag() {
        return messageIdTag;
    }

    public void setMessageIdTag(String messageIdTag) {
        this.messageIdTag = messageIdTag;
    }
}
