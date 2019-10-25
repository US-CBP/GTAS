package gov.gtas.email;

import freemarker.template.*;
import gov.gtas.email.dto.HitEmailDTO;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Component
public class EmailTemplateLoader {

    @Resource
    private Configuration configuration;

    public String generateHtmlString(String templateName, List<HitEmailDTO> hitEmailDTOS) throws IOException, TemplateException {
        Template template = configuration.getTemplate(templateName);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, hitEmailDTOS);
    }

}