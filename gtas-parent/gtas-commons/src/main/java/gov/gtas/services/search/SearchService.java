/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import gov.gtas.services.dto.AdhocQueryDto;

public interface SearchService {
    public AdhocQueryDto findPassengers(String query, int pageNumber, int pageSize, String column, String dir);
}
