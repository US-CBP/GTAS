/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.EdifactMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class RuleUtilsIT {
	private static final String testDrl = "package gov.gtas.rule; "
			+ "import gov.gtas.model.ApisMessage; "
			+ "global java.util.List resultList; "
			+ "rule \"RH1 - Hello Messsage\" "
			+ "dialect \"mvel\" "
			+ "when "
			+ "m:ApisMessage( edifactMessage.transmissionSource.equals(\"Hello\"), date:edifactMessage.transmissionDate ) "
			+ "then "
			+ "System.out.println( \"Transmission date =\"+date ); "
			+ "modify ( m ) { edifactMessage.transmissionSource = \"Goodbye\"}; "
			+ "end "
			+ "rule \"RH2 - GoodBye Messsage\" "
			+ "dialect \"java\" "
			+ "when "
			+ "m:ApisMessage( edifactMessage.transmissionSource.equals(\"Goodbye\"), date:edifactMessage.transmissionDate ) "
			+ "then " + "System.out.println( \"Got Goodbye\"); "
			+ "resultList.add(m.getEdifactMessage().getTransmissionDate()); "
			+ "end";

	@Test
	@Transactional
	public void testCreateKieBaseFromString() throws IOException {
		KieBase kbase = RuleUtils.createKieBaseFromDrlString(testDrl);
		assertNotNull("Expected non null KieBase", kbase);
		KieSession ksession = RuleUtils.createSession(kbase);
		assertNotNull("Expected non null KieSession", ksession);

		// set up the global
		ksession.setGlobal("resultList", new ArrayList<Object>());

		// insert the fact
		ApisMessage msg = new ApisMessage();
		EdifactMessage em = new EdifactMessage();
		em.setTransmissionSource("Hello");
		Date transmissionDate = new Date();
		em.setTransmissionDate(transmissionDate);
		msg.setEdifactMessage(em);
		ksession.insert(msg);

		// and fire the rules
		ksession.fireAllRules();

		// extract the result
		final List<?> resList = (List<?>) ksession.getGlobal("resultList");

		ksession.dispose();

		assertNotNull(resList);
		assertEquals("Result list is empty", 1, resList.size());
		assertEquals("Expected Transmission Date", transmissionDate,
				resList.get(0));
	}

	@Test
	@Transactional
	public void testMultipleSession() throws IOException {
		// verify that multiple sessions can be created with different IDs
		KieBase kbase = RuleUtils.createKieBaseFromDrlString(testDrl);
		KieSession s1 = RuleUtils.createSession(kbase);
		KieSession s2 = RuleUtils.createSession(kbase);
		assertNotEquals("Unexpected - got identical ids for two sessions",
				s1.getId(), s2.getId());
		s1.dispose();
		s2.dispose();
	}

	@Test
	@Transactional
	public void testBinarySerializationOfKieBase() throws IOException,
			ClassNotFoundException {
		KieBase kbase = RuleUtils.createKieBaseFromDrlString(testDrl);
		byte[] blob = RuleUtils.convertKieBaseToBytes(kbase);
		assertNotNull("ERROR - KieBase blob is null", blob);
		byte[] blobCopy = Arrays.copyOf(blob, blob.length);
		kbase = RuleUtils.convertKieBasefromBytes(blobCopy);
		assertNotNull("ERROR - could not get KieBase from blob", kbase);
		KieSession s = RuleUtils.createSession(kbase);
		assertNotNull("Could not Create KieSession from copied KieBase", s);
		s.dispose();
	}
}
