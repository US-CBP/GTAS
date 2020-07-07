package gov.gtas.services;

import gov.gtas.summary.MessageSummary;
import org.springframework.transaction.annotation.Transactional;

public interface GenericLoading {

    @Transactional
    MessageInformation load(MessageSummary messageSummary, String filePath) ;
}
