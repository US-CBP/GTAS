package gov.gtas.model;

import gov.gtas.enumtype.HitTypeEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "external_hit")
public class ExternalHit extends HitMaker {

	@Column(name = "description")
    private String description;

	@Column(name = "lookout")
    private Boolean lookout;

    public ExternalHit() {
        setHitTypeEnum(HitTypeEnum.EXTERNAL_HIT);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getLookout() {
        return lookout;
    }

    public void setLookout(Boolean lookout) {
        this.lookout = lookout;
    }
}
