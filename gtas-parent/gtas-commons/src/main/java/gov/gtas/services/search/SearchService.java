/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import gov.gtas.model.Passenger;
import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.services.dto.LinkAnalysisDto;

public interface SearchService {
	public AdhocQueryDto findPassengers(String query, int pageNumber, int pageSize, String column, String dir);

	public LinkAnalysisDto findPaxLinks(Passenger pax, int pageNumber, int pageSize, String column, String dir);
}
