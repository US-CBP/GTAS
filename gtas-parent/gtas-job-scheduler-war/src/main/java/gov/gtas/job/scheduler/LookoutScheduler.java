package gov.gtas.job.scheduler;

import gov.gtas.additional.jms.AdditionalProcessingMessageSender;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.LookoutRequest;
import gov.gtas.model.NoteType;
import gov.gtas.model.PassengerNote;
import gov.gtas.repository.LookoutRequestRepository;
import gov.gtas.services.AdditionalProcessingService;
import gov.gtas.services.NoteTypeService;
import gov.gtas.services.PassengerNoteService;
import gov.gtas.summary.MessageSummary;
import gov.gtas.summary.MessageSummaryList;
import gov.gtas.summary.PassengerHit;
import gov.gtas.summary.SummaryMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "lookout", name = "enabled")
public class LookoutScheduler {


    Logger logger = LoggerFactory.getLogger(LookoutScheduler.class);

    @Autowired
    AdditionalProcessingService additionalProcessingService;

    @Autowired
    AdditionalProcessingMessageSender additionalProcessingMessageSender;

    @Autowired
    NoteTypeService noteTypeService;

    @Autowired
    PassengerNoteService passengerNoteService;

    @Autowired
    LookoutRequestRepository lookoutRequestRepository;

    /**
     * rule engine
     **/
    @Scheduled(fixedDelayString = "${rule-runner.fixed-delay.milliseconds}", initialDelayString = "${rule-runner.initial-delay.milliseconds}")
    public void ruleEngine() {
        List<LookoutRequest> lookoutRequestList = lookoutRequestRepository.findTop500ByOrderByIdAsc();
        for (LookoutRequest lookoutRequest : lookoutRequestList) {
            try {
                gov.gtas.summary.PassengerNote note = new gov.gtas.summary.PassengerNote();
                note.setPlainTextNote(lookoutRequest.getNote());
                note.setRtfNote(lookoutRequest.getNoteRtf());
                note.getNoteType().add("LOOKOUT");

                PassengerHit passengerHit = new PassengerHit();
                passengerHit.setCreatedDate(new Date());
                passengerHit.setDescription("An external Lookout Hit.");
                passengerHit.setHitCategory(HitTypeEnum.EXTERNAL_HIT.toString());
                passengerHit.setHitTypeEnum(HitTypeEnum.EXTERNAL_HIT.getDisplayName());
                passengerHit.setPercentage(1);
                passengerHit.setRuleConditions("Manually forwarded lookout");
                passengerHit.setTitle("Lookout!");

                MessageSummaryList msl =
                        additionalProcessingService.listFromPassenger(lookoutRequest.getNoteRtf(), lookoutRequest.getNote(),
                                "LOOKOUT", lookoutRequest.getPassengerId(), lookoutRequest.getSendRaw());
                if (msl.getMessageSummaryList().size() == 1) {
                    MessageSummary ms = msl.getMessageSummaryList().get(0);
                    if (ms.getPassengerSummaries().size() == 1) {
                        List<PassengerHit> phitList = new ArrayList<>();
                        phitList.add(passengerHit);
                        ms.getPassengerSummaries().get(0).setPassengerHits(phitList);
                        ms.getPassengerSummaries().get(0).getPassengerNotes().add(note);
                    } else {
                        throw new RuntimeException("Only 1 passenger expected but none or > 1 found!");
                    }
                } else {
                    throw new RuntimeException("Only 1 message expected but none or > 1 found!");
                }
                PassengerNote systemNote = new PassengerNote();
                systemNote.setPassengerId(lookoutRequest.getPassengerId());
                systemNote.setPlainTextComment(lookoutRequest.getNote());
                systemNote.setRtfComment(lookoutRequest.getNoteRtf());
                NoteType noteType = noteTypeService.getLookoutNoteType();
                systemNote.getNoteType().add(noteType);
                passengerNoteService.saveNote(systemNote);
                SummaryMetaData smd = new SummaryMetaData();
                smd.setSummary("LOOKOUT");
                smd.setCountryGroupName(lookoutRequest.getCountryGroupName());
                additionalProcessingService.sendMessage(msl, smd);
                logger.info("Send manual lookout for passenger " + lookoutRequest.getId() + " and country id :" +lookoutRequest.getCountryGroupName());
            } catch (Exception e) {
                logger.error("ERROR UNABLE TO SEND LOOKOUT FOR  " + lookoutRequest.getId() + " and country id : " +lookoutRequest.getCountryGroupName(), e);

            }
            lookoutRequestRepository.delete(lookoutRequest);
        }
    }

}

