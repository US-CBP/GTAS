package gov.gtas.services;

import gov.gtas.model.Bag;
import gov.gtas.parsers.vo.BagVo;

import java.util.List;

public class BagInformationDTO {

    private List<Bag> existingBags;
    private List<BagVo> newBags;

    public List<Bag> getExistingBags() {
        return existingBags;
    }

    public void setExistingBags(List<Bag> existingBags) {
        this.existingBags = existingBags;
    }

    public List<BagVo> getNewBags() {
        return newBags;
    }

    public void setNewBags(List<BagVo> newBags) {
        this.newBags = newBags;
    }
}
