package gov.gtas.services.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.services.PendingHitDetailsService;
import gov.gtas.services.SummaryFactory;
import gov.gtas.summary.PassengerPendingDetail;
import gov.gtas.summary.PassengerPendingHitDetailList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class AdditionalProcessingMessageReciever {

    private final PendingHitDetailsService pendingHitDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AdditionalProcessingMessageReciever.class);

    public AdditionalProcessingMessageReciever(PendingHitDetailsService pendingHitDetailsService) {
        this.pendingHitDetailsService = pendingHitDetailsService;
    }


    @JmsListener(destination = "${additional.processing.pending.hits.intake}", concurrency = "10")
    public void processPendingHitDetails(Message<?> pendingHitDetailListMessage) {
        try {
            ObjectMapper om = new ObjectMapper();
            String json = (String)pendingHitDetailListMessage.getPayload();
            PassengerPendingHitDetailList pphd = om.readValue(json, PassengerPendingHitDetailList.class);
            Set<PendingHitDetails> phdList = new HashSet<>();
            for (PassengerPendingDetail ppd : pphd.getPassengerPendingDetailList()) {
                phdList.add(SummaryFactory.from(ppd));
            }
            pendingHitDetailsService.saveAllPendingHitDetails(phdList);
        } catch (IOException e) {
            logger.error("ERROR PROCESSING PENDING HITS!!!" ,e);
        }
    }
}
