/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import gov.gtas.model.Filter;

public interface FilterRepository extends CrudRepository<Filter, String> {
    
     @Query("SELECT f FROM Filter f WHERE f.user.userId = :userId ")
    public Filter getFilterByUserId( @Param("userId") String userId);

}
