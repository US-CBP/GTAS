/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.util.List;

import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.vo.passenger.PassengerVo;

public interface SearchService {
    public AdhocQueryDto findPassengers(String query, int pageNumber, int pageSize);
}
