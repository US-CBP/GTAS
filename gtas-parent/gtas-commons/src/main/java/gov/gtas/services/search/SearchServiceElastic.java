/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.services.dto.AdhocQueryDto;

@Service
public class SearchServiceElastic implements SearchService {
	private static final Logger logger = LoggerFactory.getLogger(SearchServiceElastic.class);
	private static final AdhocQueryDto errorDto = new AdhocQueryDto("Search service is not available");
	
	@Autowired
	private ElasticHelper elastic;
	
	@Override
	public AdhocQueryDto findPassengers(String query, int pageNumber, int pageSize, String column, String dir) {
		AdhocQueryDto rv = null;
		
		elastic.initClient();
		if (elastic.isDown()) { 
			return errorDto;
		}
		
		try {
			rv = elastic.searchPassengers(query, pageNumber, pageSize, column, dir);
		} catch (Exception e) {
			logger.error("Elastic error: ", e);
			return errorDto;
		}

		return rv;
	}
}
