/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.InvalidObjectInfo;
import gov.gtas.repository.ErrorLoggingRepository;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class ErrorLoggingServiceImpl implements ErrorLoggingService {

    @Resource
    ErrorLoggingRepository dao;
    
    @Override
    @Transactional
    public InvalidObjectInfo create(InvalidObjectInfo invalidObjectInfo) {
        return dao.save(invalidObjectInfo);
    }

    @Override
    @Transactional
    public InvalidObjectInfo delete(Long id) {
        InvalidObjectInfo error = this.findById(id);
        dao.delete(error);
        return error;
    }

    @Override
    @Transactional
    public List<InvalidObjectInfo> findAll() {
        return (List<InvalidObjectInfo>) dao.findAll();
    }

    @Override
    @Transactional
    public InvalidObjectInfo update(InvalidObjectInfo info) {
        InvalidObjectInfo i = dao.findOne(info.getId());
        i.setChangeDate();
        i.setFailureDescription(info.getFailureDescription());
        i.setUpdatedBy(info.getUpdatedBy());
        i.setInvalidObjectType(info.getInvalidObjectType());
        i.setInvalidObjectValue(info.getInvalidObjectValue());
        i.setMessageKey(info.getMessageKey());
        return info;
    }

    @Override
    @Transactional
    public InvalidObjectInfo findById(Long id) {
        return dao.findOne(id);
    }
}
