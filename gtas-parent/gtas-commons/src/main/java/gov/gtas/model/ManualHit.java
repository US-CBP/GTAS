/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import gov.gtas.enumtype.HitTypeEnum;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "manual_lookout")
public class ManualHit extends HitMaker {

    private String description;

    public ManualHit() {
        setHitTypeEnum(HitTypeEnum.MANUAL_HIT);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

