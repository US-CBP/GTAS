/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.test.util;

import static gov.gtas.util.DateCalendarUtils.formatJsonDate;
import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.model.udr.RuleMeta;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.util.DateCalendarUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;

/**
 * Generates test data for rules domain objects.
 */
public class RuleServiceDataGenUtils {
    public static final String TEST_RULE_TITLE_PREFIX = "TestRule";

    public static final int TEST_ROLE1_ID = 1;
    public static final String TEST_ROLE1_DESCRIPTION = "admin";
    public static final String TEST_USER1_ID = "test";
    public static final String TEST_USER3_ID = "jtang";

    public static final int TEST_ROLE2_ID = 99;
    public static final String TEST_ROLE2_DESCRIPTION = "readonly";
    public static final String TEST_USER2_ID = "svempati";

    private UserService userService;

    public RuleServiceDataGenUtils(UserService usrSvc) {
        this.userService = usrSvc;
    }

    public void initUserData() {
        try {
            Set<RoleData> roles = new HashSet<RoleData>();
            roles.add(new RoleData(1, "ADMIN"));
            UserData userData = new UserData("jJone", "password", "JP", "Jones", 1, roles,null);

            userService.create(userData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UdrRule createUdrRule(String title, String descr, YesNoEnum enabled) {
        return createUdrRule(title, descr, enabled, new Date(), null);
    }
    public UdrRule createUdrRule(String title, String descr, YesNoEnum enabled, Date startDate, Date endDate) {
        UdrRule rule = new UdrRule();
        if(enabled == YesNoEnum.Y){
          rule.setDeleted(YesNoEnum.N);
        } else {
            rule.setDeleted(YesNoEnum.Y);
        }
        rule.setEditDt(new Date());
        rule.setTitle(title);
        RuleMeta meta =  null;
        try{
            if(endDate != null){
               meta = createRuleMeta(title, descr, enabled, formatJsonDate(startDate),  formatJsonDate(endDate));
            } else {
                   meta = createRuleMeta(title, descr, enabled, formatJsonDate(startDate), null);               
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        rule.setMetaData(meta);
        return rule;
    }

    public String generateTestRuleTitle(int ruleIndx) {
        StringBuilder bldr = new StringBuilder(TEST_RULE_TITLE_PREFIX);
        bldr.append(ruleIndx).append('.');
        bldr.append(ThreadLocalRandom.current().nextInt(1, 10));

        return bldr.toString();
    }

    private RuleMeta createRuleMeta(String title, String descr,
            YesNoEnum enabled, String jsonStartDate, String jsonEndDate) throws ParseException{
        RuleMeta meta = new RuleMeta();
        meta.setDescription(descr);
        meta.setEnabled(enabled);
        meta.setHitSharing(YesNoEnum.N);
        meta.setPriorityHigh(YesNoEnum.N);
        meta.setStartDt(DateCalendarUtils.parseJsonDate(jsonStartDate));
        if(!StringUtils.isEmpty(jsonEndDate)){
            meta.setEndDt(DateCalendarUtils.parseJsonDate(jsonEndDate));            
        }
        meta.setTitle(title);
        return meta;
    }
}
