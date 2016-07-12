/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.HitDetail;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface HitDetailRepository extends CrudRepository<HitDetail, Long> {

    @Query("DELETE FROM HitDetail hd WHERE hd.parent.id = (:id)")
    @Modifying
    @Transactional
    public void deleteDBData(@Param("id") Long id);

}
