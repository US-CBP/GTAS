package gov.gtas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.services.VersionService;

@RestController
public class VersionController {

	private static final Logger logger = LoggerFactory.getLogger(VersionController.class);

	@Autowired
	private VersionService versionService;

	@RequestMapping(value = "/applicationVersionNumber", method = RequestMethod.GET)
	public @ResponseBody String getApplicationVersionNumber() {

		String applicationVersionNumber = versionService.getApplicationVersionNumber();
		logger.info("APPLICATION VERSION: " + applicationVersionNumber);

		return applicationVersionNumber;

	}

}
