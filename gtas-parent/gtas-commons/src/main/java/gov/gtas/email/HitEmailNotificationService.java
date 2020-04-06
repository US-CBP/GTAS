package gov.gtas.email;

import freemarker.template.TemplateException;
import gov.gtas.email.dto.CategoryDTO;
import gov.gtas.email.dto.HitEmailDTO;
import gov.gtas.email.dto.HitEmailSenderDTO;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitViewStatus;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.User;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.UserRepository;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

@Service
public class HitEmailNotificationService {

    private static final String HIGH_PROFILE_NOTIFICATION_FTL = "highProfileHitNotification.ftl";
    private static final String AUTOMATED_EMAIL_SUBJECT = "GTAS Automated Email Notification";
    private static final String MANUAL_EMAIL_SUBJECT = "GTAS Passenger Notification: %s, %s";

    @Resource
    private EmailTemplateLoader emailTemplateLoader;

    @Resource
    private PassengerService passengerService;

    @Resource
    private UserRepository userRepository;

    @Value("${hit.priority.category}")
    private Long priorityHitCategory;

    @Value("${login.page.url}")
    private String urlToLoginPage;

    @Transactional
    public List<EmailDTO> generateAutomatedHighPriorityHitEmailDTOs(Set<Passenger> passengers) throws IOException, TemplateException {
        List<EmailDTO> emailDTOs = new ArrayList<>();
        passengers = passengerService.getPassengersForEmailMatching(passengers);

        Map<String, Set<HitEmailDTO>> emailRecipientToDTOs = generateEmailRecipientDTOMap(passengers);
        for(Map.Entry<String, Set<HitEmailDTO>> emailToDto: emailRecipientToDTOs.entrySet()) {
            EmailDTO emailDTO = new EmailDTO();

            emailDTO.setTo(new String[] { emailToDto.getKey() });
            emailDTO.setSubject(AUTOMATED_EMAIL_SUBJECT);

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
                                        .map(email -> new HashMap.SimpleEntry<>(email, generateHitEmailDTO(passenger, null, null))))
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toSet())));

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

    public EmailDTO generateManualNotificationEmailDTO(String[] to, String note, Long paxId, String userID) throws IOException, TemplateException {
        User sender = userRepository.userAndGroups(userID).orElseThrow(RuntimeException::new);
        Passenger passenger = passengerService.findByIdWithFlightPaxAndDocumentsAndHitDetails(paxId);
        EmailDTO emailDTO = new EmailDTO();

        HitEmailDTO hitEmailDTO = generateHitEmailDTO(passenger, note, sender);
        emailDTO.setTo(to);

        PassengerDetails passengerDetails = passenger.getPassengerDetails();
        emailDTO.setSubject(format(MANUAL_EMAIL_SUBJECT, passengerDetails.getLastName().toUpperCase(), passengerDetails.getFirstName().toUpperCase()));
        String htmlContent = emailTemplateLoader.generateHtmlString(HIGH_PROFILE_NOTIFICATION_FTL, Collections.singleton(hitEmailDTO));
        emailDTO.setBody(htmlContent);

        return emailDTO;
    }

    public HitEmailDTO generateHitEmailDTO(Passenger passenger, String note, User sender) {
        HitEmailDTO hitEmailDto = new HitEmailDTO();
        hitEmailDto.setPassengerUUID(passenger.getUuid());

        PassengerDetails passengerDetails = passenger.getPassengerDetails();
        hitEmailDto.setFirstName(passengerDetails.getFirstName());
        hitEmailDto.setLastName(passengerDetails.getLastName());
        hitEmailDto.setGender(passengerDetails.getGender());
        hitEmailDto.setDob(passengerDetails.getDob());
        hitEmailDto.setNote(note);

        if(!Objects.isNull(sender)) {
            HitEmailSenderDTO hitEmailSender = new HitEmailSenderDTO();
            hitEmailSender.setFirstName(sender.getFirstName());
            hitEmailSender.setLastName(sender.getLastName());
            hitEmailSender.setUserId(sender.getUserId());

            hitEmailDto.setHitEmailSenderDTO(hitEmailSender);
            hitEmailDto.seturlToLoginPage(urlToLoginPage);
        }


        Set<HitDetail> hitDetails = passenger.getHitDetails();
        for(HitDetail hitDetail: hitDetails) {
            HitCategory category = hitDetail.getHitMaker().getHitCategory();

            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryName(category.getName());
            categoryDTO.setSeverity(category.getSeverity().name());
            categoryDTO.setRule(hitDetail.getTitle());

            Optional<HitViewStatus> hitViewStatus = hitDetail.getHitViewStatus().stream().findFirst();
            hitViewStatus.ifPresent(viewStatus -> categoryDTO.setStatus(viewStatus.getHitViewStatusEnum().name()));
            categoryDTO.setType(hitDetail.getHitType());

            hitEmailDto.addCategory(categoryDTO);
        }

        Set<Document> documents = passenger.getDocuments();
        for(Document document: documents) {
            hitEmailDto.addDocument(document.getDocumentNumber(), document.getDocumentType());
        }

        Flight flight = passenger.getFlight();
        hitEmailDto.setFlightNumber(flight.getFlightNumber());
        hitEmailDto.setFlightOrigin(flight.getOrigin());
        hitEmailDto.setFlightDestination(flight.getDestination());
        hitEmailDto.setCarrier(flight.getCarrier());

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
               .filter(User::isActive)
               .filter(HitEmailNotificationService::isRegisteredForHighPriorityHitNotifications)
               .map(User::getEmail)
               .collect(Collectors.toList());
    }

    private static boolean isRegisteredForHighPriorityHitNotifications(User u) {
        return u.getHighPriorityHitsEmailNotification() != null && u.getHighPriorityHitsEmailNotification() && StringUtils.isNotBlank(u.getEmail());
    }
}
