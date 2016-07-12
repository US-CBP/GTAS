/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The spring configuration class for the Rule Engine.<br>
 * It can be imported into an XML configuration by:<br>
 * &lt;context:annotation-config/&gt;<br>
 * &lt;bean class="gov.gtas.config.RuleServiceConfig"/&gt;
 */
@Configuration
@ComponentScan("gov.gtas")
@EnableTransactionManagement
public class RuleServiceConfig {
    @Resource
    private Environment env;

}
