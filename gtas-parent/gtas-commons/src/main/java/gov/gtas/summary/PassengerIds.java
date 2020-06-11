package gov.gtas.summary;


import gov.gtas.model.PassengerIDTag;
import org.springframework.beans.BeanUtils;

public class PassengerIds {

    private String idTag;

    private String tamrId;

    private Long pax_id;

    public static PassengerIds from(PassengerIDTag passengerIDTag) {
        PassengerIds pids = new PassengerIds();
        if (passengerIDTag == null) {
            return pids;
        }
        pids.setIdTag(passengerIDTag.getIdTag());
        pids.setPax_id(passengerIDTag.getPax_id());
        pids.setTamrId(passengerIDTag.getTamrId());
        return pids;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }

    public String getTamrId() {
        return tamrId;
    }

    public void setTamrId(String tamrId) {
        this.tamrId = tamrId;
    }

    public Long getPax_id() {
        return pax_id;
    }

    public void setPax_id(Long pax_id) {
        this.pax_id = pax_id;
    }
}
