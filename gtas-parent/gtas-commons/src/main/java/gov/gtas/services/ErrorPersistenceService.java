/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.error.ErrorDetailInfo;

import java.util.Date;
import java.util.List;

public interface ErrorPersistenceService {
    public ErrorDetailInfo create(ErrorDetailInfo error);
    public ErrorDetailInfo findById(Long id);

    public List<ErrorDetailInfo> findByDateRange(Date dateFrom, Date dateTo);
    public List<ErrorDetailInfo> findByDateFrom(Date dateFrom);
    public List<ErrorDetailInfo> findByCode(String code);

}
