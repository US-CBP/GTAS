package gov.gtas.email;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.gtas.email.dto.HitEmailDTO;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class EmailTemplateLoader {

    private static final String HIGH_PROFILE_NOTIFICATION_FTL = "highProfileHitNotification.ftl";
    private static final String ACCOUNT_LOCKED_RESET_PASSWORD_FTL = "accountLockedResetPassword.ftl";

    @Resource
    private Configuration configuration;

    public String generateAccountLockedResetPasswordHtmlContent(String email, String resetLinkUrl) throws IOException, TemplateException {
        Template template = configuration.getTemplate(ACCOUNT_LOCKED_RESET_PASSWORD_FTL);

        Map<String, Object> model = new HashMap<>();
        model.put("userEmail", email);
        model.put("resetLinkUrl", resetLinkUrl);

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    public String generateHitEmailHtmlContent(Set<HitEmailDTO> hitEmailDTOs) throws IOException, TemplateException {
        Template template = configuration.getTemplate(HIGH_PROFILE_NOTIFICATION_FTL);
        Map<String, Object> model = new HashMap<>();
        model.put("hits", hitEmailDTOs);

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

}