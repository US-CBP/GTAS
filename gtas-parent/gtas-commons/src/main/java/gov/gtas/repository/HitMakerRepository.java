/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.repository;

import gov.gtas.model.ExternalHit;
import gov.gtas.model.HitMaker;
import gov.gtas.model.ManualHit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HitMakerRepository extends CrudRepository<HitMaker, Long> {

    @Query("SELECT mhs FROM ManualHit mhs WHERE mhs.hitCategory.id = :hitCategoryId")
    List<ManualHit> findOneByHitCategoryIdAndHitType(@Param("hitCategoryId") Long hitCategoryId);

    @Query("Select hm from ExternalHit hm where hm.hitCategory.name = :name")
    List<ExternalHit> getExternalHitsCategoryByName(@Param("name") String name);

}
