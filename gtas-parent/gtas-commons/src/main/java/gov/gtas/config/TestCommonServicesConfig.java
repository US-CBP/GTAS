/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * The configuration class can be imported into an XML configuration by:<br>
 * <context:annotation-config/>
 * <bean class="gov.gtas.config.TestCommonServicesConfig"/>
 */
//@Configuration
//@ComponentScan("gov.gtas")
//@PropertySource({ "classpath:default.application.properties"})
//@EnableJpaRepositories("gov.gtas")
//@EnableTransactionManagement
//@Import({ AsyncConfig.class, CommonServicesConfig.class })
public class TestCommonServicesConfig {

 /*
   @Value("${email.sender.username}")
    private String mailSenderUserName;

    @Value("${email.sender.password}")
    private String mailSenderPassword;

    @Value("${mail.smtp.auth}")
    private String maiilSenderSmptAuth;

    @Value("${mail.smtp.starttls.enable}")
    private String mailSenderSmtpStarttlsEnable;

    @Value("${mail.smtp.host}")
    private String mailSenderHost;

    @Value("${mail.smtp.port}")
    private String mailSenderPort;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setUsername(mailSenderUserName);
        mailSender.setPassword(mailSenderPassword);

        Properties props = new Properties();
        props.put("mail.smtp.user", mailSenderUserName);
        props.put("mail.smtp.password", mailSenderPassword);
        props.put("mail.smtp.auth", maiilSenderSmptAuth);
        props.put("mail.smtp.starttls.enable", mailSenderSmtpStarttlsEnable);
        props.put("mail.smtp.host", mailSenderHost);
        props.put("mail.smtp.port", mailSenderPort);

        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

*/
}
