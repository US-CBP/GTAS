package gov.gtas.email;

import freemarker.template.TemplateException;
import gov.gtas.email.dto.HitEmailDTO;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.User;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.services.CountDownCalculator;
import gov.gtas.services.PassengerService;
import gov.gtas.services.dto.EmailDTO;
import gov.gtas.vo.passenger.CountDownVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.*;
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

@Service
public class HighPriorityHitEmailNotificationService {

    private static final String HIGH_PROFILE_NOTIFICATION_FTL = "highProfileHitNotification.ftl";
    private static final String SUBJECT = "(GTAS): Hit Status Notification";

    @Resource
    private EmailTemplateLoader emailTemplateLoader;

    @Resource
    private PassengerService passengerService;

    @Value("${hit.priority.category}")
    private Long priorityHitCategory;

    @Transactional
    public List<EmailDTO> generateAutomatedHitEmailDTOs(Set<Passenger> passengers) throws IOException, TemplateException {
        List<EmailDTO> emailDTOs = new ArrayList<>();
        passengers = passengerService.getPassengersForEmailMatching(passengers);

        Map<String, List<HitEmailDTO>> emailRecipientToDTOs = generateEmailRecipientDTOMap(passengers);
        for(Map.Entry<String, List<HitEmailDTO>> emailToDto: emailRecipientToDTOs.entrySet()) {
            EmailDTO emailDTO = new EmailDTO();

            emailDTO.setTo(new String[] { emailToDto.getKey() });
            emailDTO.setSubject(SUBJECT);

            String htmlContent = emailTemplateLoader.generateHtmlString(HIGH_PROFILE_NOTIFICATION_FTL, emailToDto.getValue());
            emailDTO.setBody(htmlContent);

            emailDTOs.add(emailDTO);
        }

        return emailDTOs;
    }

    public Map<String, List<HitEmailDTO>> generateEmailRecipientDTOMap(Set<Passenger> passengers) {
        Map<String, List<HitEmailDTO>> emailRecipientToDTOs =
                passengers.stream()
                        .filter(this::hasHighPriorityHitCategory)
                        .flatMap(passenger ->
                                generateRecipientEmailList(passenger).stream()
                                        .map(email -> new HashMap.SimpleEntry<>(email, generateHitEmailDTO(passenger))))
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));

        return emailRecipientToDTOs;

    }

	private boolean hasHighPriorityHitCategory(Passenger passenger) {
		boolean hasHighPriorityHitCategory = false;
		Date dob = passenger.getPassengerDetails().getDob();
		if (dob != null) {
			LocalDateTime localDateTimeDOB = Instant.ofEpochMilli(dob.getTime()).atZone(ZoneOffset.UTC)
					.toLocalDateTime();
			if (!(localDateTimeDOB.getDayOfMonth() == 1 && localDateTimeDOB.getMonth() == Month.JANUARY)) {
				hasHighPriorityHitCategory = passenger.getHitDetails().stream()
						.filter(hd -> HitTypeEnum.PARTIAL_WATCHLIST != hd.getHitEnum())
						.anyMatch(hitDetail -> priorityHitCategory
								.equals(hitDetail.getHitMaker().getHitCategory().getId()));
			}
		}
		return hasHighPriorityHitCategory;
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
        CountDownCalculator countDownCalculator = new CountDownCalculator(new Date());
        CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(date);
        return countDownVo.getCountDownTimer();
    }

    private static List<String> generateRecipientEmailList(Passenger passenger) {
       return passenger.getHitDetails().stream()
               .flatMap(details -> details.getHitMaker().getHitCategory().getUserGroups().stream())
               .flatMap(userGroup -> userGroup.getGroupMembers().stream())
               .filter(HighPriorityHitEmailNotificationService::isRegisteredForHighPriorityHitNotifications)
               .map(User::getEmail)
               .collect(Collectors.toList());
    }

    private static boolean isRegisteredForHighPriorityHitNotifications(User u) {
        return u.getEmailEnabled() != null && u.getHighPriorityHitsEmailNotification() != null && u.getHighPriorityHitsEmailNotification() && u.getEmailEnabled() && StringUtils.isNotBlank(u.getEmail());
    }
}
