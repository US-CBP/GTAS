/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas;

import gov.gtas.repository.HitsSummaryRepositoryIT;
import gov.gtas.repository.ServiceRepositoryIT;
import gov.gtas.services.AuditLogPersistenceServiceIT;
import gov.gtas.services.DashboardCountsIT;
import gov.gtas.services.PnrServiceIT;
import gov.gtas.services.RulePersistenceServiceIT;
import gov.gtas.services.UserServiceIT;
import gov.gtas.services.WatchlistPersistenceServiceIT;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ServiceRepositoryIT.class, HitsSummaryRepositoryIT.class,
		DashboardCountsIT.class, AuditLogPersistenceServiceIT.class,
		ServiceRepositoryIT.class, RulePersistenceServiceIT.class,
		UserServiceIT.class, PnrServiceIT.class,
		WatchlistPersistenceServiceIT.class })
public class RunAllCommonIT {
}
