/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import static gov.gtas.constant.DomainModelConstants.UDR_UNIQUE_CONSTRAINT_NAME;
import static gov.gtas.constant.JsonResponseConstants.ATTR_ERROR_ID;
import gov.gtas.constants.ErrorConstants;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.json.JsonServiceResponse.ServiceResponseDetailAttribute;
import gov.gtas.services.ErrorPersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaSystemException;

public class GlobalErrorHandlerHelper {
	/*
	 * The logger for the Webapp Global Error Handler
	 */
	private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandlerHelper.class);

	public static JsonServiceResponse createDbErrorResponse(ErrorPersistenceService errorService,
			JpaSystemException ex) {
		if (ErrorUtils.isExceptionOfType(ex, "SQLGrammarException")) {
			logger.error("GTAS Webapp:SQLGrammarException - " + ex.getMessage());
			return new JsonServiceResponse(ErrorConstants.INVALID_SQL_ERROR_CODE,
					"There was a data base Error:" + ex.getMessage(), null);

		} else if (ErrorUtils.isConstraintViolationException(ex, UDR_UNIQUE_CONSTRAINT_NAME)) {
			logger.error("GTAS Webapp:ConstraintViolationException - " + ex.getMessage());
			return new JsonServiceResponse(ErrorConstants.DUPLICATE_UDR_ERROR_CODE,
					"This author has already created a UDR with this title:" + ex.getMessage(), null);
		}

		ErrorDetailInfo err = ErrorUtils.createErrorDetails(ex);
		try {
			err = errorService.create(err);
		} catch (Exception exception) {
			// possibly DB is down
			logger.error("error saving errorservice. Is DB Down?", exception);
		}

		JsonServiceResponse resp = new JsonServiceResponse(ErrorConstants.FATAL_DB_ERROR_CODE,
				"There was a backend DB error:" + ex.getMessage(), null);
		resp.getResponseDetails().add(0, new ServiceResponseDetailAttribute(ATTR_ERROR_ID, err.getErrorId()));
		return resp;
	}
}
