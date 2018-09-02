/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Case;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.services.dto.SortOptionsDto;
import java.time.Instant;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Query;

@Repository
public class CaseDispositionRepositoryImpl implements CaseDispositionRepositoryCustom {

    private static final Logger logger = LoggerFactory
            .getLogger(CaseDispositionRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Pair<Long, List<Case>> findByCriteria(CaseRequestDto dto) 
    {
        StringBuilder querySB = new StringBuilder();
        StringBuilder countQuerySB = new StringBuilder();
        countQuerySB.append("SELECT COUNT(c) FROM Case c ");
        
        querySB.append("SELECT c, CASE WHEN c.flight.direction= 'I' THEN c.flightETADate WHEN c.flight.direction= 'O' THEN c.flightETDDate ELSE c.flightETDDate END AS countdown ");
        querySB.append(" FROM Case c ");
        
        List<String> criteria = addCriteria(dto);

        if (!criteria.isEmpty())
        {
           querySB.append(" WHERE "); 
           countQuerySB.append(" WHERE "); 
        }
        
        for (int j = 0; j < criteria.size(); j++)
        {
            if (j > 0)
            {
                querySB.append( " AND ");
                countQuerySB.append( " AND ");
            }
            
            querySB.append(criteria.get(j));
            countQuerySB.append(criteria.get(j));
        }
        
        // sorting
        querySB =  handleSortOptions(querySB, dto);
        
        String finalQuery = querySB.toString().endsWith(",") ? querySB.toString().substring(0, querySB.toString().length() - 1) : querySB.toString();
        
        Query countQuery = em.createQuery(countQuerySB.toString());
        Query query = em.createQuery(finalQuery);
        
        setQueryParameters(query, countQuery,  dto);

        Long caseCount = (Long)countQuery.getSingleResult();
        
         // pagination
        int pageNumber = dto.getPageNumber();
        int pageSize = dto.getPageSize();
        int firstResultIndex = (pageNumber - 1) * pageSize;
        query.setFirstResult(firstResultIndex);
        query.setMaxResults(dto.getPageSize());      
        
       logger.debug(query.unwrap(org.hibernate.Query.class)
                .getQueryString());
       
        List<Object[]> resultList = query.getResultList();
         
        List<Case> results = putCountdownTimeIntoCaseObjects(resultList);

        return new ImmutablePair<>(caseCount, results);       
    }
    
    private List<String> addCriteria(CaseRequestDto dto)
    {
        List<String> criteria = new ArrayList<>();
       
        if (dto.getLastName() != null && !dto.getLastName().isEmpty()) 
        {
          criteria.add(" c.lastName LIKE :lastName ");
        }

        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) 
        {
           criteria.add(" c.status = :status ");
        }

        if (dto.getFlightNumber() != null && !dto.getFlightNumber().isEmpty()) 
        {
           criteria.add(" c.flightNumber LIKE :flightNumber ");
        }

        if (dto.getRuleCatId() != null) 
        {
           criteria.add(" c.highPriorityRuleCatId = :highPriorityRuleCatId ");
        }
        
        if (dto.getEtaStart() != null && dto.getEtaEnd() != null) 
        {
           criteria.add(" (c.flightETADate BETWEEN :etaStart AND :etaEnd OR  c.flightETDDate BETWEEN :etaStart AND :etaEnd) "); 
        }      
       
       return criteria;
    }
    
    
    private void setQueryParameters(Query query, Query countQuery, CaseRequestDto dto)
    {
         if (dto.getLastName() != null && !dto.getLastName().isEmpty()) 
        { 
            query.setParameter("lastName", dto.getLastName().toUpperCase() + "%"); 
            countQuery.setParameter("lastName", dto.getLastName().toUpperCase() + "%"); 
        }
        
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) 
        { 
            query.setParameter("status", dto.getStatus().toUpperCase()); 
            countQuery.setParameter("status", dto.getStatus().toUpperCase()); 
        }
        
        if (dto.getFlightNumber() != null && !dto.getFlightNumber().isEmpty()) 
        { 
            query.setParameter("flightNumber", dto.getFlightNumber() + "%");
            countQuery.setParameter("flightNumber", dto.getFlightNumber() + "%");
        }
        
        if (dto.getRuleCatId() != null) 
        { 
            query.setParameter("highPriorityRuleCatId", dto.getRuleCatId()); 
            countQuery.setParameter("highPriorityRuleCatId", dto.getRuleCatId());
        }
        
        if (dto.getEtaStart() != null && dto.getEtaEnd() != null) 
        {
           Calendar calStart = new GregorianCalendar();
           Calendar calEnd = new GregorianCalendar();
           calStart.setTime(dto.getEtaStart());
           calEnd.setTime(dto.getEtaEnd());

           calStart.set(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
           Date startDate = calStart.getTime();
           calEnd.set(calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
           Date endDate = calEnd.getTime();
           query.setParameter("etaStart", startDate);
           query.setParameter("etaEnd", endDate);
           countQuery.setParameter("etaStart", startDate);
           countQuery.setParameter("etaEnd", endDate);
        }       
    }

    private StringBuilder handleSortOptions(StringBuilder querySB, CaseRequestDto dto)
    {
        if (dto.getSort() != null && !dto.getSort().isEmpty()) 
        {
            querySB.append(" ORDER BY "); 

            for (SortOptionsDto sort : dto.getSort()) 
            {
                querySB.append(" "); 
                querySB.append(sort.getColumn());
                querySB.append(" ");

                if ("desc".equalsIgnoreCase(sort.getDir())) 
                {
                  querySB.append(" DESC,");
                } 
                else 
                {
                  querySB.append(" ASC,");
                }
            } 
        }
        return querySB;
    }
    
    private List<Case> putCountdownTimeIntoCaseObjects(List<Object[]> resultList)
    {
       List<Case> returnList = new ArrayList<>();
       
       for (Object[] objArray : resultList)
       {
          Date countdownTime = (Date)objArray[1];
          Case caseObj = (Case)objArray[0];
          caseObj.setCountdown(countdownTime);
          returnList.add(caseObj);
       }
       
       return returnList; 
    }
}
