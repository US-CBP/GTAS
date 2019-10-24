package gov.gtas.email;

import freemarker.template.TemplateException;
import gov.gtas.email.dto.HitEmailDto;
import gov.gtas.model.Notification;
import gov.gtas.model.Passenger;
import gov.gtas.model.User;
import gov.gtas.model.UserGroupNotification;
import gov.gtas.model.UserNotification;
import gov.gtas.services.dto.EmailDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Service
public class HighPriorityHitEmailNotificationService {

    private static final String HIGH_PROFILE_NOTIFICATION_FTL = "highProfileHitNotification.ftl";
    private static final String SUBJECT = "Generated Hits";

    @Resource
    private EmailTemplateLoader emailTemplateLoader;

    public List<EmailDTO> generateEmailDTOs(Set<Passenger> passengers) throws IOException, TemplateException {
        List<EmailDTO> emailDTOs = new ArrayList<>();
        Map<String, List<HitEmailDto>> emailRecipientToDtos = generateEmailRecipientDtoMap(passengers);
        for(Map.Entry<String, List<HitEmailDto>> emailToDto: emailRecipientToDtos.entrySet()) {
            EmailDTO emailDTO = new EmailDTO();

            emailDTO.setTo(new String[]{ emailToDto.getKey() });
            emailDTO.setSubject(SUBJECT);

            String htmlContent = emailTemplateLoader.generateHtmlString(HIGH_PROFILE_NOTIFICATION_FTL, emailToDto.getValue());
            emailDTO.setBody(htmlContent);

            emailDTOs.add(emailDTO);
        }

        return emailDTOs;
    }

    public Map<String, List<HitEmailDto>> generateEmailRecipientDtoMap(Set<Passenger> passengers) {
        Map<String, List<HitEmailDto>> emailRecipientToDtos =
                passengers.stream()
                        .flatMap(passenger -> generateEmailDtoMapEntries(passenger).stream())
                        .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));

        return emailRecipientToDtos;

    }

    private static List<Map.Entry<String, HitEmailDto>> generateEmailDtoMapEntries(Passenger passenger) {
        List<Map.Entry<String, HitEmailDto>> emailToEmailDtoList =
                generateRecipientEmailList(passenger).stream()
                        .map(email -> new HashMap.SimpleEntry<>(email, new HitEmailDto()))
                        .collect(toList());

        return emailToEmailDtoList;
    }

    private static List<String> generateRecipientEmailList(Passenger passenger) {
        List<String> emailsToNotify = new ArrayList<>();

        Set<Notification> notifications = passenger.getNotifications();
        for(Notification notification: notifications) {
            if(notification.getClass().isInstance(UserGroupNotification.class)) {
                List<String> notificationEmailAddresses = ((UserGroupNotification) notification)
                        .getUserGroup().getGroupMembers().stream()
                        .map(User::getEmail).collect(toList());

                emailsToNotify.addAll(notificationEmailAddresses);
            } else {
                String notificationEmailAddress = ((UserNotification) notification)
                        .getUser().getEmail();

                emailsToNotify.add(notificationEmailAddress);
            }
        }

        return emailsToNotify;
    }
}
