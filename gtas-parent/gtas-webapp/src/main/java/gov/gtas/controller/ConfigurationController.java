/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.controller;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Base64.Encoder;

@RestController
public class ConfigurationController {

	private Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	@Value("${default.landing.page}")
	String defaultLandingPage;

	@Value("${neo4j.protocol}")
	String neo4jProtocol;

	@Value("${kibana.protocol}")
	String kibanaProtocol;

	@Value("${neo4j.url}")
	String neo4jUrl;

	@Value("${kibana.url}")
	String kibanaUrl;

	@Value("${cypher.url}")
	String cypherUrl;

	@Value("${neo4j.username}")
	String neo4jusername;

	@Value("${neo4j.password}")
	String neo4jpassword;

	@Value("${agency.name}")
	String agencyName;

	@Value("${enable.email.notification.service}")
	String enableEmailNotification;
	
	@Deprecated
	@GetMapping(value = "/api/config/dashboard", produces = "text/plain")
	public String getDashboard() {
		String landingPage;
		if (defaultLandingPage == null) {
			logger.warn("No default landing page set, setting to flight! Set default page in settings!");
			landingPage = "flight";
		} else {
			landingPage = defaultLandingPage;
		}
		return landingPage;
	}

	@Deprecated
	@GetMapping(value = "/api/config/kibanaProtocol", produces = "text/plain")
	public String getKibanaProtocol() {
		return kibanaProtocol;
	}

	@Deprecated
	@GetMapping(value = "/api/config/neo4jProtocol", produces = "text/plain")
	public String getNeo4JProtocol() {
		return neo4jProtocol;
	}

	@Deprecated
	@GetMapping(value = "/api/config/kibanaUrl", produces = "text/plain")
	public String getDashboardUrl() {
		return kibanaUrl;
	}

	@Deprecated
	@GetMapping(value = "/api/config/neo4j", produces = "text/plain")
	public String getNeo4J() {
		return neo4jUrl;
	}

	@Deprecated
	@GetMapping(value = "/api/config/cypherUrl", produces = "text/plain")
	public String getCypherUrl() {
		return cypherUrl;
	}

	@Deprecated
	@GetMapping(value = "/api/config/cypherAuth", produces = "text/plain")
	public String getCypherAuth() {
		Encoder enc = Base64.getEncoder();
		String authString = neo4jusername + ":" + neo4jpassword;
		String authEncoded = enc.encodeToString(authString.getBytes());

		return "Basic " + authEncoded;
	}

	@Deprecated
	@GetMapping(value = "/api/config/agencyName", produces = "text/plain")
	public String getAgencyName() {
		return agencyName;
	}

	@Deprecated
	@GetMapping(value = "/api/config/enableEmailNotification", produces = "text/plain")
	public String isEmailNotificationEnabled() {
		return enableEmailNotification;
	}

	@GetMapping(value = "/config/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse dashboard() {
		String landingPage;
		if (defaultLandingPage == null) {
			logger.warn("No default landing page set, setting to flight! Set default page in settings!");
			landingPage = "flight";
		} else {
			landingPage = defaultLandingPage;
		}
		
		return new JsonServiceResponse(Status.SUCCESS, "landing page", landingPage);
	}

	@GetMapping(value = "/config/kibanaProtocol", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse kibanaProtocol() {
		return new JsonServiceResponse(Status.SUCCESS, "Kibana protocol", kibanaProtocol);
	}

	@GetMapping(value = "/config/neo4jProtocol", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse neo4JProtocol() {
		return new JsonServiceResponse(Status.SUCCESS, "Neo4j protocol", neo4jProtocol);
	}

	@GetMapping(value = "/config/kibanaUrl", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse dashboardUrl() {
		return new JsonServiceResponse(Status.SUCCESS, "Kibana url", kibanaUrl);
	}

	@GetMapping(value = "/config/neo4j", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse getNeo4JUrl() {
		
		return new JsonServiceResponse(Status.SUCCESS, "Neo4j url", neo4jUrl);
		
	}

	@GetMapping(value = "/config/cypherUrl", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse cypherUrl() {
		return new JsonServiceResponse(Status.SUCCESS, "Cypher url", cypherUrl);
	}

	@GetMapping(value = "/config/cypherAuth", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse cypherAuth() {
		Encoder enc = Base64.getEncoder();
		String authString = neo4jusername + ":" + neo4jpassword;
		String authEncoded = enc.encodeToString(authString.getBytes());
		
		return new JsonServiceResponse(Status.SUCCESS, "Cypher Auth",  authEncoded);
	}

	@GetMapping(value = "/config/agencyName", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse agencyName() {
		return new JsonServiceResponse(Status.SUCCESS, "Agency name",  agencyName);
	}

	@GetMapping(value = "/config/enableEmailNotification", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonServiceResponse emailNotificationEnabled() {
		return new JsonServiceResponse(Status.SUCCESS, "Emaile notification enabled",  enableEmailNotification);
		
	}
}
