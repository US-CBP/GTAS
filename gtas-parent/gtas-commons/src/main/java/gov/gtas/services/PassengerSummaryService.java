package gov.gtas.services;

import gov.gtas.model.NoteType;
import gov.gtas.services.jms.AdditionalProcessingMessageSender;
import gov.gtas.summary.MessageSummaryList;
import gov.gtas.summary.PassengerNote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;


@Component
public class PassengerSummaryService {


    final
    AdditionalProcessingService additionalProcessingService;

    final
    AdditionalProcessingMessageSender additionalProcessingMessageSender;

    @Value("${additional.processing.queue}")
    private String addProcessQueue;

    public PassengerSummaryService(AdditionalProcessingService additionalProcessingService,
                                   AdditionalProcessingMessageSender additionalProcessingMessageSender) {
        this.additionalProcessingService = additionalProcessingService;
        this.additionalProcessingMessageSender = additionalProcessingMessageSender;
    }

    public void sendMessage(Long passengerId, String note, String noteRtf, NoteType noteType) {
        PassengerNote passengerNote = new PassengerNote();
        passengerNote.setNoteType(Collections.singletonList(noteType.getType()));
        passengerNote.setPlainTextNote(note);
        passengerNote.setRtfNote(noteRtf);
        MessageSummaryList msl = additionalProcessingService.listFromPassenger(noteRtf, note, noteType.getType(), passengerId, false);
        additionalProcessingMessageSender.sendProcessedMessage(addProcessQueue, msl, msl.getSummaryMetaData());
    }

}
