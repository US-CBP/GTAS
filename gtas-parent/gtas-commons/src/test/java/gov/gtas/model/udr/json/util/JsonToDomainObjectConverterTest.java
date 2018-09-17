/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.User;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.UdrSpecification;

import java.io.IOException;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonToDomainObjectConverterTest {
    private static final Logger logger = LoggerFactory.getLogger(JsonToDomainObjectConverter.class);
    private static final String UDR_TITLE = "test";
    private static final String UDR_DESCRIPTION = "test_descr";
    private static final String UDR_AUTHOR = "jpjones";
    private static final Long UDR_ID = new Long(251);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBasicWithId() {
        UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(UDR_AUTHOR, UDR_TITLE, UDR_DESCRIPTION);
        logger.info(spec.toString());
        Date startDate = spec.getSummary().getStartDate();
        Date endDate = new Date(System.currentTimeMillis() + 86400000L);//one day in the future
        spec.getSummary().setEndDate(endDate);
        spec.setId(UDR_ID);
        
        UdrRule rule = null;
        try {
            User author = new User();
            author.setUserId(spec.getSummary().getAuthor());
            
            rule = JsonToDomainObjectConverter.createUdrRuleFromJson(spec,
                    author);
            
            assertNotNull("Rule blob is null", rule.getUdrConditionObject());
            assertEquals(UDR_AUTHOR, rule.getAuthor().getUserId());
            assertEquals("rule id is null", UDR_ID, rule.getId());
            assertEquals("meta data id is null",UDR_ID, rule.getMetaData().getId());
            assertNotNull(rule);
            RuleMeta meta = rule.getMetaData();
            verifyMeta(meta, UDR_TITLE, UDR_DESCRIPTION, startDate, endDate, true);
        } catch (IOException ioe) {
            logger.error("error!", ioe);
            fail("Not expecting exception");
        }

    }

    @Test
    public void testBasicWithNullId() {     
        UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(UDR_AUTHOR, UDR_TITLE, UDR_DESCRIPTION);
        Date startDate = spec.getSummary().getStartDate();
        Date endDate = new Date(System.currentTimeMillis() + 86400000L);//one day in the future
        spec.getSummary().setEndDate(endDate);
        
        UdrRule rule = null;
        try {
            User author = new User();
            author.setUserId(spec.getSummary().getAuthor());
            
            rule = JsonToDomainObjectConverter.createUdrRuleFromJson(spec,
                    author);
            assertNotNull("Rule blob is null", rule.getUdrConditionObject());
            assertEquals(UDR_AUTHOR, rule.getAuthor().getUserId());
            assertNull("rule id is  not null", rule.getId());
            assertNull("meta data id is not null", rule.getMetaData().getId());
            assertNotNull(rule);
            RuleMeta meta = rule.getMetaData();
            verifyMeta(meta, UDR_TITLE, UDR_DESCRIPTION, startDate, endDate, true);
        } catch (IOException ioe) {
            logger.error("error!", ioe);
            fail("Not expecting exception");
        }

    }
    @Test
    public void testMetaOnly() {
        UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(UDR_AUTHOR, UDR_TITLE, UDR_DESCRIPTION);
        Date startDate = spec.getSummary().getStartDate();
        Date endDate = new Date(System.currentTimeMillis() + 86400000L);//one day in the future
        spec.getSummary().setEndDate(endDate);
        spec.setId(UDR_ID);
        
        RuleMeta meta = JsonToDomainObjectConverter.extractRuleMeta(spec);
        verifyMeta(meta, UDR_TITLE, UDR_DESCRIPTION, startDate, endDate, true);
        
    }
    
    private void verifyMeta(RuleMeta meta, String title, String descr,
            Date startDate, Date endDate, boolean enabled) {
        assertNotNull(meta);
        assertEquals(title, meta.getTitle());
        assertEquals(descr, meta.getDescription());
        assertEquals(startDate, meta.getStartDt());
        assertEquals(endDate, meta.getEndDt());
        assertEquals(enabled, meta.getEnabled() == YesNoEnum.Y ? true : false);
    }

}
