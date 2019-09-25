/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.common.GtasResourceBundleMessageSource;

import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GtasMessageBundleController {

	@Autowired
	ApplicationContext ctx;

	GtasResourceBundleMessageSource messageBundle = new GtasResourceBundleMessageSource();

	/**
	 * ReadAll
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/messageBundle/")
	public Properties list(@RequestParam String lang) {
		messageBundle = (GtasResourceBundleMessageSource) ctx.getBean("gtasMessageSource");

		return messageBundle.getAllProperties(new Locale(lang));
	}

}
