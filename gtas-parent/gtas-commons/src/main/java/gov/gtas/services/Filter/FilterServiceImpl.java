/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.Filter;

import gov.gtas.model.Filter;
import gov.gtas.repository.FilterRepository;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class FilterServiceImpl.
 */
@Service
public class FilterServiceImpl implements FilterService {

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	FilterServiceUtil filterServiceUtil;
	@Resource
	FilterRepository filterRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.gtas.services.Filter.FilterService#create(gov.gtas.services.Filter
	 * .FilterData)
	 */
	@Override
	@Transactional
	public FilterData create(FilterData filterData) {
		Filter filterEntity = filterServiceUtil
				.mapFilterEntityFromFilterData(filterData);

		Filter newFilterEntity = filterRepository.save(filterEntity);
		FilterData newFilter = filterServiceUtil
				.mapFilterDataFromEntity(newFilterEntity);
		return newFilter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.gtas.services.Filter.FilterService#delete(java.lang.String)
	 */
	@Override
	@Transactional
	public void delete(String userId) {
		Filter entity = filterRepository.getFilterByUserId(userId);
		filterRepository.delete(entity);
	}

	/* (non-Javadoc)
	 * @see gov.gtas.services.Filter.FilterService#findAll()
	 */
	@Override
	public List<FilterData> findAll() {
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.gtas.services.Filter.FilterService#update(gov.gtas.services.Filter.FilterData)
	 */
	@Override
	@Transactional
	public FilterData update(FilterData data) {

		Filter entity = filterRepository.getFilterByUserId(data.getUserId());
		FilterData updatedFilter = null;
		if (entity != null) {
			Filter mappedEntity = filterServiceUtil
					.mapFilterEntityFromFilterData(data);
			entity.setUser(mappedEntity.getUser());
			entity.setOriginAirports(mappedEntity.getOriginAirports());
			entity.setDestinationAirports(mappedEntity.getDestinationAirports());
			entity.setEtaStart(mappedEntity.getEtaStart());
			entity.setEtaEnd(mappedEntity.getEtaEnd());
			entity.setFlightDirection(mappedEntity.getFlightDirection());

			Filter savedEntity = filterRepository.save(entity);
			updatedFilter = filterServiceUtil
					.mapFilterDataFromEntity(savedEntity);
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
