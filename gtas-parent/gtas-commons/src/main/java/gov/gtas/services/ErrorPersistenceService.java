/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGE_ADMIN;
import gov.gtas.error.ErrorDetailInfo;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

public interface ErrorPersistenceService {
	public ErrorDetailInfo create(ErrorDetailInfo error);

	public ErrorDetailInfo findById(Long id);

	@PreAuthorize(PRIVILEGE_ADMIN)
	public List<ErrorDetailInfo> findByDateRange(Date dateFrom, Date dateTo);

	public List<ErrorDetailInfo> findByDateFrom(Date dateFrom);

	@PreAuthorize(PRIVILEGE_ADMIN)
	public List<ErrorDetailInfo> findByCode(String code);

}
