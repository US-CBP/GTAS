/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Case;

import java.util.List;

public interface CaseDispositionService {

    public Case create(Long flight_id, Long pax_id, List<Long> hit_ids);

    public Case addCaseComments(Long flight_id, Long pax_id, Long hit_id);
}