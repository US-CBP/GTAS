/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas;

import gov.gtas.services.ApisMessageServiceIT;
import gov.gtas.services.PnrMessageServiceIT;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ApisMessageServiceIT.class, PnrMessageServiceIT.class })
public class RunAllLoaderIT {
}
