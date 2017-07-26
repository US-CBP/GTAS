/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Case;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface CaseDispositionRepository extends CrudRepository<Case, Long> {


    public List<Case> findAll();
    public Case findById(Long id);

}