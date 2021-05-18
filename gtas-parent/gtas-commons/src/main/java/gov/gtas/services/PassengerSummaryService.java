package gov.gtas.services;

import gov.gtas.model.NoteType;
import gov.gtas.services.jms.AdditionalProcessingMessageSender;
import gov.gtas.summary.MessageSummaryList;
import gov.gtas.summary.PassengerNote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@ConditionalOnProperty(prefix = "additionalprocessing", name = "enabled")
public class PassengerSummaryService {


	@Autowired(required=false)
    private AdditionalProcessingService additionalProcessingService;

    
    @Autowired(required=false)
    private AdditionalProcessingMessageSender additionalProcessingMessageSender;

    @Value("${additional.processing.queue}")
    private String addProcessQueue;

    public void sendMessage(Long passengerId, String note, String noteRtf, NoteType noteType) {
        PassengerNote passengerNote = new PassengerNote();
        passengerNote.setNoteType(Collections.singletonList(noteType.getType()));
        passengerNote.setPlainTextNote(note);
        passengerNote.setRtfNote(noteRtf);
        MessageSummaryList msl = additionalProcessingService.listFromPassenger(noteRtf, note, noteType.getType(), passengerId, false);
        additionalProcessingMessageSender.sendProcessedMessage(addProcessQueue, msl, msl.getSummaryMetaData());
    }

}
