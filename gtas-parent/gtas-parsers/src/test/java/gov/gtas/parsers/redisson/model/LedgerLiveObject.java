/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.redisson.model;

import org.redisson.api.annotation.REntity;
import org.redisson.api.annotation.RId;

@REntity
public class LedgerLiveObject {
    @RId
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
