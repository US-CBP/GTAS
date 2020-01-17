package gov.gtas.email;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.gtas.email.dto.HitEmailDTO;
import gov.gtas.services.dto.SignupRequestDTO;

import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class EmailTemplateLoader {

	@Resource
	private Configuration configuration;

	public String generateHtmlString(String templateName, Set<HitEmailDTO> hitEmailDTOs)
			throws IOException, TemplateException {
		Template template = configuration.getTemplate(templateName);
		Map<String, Object> model = new HashMap<>();
		model.put("hits", hitEmailDTOs);

		return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
	}

	public String signupRequestEmailHtmlString(String templateName, SignupRequestDTO signupRequestDTO, String password)
			throws IOException, TemplateException {
		Map<String, Object> model = new HashMap<>();
		model.put("signupRequest", signupRequestDTO);
		model.put("tempPassword", password);
		return this.generateHtmlString(templateName, model);
	}

	public String signupRequestEmailHtmlString(String templateName, SignupRequestDTO signupRequestDTO)
			throws IOException, TemplateException {
		Map<String, Object> model = new HashMap<>();
		model.put("signupRequest", signupRequestDTO);
		return this.generateHtmlString(templateName, model);
	}

	private String generateHtmlString(String templateName, Map<String, Object> model)
			throws IOException, TemplateException {
		Template template = configuration.getTemplate(templateName);
		return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
	}
}