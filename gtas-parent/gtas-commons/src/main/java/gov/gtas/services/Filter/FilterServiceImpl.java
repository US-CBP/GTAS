/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.Filter;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.Filter;
import gov.gtas.repository.FilterRepository;

@Service
public class FilterServiceImpl implements FilterService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    FilterServiceUtil filterServiceUtil;
    @Resource
    FilterRepository filterRepository;

    @Override
    @Transactional
    public FilterData create(FilterData filterData) {
        // TODO Auto-generated method stub
        Filter filterEntity = filterServiceUtil.mapFilterEntityFromFilterData(filterData);

        Filter newFilterEntity = filterRepository.save(filterEntity);
        FilterData newFilter = filterServiceUtil.mapFilterDataFromEntity(newFilterEntity);
        return newFilter;
    }

    @Override
    public void delete(String userId) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<FilterData> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FilterData update(FilterData data) {

        Filter entity = filterRepository.getFilterByUserId(data.getUserId());
        FilterData updatedFilter = null;
        if (entity != null) {
            Filter mappedEntity = filterServiceUtil.mapFilterEntityFromFilterData(data);
            entity.setUser(mappedEntity.getUser());
            entity.setOriginAirports(mappedEntity.getOriginAirports());
            entity.setDestinationAirports(mappedEntity.getDestinationAirports());
            entity.setEtaStart(mappedEntity.getEtaStart());
            entity.setEtaEnd(mappedEntity.getEtaEnd());
            entity.setFlightDirection(mappedEntity.getFlightDirection());

            Filter savedEntity = filterRepository.save(entity);
            updatedFilter = filterServiceUtil.mapFilterDataFromEntity(savedEntity);
        }
        return updatedFilter;

    }

    @Override
    public FilterData findById(String userId) {
        Filter entity = filterRepository.getFilterByUserId(userId);
        FilterData filter = null;
        if (entity != null)
            filter = filterServiceUtil.mapFilterDataFromEntity(entity);
        return filter;
    }

}
