package gov.gtas.rule_app;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import gov.ruleapp.InboundRules;
import gov.ruleapp.RuleAppApplication;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(
//	    classes = RuleAppApplication.class)
class RuleAppApplicationTests {

	@Autowired
	private InboundRules inboundRules;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	void sendMessage() throws JsonMappingException, JsonProcessingException {
		System.out.println("starting test");
	//	inboundRules.inboundRules(null, null, null);
		System.out.println("test ending");

	}

}
