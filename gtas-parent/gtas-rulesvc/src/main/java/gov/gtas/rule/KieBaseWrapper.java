/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.rule;

import org.kie.api.KieBase;

import java.io.Serializable;

public class KieBaseWrapper implements Serializable {

    private static final long serialVersionUID = -431402518498037033L;

    private final KieBase kieBase;
    KieBaseWrapper(KieBase kieBase) {
        this.kieBase = kieBase;
    }
    public KieBase getKieBase() {
        return kieBase;
    }
}
