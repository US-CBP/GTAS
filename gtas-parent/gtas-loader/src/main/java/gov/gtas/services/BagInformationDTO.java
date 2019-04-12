package gov.gtas.services;

import gov.gtas.model.Bag;
import gov.gtas.parsers.vo.BagVo;

import java.util.Set;

public class BagInformationDTO {

    private Set<Bag> existingBags;
    private Set<BagVo> newBags;

    public Set<Bag> getExistingBags() {
        return existingBags;
    }

    public void setExistingBags(Set<Bag> existingBags) {
        this.existingBags = existingBags;
    }

    public Set<BagVo> getNewBags() {
        return newBags;
    }

    public void setNewBags(Set<BagVo> newBags) {
        this.newBags = newBags;
    }
}
