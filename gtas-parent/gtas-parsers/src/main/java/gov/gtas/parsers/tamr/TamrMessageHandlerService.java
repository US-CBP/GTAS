package gov.gtas.parsers.tamr;

import org.springframework.stereotype.Service;

import gov.gtas.parsers.tamr.model.TamrMessage;

@Service
public interface TamrMessageHandlerService {
    /**
     * Handle responses to Tamr QUERY requests. This handles both the "derog
     * matches" and "traveler history" responses.
     */
    public void handleQueryResponse(TamrMessage response);

    /**
     * Handles an acknowledgement response from Tamr. This response is received
     * for requests that don't give any particular response data.
     */
    public void handleAcknowledgeResponse(TamrMessage response);

    /**
     * Handles TH.CLUSTERS and TH.DELTAS messages from Tamr. These both require
     * similar functionality: updating the Tamr cluster IDs.
     */
    public void handleTamrIdUpdate(TamrMessage response);
    
    /**
     * Handles ERROR messages from Tamr. These can happen during Tamr
     * processing or for any error unrelated to a request.
     */
    public void handleErrorResponse(TamrMessage response);
}
