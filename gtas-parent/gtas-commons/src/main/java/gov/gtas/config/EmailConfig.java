package gov.gtas.config;

import freemarker.template.Configuration;
import freemarker.template.Version;
import gov.gtas.email.EmailTemplateLoader;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class EmailConfig {

    private static final String TEMPLATE_BASE_PATH =  "/emailtemplate/";
    private static final String VERSION_NUMBER = "2.3.23";
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Bean
    public Configuration configuration() {
        Configuration cfg = new Configuration(new Version(VERSION_NUMBER));
        cfg.setClassForTemplateLoading(EmailTemplateLoader.class, TEMPLATE_BASE_PATH);
        cfg.setDefaultEncoding(DEFAULT_ENCODING);
        return cfg;
    }

}
