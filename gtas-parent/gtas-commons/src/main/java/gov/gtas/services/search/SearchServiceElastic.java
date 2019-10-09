/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.model.Passenger;
import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.services.dto.LinkAnalysisDto;
import gov.gtas.vo.passenger.PassengerVo;

@Service
public class SearchServiceElastic implements SearchService {
	private static final Logger logger = LoggerFactory.getLogger(SearchServiceElastic.class);
	private static final AdhocQueryDto errorDto = new AdhocQueryDto("Search service is not available");
	private static final LinkAnalysisDto linkErrorDto = new LinkAnalysisDto("Search service is not available");

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

	@Override
	public LinkAnalysisDto findPaxLinks(Passenger pax, int pageNumber, int pageSize, String column, String dir) {
		LinkAnalysisDto la = null;

		elastic.initClient();
		if (elastic.isDown()) {
			return linkErrorDto;
		}

		try {
			la = elastic.findPaxLinks(pax, pageNumber, pageSize, column, dir);
		} catch (Exception e) {
			logger.error("Elastic error: ", e);
			return linkErrorDto;
		}

		return la;
	}
}
