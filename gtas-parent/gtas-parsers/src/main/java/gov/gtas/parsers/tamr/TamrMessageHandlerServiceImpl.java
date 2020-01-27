package gov.gtas.parsers.tamr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        logger.info("TODO: handle QUERY response");
    }

    /**
     * Handles an acknowledgement response from Tamr. This response is received
     * for requests that don't give any particular response data.
     */
    @Override
    public void handleAcknowledgeResponse(TamrMessage response) {
        logger.info("TODO: handle acknowledgement");
    }

    /**
     * Handles TH.CLUSTERS and TH.DELTAS messages from Tamr. These both require
     * similar functionality: updating the Tamr cluster IDs.
     */
    @Override
    public void handleTamrIdUpdate(TamrMessage response) {
        logger.info("TODO: handle Tamr ID updates");
    }
    
    /**
     * Handles ERROR messages from Tamr. These can happen during Tamr
     * processing or for any error unrelated to a request.
     */
    @Override
    public void handleErrorResponse(TamrMessage response) {
        logger.info("TODO: handle Tamr error");
    }
}
