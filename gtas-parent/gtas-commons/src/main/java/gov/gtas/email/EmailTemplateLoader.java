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
import java.util.List;
import java.util.Map;

@Component
public class EmailTemplateLoader {

    @Resource
    private Configuration configuration;

    public String generateHtmlString(String templateName, List<HitEmailDTO> hitEmailDTOs) throws IOException, TemplateException {
        Template template = configuration.getTemplate(templateName);
        Map<String, Object> model = new HashMap<>();
        model.put("hits", hitEmailDTOs);

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

}