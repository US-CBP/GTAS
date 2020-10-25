package gov.gtas.controller;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.NoteType;
import gov.gtas.model.PassengerNote;
import gov.gtas.services.AdditionalProcessingService;
import gov.gtas.services.NoteTypeService;
import gov.gtas.services.PassengerNoteService;
import gov.gtas.services.dto.LookoutSendRequest;
import gov.gtas.summary.MessageSummary;
import gov.gtas.summary.MessageSummaryList;
import gov.gtas.summary.PassengerHit;
import gov.gtas.summary.SummaryMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class LookoutController {

    @Autowired
    AdditionalProcessingService additionalProcessingService;

    @Autowired
    PassengerNoteService passengerNoteService;

    @Autowired
    NoteTypeService noteTypeService;

    @RequestMapping(value = "/lookout/send", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    void updateMessages(@RequestBody LookoutSendRequest lookoutSendRequest, HttpServletRequest hsr) {
        PassengerNote note = new PassengerNote();
        note.setPassengerId(lookoutSendRequest.getPassengerId());
        note.setPlainTextComment(lookoutSendRequest.getNote());
        note.setRtfComment(lookoutSendRequest.getNoteRtf());
        NoteType noteType = noteTypeService.getLookoutNoteType();
        note.getNoteType().add(noteType);

        PassengerHit passengerHit = new PassengerHit();
        passengerHit.setCreatedDate(new Date());
        passengerHit.setDescription("An external Lookout Hit.");
        passengerHit.setHitCategory(HitTypeEnum.EXTERNAL_HIT.toString());
        passengerHit.setHitTypeEnum(HitTypeEnum.EXTERNAL_HIT.getDisplayName());
        passengerHit.setPercentage(1);
        passengerHit.setRuleConditions("Manually forwarded lookout");
        passengerHit.setTitle("Lookout!");

        MessageSummaryList msl =
                additionalProcessingService.listFromPassenger(lookoutSendRequest.getNoteRtf(), lookoutSendRequest.getNote(),
                        "LOOKOUT", lookoutSendRequest.getPassengerId(), lookoutSendRequest.getSendRaw());
        if (msl.getMessageSummaryList().size() == 1) {
            MessageSummary ms = msl.getMessageSummaryList().get(0);
            if (ms.getPassengerSummaries().size() == 1) {
                ms.getPassengerSummaries().get(0).getPassengerHits().add(passengerHit);
            } else {
                throw new RuntimeException("Only 1 passenger expected but none or > 1 found!");
            }
        } else {
            throw new RuntimeException("Only 1 message expected but none or > 1 found!");
        }
        passengerNoteService.saveNote(note);
        SummaryMetaData smd = new SummaryMetaData();
        smd.setSummary("LOOKOUT");
        smd.setCountryGroupName(lookoutSendRequest.getCountryGroupName());
        additionalProcessingService.sendMessage(msl, smd);
    }
}
