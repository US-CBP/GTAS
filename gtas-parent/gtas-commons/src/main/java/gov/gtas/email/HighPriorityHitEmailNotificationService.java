package gov.gtas.email;

import freemarker.template.TemplateException;
import gov.gtas.email.dto.HitEmailDTO;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.User;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.services.dto.EmailDTO;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class HighPriorityHitEmailNotificationService {

    private static final String HIGH_PROFILE_NOTIFICATION_FTL = "highProfileHitNotification.ftl";
    private static final String SUBJECT = "(GTAS): Hit Status Notification";

    @Resource
    private EmailTemplateLoader emailTemplateLoader;

    @Value("${hit.priority.category}")
    private Long priorityHitCategory;

    public List<EmailDTO> generateEmailDTOs(Set<Passenger> passengers) throws IOException, TemplateException {
        List<EmailDTO> emailDTOs = new ArrayList<>();
        Map<String, Set<HitEmailDTO>> emailRecipientToDTOs = generateEmailRecipientDTOMap(passengers);
        for(Map.Entry<String, Set<HitEmailDTO>> emailToDto: emailRecipientToDTOs.entrySet()) {
            EmailDTO emailDTO = new EmailDTO();

            emailDTO.setTo(new String[] { emailToDto.getKey() });
            emailDTO.setSubject(SUBJECT);

            String htmlContent = emailTemplateLoader.generateHtmlString(HIGH_PROFILE_NOTIFICATION_FTL, emailToDto.getValue());
            emailDTO.setBody(htmlContent);

            emailDTOs.add(emailDTO);
        }

        return emailDTOs;
    }

    public Map<String, Set<HitEmailDTO>> generateEmailRecipientDTOMap(Set<Passenger> passengers) {
        Map<String, Set<HitEmailDTO>> emailRecipientToDTOs =
                passengers.stream()
                        .filter(this::hasHighPriorityHitCategory)
                        .flatMap(passenger ->
                                generateRecipientEmailList(passenger).stream()
                                        .map(email -> new HashMap.SimpleEntry<>(email, generateHitEmailDTO(passenger))))
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toSet())));

        return emailRecipientToDTOs;

    }

    private boolean hasHighPriorityHitCategory(Passenger passenger) {
       return passenger.getHitDetails()
               .stream()
               .anyMatch(hitDetail ->
                       priorityHitCategory.equals(hitDetail.getHitMaker().getHitCategory().getId()));
    }

    public HitEmailDTO generateHitEmailDTO(Passenger passenger) {
        HitEmailDTO hitEmailDto = new HitEmailDTO();

        PassengerDetails passengerDetails = passenger.getPassengerDetails();
        hitEmailDto.setFirstName(passengerDetails.getFirstName());
        hitEmailDto.setLastName(passengerDetails.getLastName());
        hitEmailDto.setGender(passengerDetails.getGender());
        hitEmailDto.setDob(passengerDetails.getDob());

        Set<HitDetail> hitDetails = passenger.getHitDetails();
        for(HitDetail hitDetail: hitDetails) {
            HitCategory category = hitDetail.getHitMaker().getHitCategory();
            String rule = hitDetail.getTitle() + " (" + hitDetail.getHitType() + ")";
            hitEmailDto.addCategory(category.getSeverity().name(), category.getName(), rule);
        }

        Set<Document> documents = passenger.getDocuments();
        for(Document document: documents) {
            hitEmailDto.addDocument(document.getDocumentNumber(), document.getDocumentType());
        }

        Flight flight = passenger.getFlight();
        hitEmailDto.setFlightNumber(flight.getFlightNumber());

        Date flightDate = flight.getFlightCountDownView().getCountDownTimer();
        hitEmailDto.setTimeRemaining(getTimeRemaining(flightDate));

        return hitEmailDto;
    }

    private String getTimeRemaining(Date date) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        Duration duration = Duration.between(LocalDateTime.now(), localDateTime);
        return DurationFormatUtils.formatDuration(duration.toMillis(), "dd:HH:mm", true);
    }

    private static List<String> generateRecipientEmailList(Passenger passenger) {
       return passenger.getHitDetails().stream()
               .flatMap(details -> details.getHitMaker().getHitCategory().getUserGroups().stream())
               .flatMap(userGroup -> userGroup.getGroupMembers().stream())
               .map(User::getEmail)
               .collect(Collectors.toList());
    }
}
