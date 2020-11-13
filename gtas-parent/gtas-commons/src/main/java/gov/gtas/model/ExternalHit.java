package gov.gtas.model;

import gov.gtas.enumtype.HitTypeEnum;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "external_hit")
public class ExternalHit extends HitMaker {

    private String description;

    public ExternalHit() {
        setHitTypeEnum(HitTypeEnum.EXTERNAL_HIT);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
