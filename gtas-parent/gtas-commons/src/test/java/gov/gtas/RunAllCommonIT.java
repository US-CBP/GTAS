/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas;

import gov.gtas.repository.FlightRepositoryIT;
import gov.gtas.services.AuditLogPersistenceServiceIT;
import gov.gtas.services.PnrServiceIT;
import gov.gtas.services.RulePersistenceServiceIT;
import gov.gtas.services.UserServiceIT;
import gov.gtas.services.WatchlistPersistenceServiceIT;
import gov.gtas.services.WhitelistPersistenceServiceIT;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AuditLogPersistenceServiceIT.class, RulePersistenceServiceIT.class, UserServiceIT.class,
		PnrServiceIT.class, WatchlistPersistenceServiceIT.class, FlightRepositoryIT.class,
		WhitelistPersistenceServiceIT.class })
public class RunAllCommonIT {
}
