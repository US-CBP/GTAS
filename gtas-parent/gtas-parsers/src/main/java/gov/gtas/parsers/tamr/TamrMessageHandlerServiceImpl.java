package gov.gtas.parsers.tamr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.parsers.tamr.model.TamrMessage;
import gov.gtas.repository.PassengerIDTagRepository;

@Component
public class TamrMessageHandlerServiceImpl implements TamrMessageHandlerService {

    private final Logger logger = LoggerFactory.getLogger(TamrMessageHandlerServiceImpl.class);

    @Autowired
    private PassengerIDTagRepository passengerIDTagRepository;
    
    /**
     * Handle responses to Tamr QUERY requests. This handles both the "derog
     * matches" and "traveler history" responses.
     */
    @Override
    public void handleQueryResponse(TamrMessage response) {
        if (this.checkRecordErrors(response)) return;

        logger.info("TODO: handle QUERY response");
    }

    /**
     * Handles an acknowledgement response from Tamr. This response is received
     * for requests that don't give any particular response data.
     */
    @Override
    public void handleAcknowledgeResponse(TamrMessage response) {
        if (this.checkRecordErrors(response)) {
            // Record errors are already logged.
        } else if (response.getAcknowledgment() == true) {
            logger.info("{} request to Tamr acknowledged",
                    response.getMessageType());
        } else if (response.getAcknowledgment() == false) {
            logger.error("Error with {} request to Tamr: {}",
                    response.getMessageType(), response.getError());
        } else {
            logger.warn("{} message received from Tamr with no " +
                    "\"acknowledgment\" key. Ignoring...");
        }
    }

    /**
     * Handles TH.CLUSTERS and TH.DELTAS messages from Tamr. These both require
     * similar functionality: updating the Tamr cluster IDs.
     */
    @Override
    public void handleTamrIdUpdate(TamrMessage message) {
        // The "error" field can be set on these.
        if (message.getError() != null) {
            logger.error("Tamr error during {} request: {}",
                    message.getMessageType(), message.getError());
        } else {
            logger.info("TODO: handle Tamr ID updates");
        }
    }
    
    /**
     * Handles ERROR messages from Tamr. These can happen during Tamr
     * processing or for any error unrelated to a request.
     */
    @Override
    public void handleErrorResponse(TamrMessage response) {
        logger.error("{} message received from Tamr: {}",
                response.getMessageType(), response.getError());
    }
    
    /**
     * Checks to see if recordErrors are present on the given response. If so,
     * returns true and logs a warning message.
     */
    private boolean checkRecordErrors(TamrMessage response) {
        boolean hasRecordErrors = response.getRecordErrors() != null &&
                response.getRecordErrors().size() > 0;
        if (hasRecordErrors) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String recordErrorsJson = mapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(response.getRecordErrors());
                logger.warn("Received recordErrors from Tamr for {} " +
                        "request. recordErrors = {}",
                        response.getMessageType(), recordErrorsJson);
            } catch (JsonProcessingException e) {
                logger.warn("Received recordErrors from Tamr for {} " +
                        "request. Unable to display errors.",
                        response.getMessageType());
            }
        }
        return hasRecordErrors;
    }
}
