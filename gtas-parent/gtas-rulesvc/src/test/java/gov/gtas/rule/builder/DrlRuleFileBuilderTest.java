/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleBuilderTestUtils.FLIGHT_ETA_ETD_RULE_INDX;
import static gov.gtas.rule.builder.RuleBuilderTestUtils.PASSENGER_SEAT_RULE_INDX;
import static gov.gtas.rule.builder.RuleBuilderTestUtils.PNR_SEAT_RULE_INDX;
import static gov.gtas.rule.builder.RuleBuilderTestUtils.UDR_RULE_TITLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.rule.RuleUtils;

import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class DrlRuleFileBuilderTest {
    public static final String UDR_RULE_AUTHOR="adelorie";
    private DrlRuleFileBuilder testTarget;
    @Before
    public void setUp() throws Exception {
        testTarget = new DrlRuleFileBuilder();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEtaEtdCompilation() {
        try{
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, FLIGHT_ETA_ETD_RULE_INDX);
        testTarget.addRule(udrRule);
        String result = testTarget.build();
        verifyDrl(result, FLIGHT_ETA_ETD_RULE_INDX);
        testKnowledgeBaseTest(result);
        }catch (Exception ex){
            ex.printStackTrace();
            fail("Not expecting exception.");
        }
    }
    @Test
    public void testSimpleRuleGenerationAndCompilation1() {
        try{
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, 1);
        testTarget.addRule(udrRule);
        String result = testTarget.build();
        verifyDrl(result, 1);
        testKnowledgeBaseTest(result);
        }catch (Exception ex){
            ex.printStackTrace();
            fail("Not expecting exception.");
        }
    }
    @Test
    public void testSimpleRuleGenerationAndCompilation2() {
        try{
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, 2);
        testTarget.addRule(udrRule);
        String result = testTarget.build();
        verifyDrl(result, 2);
        testKnowledgeBaseTest(result);
        }catch (Exception ex){
            ex.printStackTrace();
            fail("Not expecting exception.");
        }
    }
    @Test
    public void testSimpleRuleGenerationAndCompilation4() {
        try{
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, 4);
        testTarget.addRule(udrRule);
        String result = testTarget.build();
        verifyDrl(result, 4);
        testKnowledgeBaseTest(result);
        }catch (Exception ex){
            ex.printStackTrace();
            fail("Not expecting exception.");
        }
    }
    @Test
    public void testSimpleRuleGenerationAndCompilation5() {
        try{
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, PASSENGER_SEAT_RULE_INDX);
        testTarget.addRule(udrRule);
        String result = testTarget.build();
        verifyDrl(result, PASSENGER_SEAT_RULE_INDX);
        testKnowledgeBaseTest(result);
        }catch (Exception ex){
            ex.printStackTrace();
            fail("Not expecting exception.");
        }
    }
    @Test
    public void testSimpleRuleGenerationAndCompilation6() {
        try{
        UdrRule udrRule = RuleBuilderTestUtils.createSimpleUdrRule(UDR_RULE_AUTHOR, PNR_SEAT_RULE_INDX);
        testTarget.addRule(udrRule);
        String result = testTarget.build();
        verifyDrl(result, PNR_SEAT_RULE_INDX);
        testKnowledgeBaseTest(result);
        }catch (Exception ex){
            ex.printStackTrace();
            fail("Not expecting exception.");
        }
    }
    public static void verifyDrl(String drl, int indx){
        String target = "rule \""+UDR_RULE_TITLE+":"+UDR_RULE_AUTHOR+":"+indx+"\"";
        assertTrue(drl.indexOf(target) > 0);
    }
    private void testKnowledgeBaseTest(String testDrl) throws IOException, ClassNotFoundException{
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
