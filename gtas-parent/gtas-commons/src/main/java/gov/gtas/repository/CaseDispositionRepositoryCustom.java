/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.repository;

import gov.gtas.model.Case;
import gov.gtas.services.dto.CaseRequestDto;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface CaseDispositionRepositoryCustom {

    public Pair<Long, List<Case>> findByCriteria(CaseRequestDto dto);
}
