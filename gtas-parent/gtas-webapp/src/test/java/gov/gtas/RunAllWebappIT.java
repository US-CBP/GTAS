/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas;

import gov.gtas.controller.UdrBuilderControllerIT;
import gov.gtas.controller.WatchlistManagementControllerIT;
import gov.gtas.security.SecurityUserDetailsServiceIT;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ UdrBuilderControllerIT.class,
		WatchlistManagementControllerIT.class,
		SecurityUserDetailsServiceIT.class })
public class RunAllWebappIT {
}
