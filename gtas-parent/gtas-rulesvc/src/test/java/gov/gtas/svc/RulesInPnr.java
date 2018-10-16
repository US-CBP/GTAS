package gov.gtas.svc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Pnr;
import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.rule.builder.QueryTermFactory;
import gov.gtas.services.Loader;
import gov.gtas.svc.util.RuleExecutionContext;
import gov.gtas.svc.util.TargetingServiceUtils;
import gov.gtas.testdatagen.TestDataAllRules;
import gov.gtas.util.DateCalendarUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CommonServicesConfig.class,
        CachingConfig.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class RulesInPnr {

    @Autowired
    private Loader loader;

    @Autowired
    private TargetingService targetingService;

    @Before
    @Transactional
    public void setUp() {

    }

    @Test
    @Transactional
    public void runRulesOnPnr3() throws IOException {
        assertEquals(Collections.EMPTY_LIST, targetingService.retrievePnr(MessageStatus.LOADED));
        ClassPathResource resource = new ClassPathResource("pnrForSingleRuleHits.txt");
        File pnrFile = resource.getFile();
        loader.processMessage(pnrFile, new String[]{"placeholder"});
        List<QueryTerm> ruleList = listFromRuleDescriptionJson("rulesForSingleRuleHits.json");
        TestDataAllRules testDataAllRules = new TestDataAllRules();
        testDataAllRules.generateRulesFromQueryTerms(ruleList);
        List<Pnr> msgs = targetingService.retrievePnr(MessageStatus.LOADED);
        if (msgs != null) {
            RuleExecutionContext ctx = TargetingServiceUtils
                    .createPnrRequestContext(msgs);
            RuleServiceRequest request = ctx.getRuleServiceRequest();
            RuleServiceResult result = targetingService.applyRules(request, testDataAllRules.getDrlString());
            assertNotNull(result);
        } else {
            fail("Expected rule hits");
            //If no rules hit throw assertion error?
        }
    }

    public static List<QueryTerm> listFromRuleDescriptionJson(String relativeFilePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(relativeFilePath);
        JsonElement root = new JsonParser().parse(new FileReader(resource.getURL().getPath()));
        JsonObject object = root.getAsJsonObject();
        Gson gson = new Gson();
        return gson.fromJson(object, RuleDescription.class).getQueryTerms();
    }


    public static UdrSpecification createSampleSpec3(String userId,
                                                     String title, String description) {
        final UdrSpecificationBuilder builder = new UdrSpecificationBuilder(null,
                QueryConditionEnum.OR);
        QueryTermFactory qtf = new QueryTermFactory();
        builder.addNestedQueryObject(QueryConditionEnum.AND);
        builder.addTerm(qtf.create(PassengerMapping.DOB,
                CriteriaOperatorEnum.EQUAL,
                new String[]{DateCalendarUtils.formatJsonDate(new Date())}));
        builder.addTerm(qtf.create(PassengerMapping.LAST_NAME, CriteriaOperatorEnum.EQUAL,
                new String[]{"Jones"}));
        builder.endCurrentQueryObject();

        builder.addNestedQueryObject(QueryConditionEnum.AND);
        builder.addTerm(qtf.create(
                PassengerMapping.EMBARKATION,
                CriteriaOperatorEnum.IN,
                new String[]{
                        "DBY", "PKY", "FLT"}));
        builder.addTerm(qtf.create(
                PassengerMapping.DEBARKATION,
                CriteriaOperatorEnum.EQUAL,
                new String[]{"IAD"}));
        builder.addMeta(title, description, new Date(), null, true, userId);
        return builder.build();
    }
}
