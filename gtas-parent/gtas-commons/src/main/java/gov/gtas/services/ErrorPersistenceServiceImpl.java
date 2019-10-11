/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.error.BasicErrorDetailInfo;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.model.ErrorRecord;
import gov.gtas.repository.ErrorRecordRepository;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
@Service
public class ErrorPersistenceServiceImpl implements ErrorPersistenceService{
    @Resource
    private ErrorRecordRepository errorRecordRepository;
    
    @Override
    public ErrorDetailInfo create(ErrorDetailInfo error) {
        ErrorRecord err =  errorRecordRepository.save(new ErrorRecord(error));
        return new BasicErrorDetailInfo(err.getId(), err.getCode(), err.getTimestamp(), err.getDescription(), err.fetchErrorDetails());
    }

    @Override
    public ErrorDetailInfo findById(Long id) {
        ErrorRecord err =  errorRecordRepository.findById(id).orElse(null);
        assert err != null;
        return new BasicErrorDetailInfo(err.getId(), err.getCode(), err.getTimestamp(), err.getDescription(), err.fetchErrorDetails());
    }

    @Override
    public List<ErrorDetailInfo> findByDateRange(Date fromDate,
            Date toDate) {
        return convert(errorRecordRepository.findByTimestampRange(fromDate, toDate));
    }

    @Override
    public List<ErrorDetailInfo> findByDateFrom(Date fromDate) {
        return convert(errorRecordRepository.findByTimestampFrom(fromDate));
    }

    @Override
    public List<ErrorDetailInfo> findByCode(String code) {
        return convert(errorRecordRepository.findByCode(code));
    }

    private List<ErrorDetailInfo> convert(List<ErrorRecord> lst){
        List<ErrorDetailInfo> ret;
        if(!CollectionUtils.isEmpty(lst)){
            ret = lst.stream().map(
                                   (ErrorRecord e)->
                                        new BasicErrorDetailInfo(e.getId(), e.getCode(), e.getTimestamp(), e.getDescription(), e.fetchErrorDetails()))
                              .collect(Collectors.toList());
        } else {
            ret = new LinkedList<ErrorDetailInfo>();
        }
        return ret;
        
    }
}
