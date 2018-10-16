package gov.gtas.testdatagen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.ParserTestHelper;
import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.QueryTerm;
import gov.gtas.querybuilder.mappings.*;
import gov.gtas.rule.builder.DrlRuleFileBuilder;
import gov.gtas.rule.builder.QueryTermFactory;
import gov.gtas.svc.RuleDescription;

import java.time.LocalDate;
import java.util.*;

import static gov.gtas.rule.builder.RuleBuilderTestUtils.*;
import static gov.gtas.util.DateCalendarUtils.formatJsonDate;


public class TestDataAllRules implements ParserTestHelper {




    private Map<String, List<String>> lookUp = new HashMap<>();
    private String drlString = "";
    private static final String FOO = "Foo";
    private static final String BAR = "Bar";
    private static final String UNDERSCORE = "_";
    private List<IEntityMapping> iEntityMappings;
    private EnumSet<CriteriaOperatorEnum> criteriaOperatorEnums;

    public TestDataAllRules() {
        this.iEntityMappings = getiEntityMappings();
        this.criteriaOperatorEnums = EnumSet.of(CriteriaOperatorEnum.EQUAL);
    }

    public TestDataAllRules(List<IEntityMapping> iEntityMappings, EnumSet<CriteriaOperatorEnum> criteriaOperatorEnums) {
        this.iEntityMappings = iEntityMappings;
        this.criteriaOperatorEnums = criteriaOperatorEnums;
    }

    public void generateRules() {
        List<QueryTerm> allQueryTerms = getQueryTerms(iEntityMappings, criteriaOperatorEnums);
        DrlRuleFileBuilder drlRuleFileBuilder = getFullRuleBuilder(allQueryTerms);
        drlString = drlRuleFileBuilder.build();
    }
    public void generateRulesFromQueryTerms(List<QueryTerm> allQueryTerms) {
        DrlRuleFileBuilder drlRuleFileBuilder = getFullRuleBuilder(allQueryTerms);
        drlString = drlRuleFileBuilder.build();
    }

    private DrlRuleFileBuilder getFullRuleBuilder(List<QueryTerm> allQueryTerms) {
        DrlRuleFileBuilder drlRuleFileBuilder = new DrlRuleFileBuilder();
        for (QueryTerm queryTerm : allQueryTerms) {
            UdrRule udrRule = createBaseUdrRule("123");
            String ruleDetails = queryTerm.getEntity().toUpperCase() + queryTerm.getField() + queryTerm.getOperator();
            udrRule.setTitle(ruleDetails);
            udrRule.getMetaData().setTitle(ruleDetails);
            List<QueryTerm> queryTerms = new LinkedList<>();
            queryTerms.add(queryTerm);
            udrRule.addEngineRule(generateRule(udrRule, queryTerms));
            drlRuleFileBuilder.addRule(udrRule);
        }
        return drlRuleFileBuilder;
    }

    private List<QueryTerm> getQueryTerms(List<IEntityMapping> iEntityMappings, EnumSet<CriteriaOperatorEnum> criteriaOperatorEnums) {
        List<QueryTerm> allQueryTerms = new LinkedList<>();
        LocalDate date = LocalDate.of(2014, 2, 11);
        Integer seedInt = 10;
        QueryTermFactory qtf = new QueryTermFactory();
        for (IEntityMapping iEntityMapping : iEntityMappings) {
            for (CriteriaOperatorEnum co : criteriaOperatorEnums) {
                String key = getKey(iEntityMapping, co);
                String value1;
                String value2;
                TypeEnum iEntityType = TypeEnum.getEnum(iEntityMapping.getFieldType());
                switch (iEntityType) {
                    case INTEGER:
                    case LONG:
                    case DOUBLE:
                        value1 = seedInt.toString();
                        seedInt += 10;
                        value2 = seedInt.toString();
                        break;
                    case DATE:
                    case TIME:
                    case DATETIME:
                        value1 = formatJsonDate(getDateFromLocalDate(date));
                        date = date.plusDays(10L);
                        value2 = formatJsonDate(getDateFromLocalDate(date));
                        break;
                    case BOOLEAN:
                        value1 = "1";
                        value2 = "0";
                        break;
                    case STRING:
                    default:
                        value1 = FOO + UNDERSCORE + key;
                        value2 = BAR + UNDERSCORE + key;
                        break;
                }
                System.out.println("Rule name and value(1): " + key + " + " + value1);

               if (co != CriteriaOperatorEnum.EQUAL) {
                   System.out.println("Rule name and value(2): " + key + " + " + value2);
               }
                String[] values = new String[]{value1, value2};
                QueryTerm queryTerm = qtf.create(iEntityMapping, co, values);
                lookUp.put(key, Arrays.asList(values));
                allQueryTerms.add(queryTerm);
            }
        }
        return allQueryTerms;
    }

    private List<IEntityMapping> getiEntityMappings() {
        List<IEntityMapping> iEntityMappings = new ArrayList<>();
        iEntityMappings.addAll(Arrays.asList(AddressMapping.values()));
        iEntityMappings.addAll(Arrays.asList(BagMapping.values()));
        iEntityMappings.addAll(Arrays.asList(BookingDetailMapping.values()));
        iEntityMappings.addAll(Arrays.asList(CreditCardMapping.values()));
        iEntityMappings.addAll(Arrays.asList(DocumentMapping.values()));
        iEntityMappings.addAll(Arrays.asList(DwellTimeMapping.values()));
        iEntityMappings.addAll(Arrays.asList(EmailMapping.values()));
        iEntityMappings.addAll(Arrays.asList(FlightMapping.values()));
        iEntityMappings.addAll(Arrays.asList(FlightPaxMapping.values()));
        iEntityMappings.addAll(Arrays.asList(FrequentFlyerMapping.values()));
 //       iEntityMappings.addAll(Arrays.asList(HitsMapping.values()));
        iEntityMappings.addAll(Arrays.asList(PassengerMapping.values()));
        iEntityMappings.addAll(Arrays.asList(PhoneMapping.values()));
        iEntityMappings.addAll(Arrays.asList(PNRMapping.values()));
        iEntityMappings.addAll(Arrays.asList(TravelAgencyMapping.values()));
        return iEntityMappings;
    }

    private String getKey(IEntityMapping iEntityMapping, CriteriaOperatorEnum co) {
        String fieldName = iEntityMapping.getFieldName().toUpperCase();
        String conditionalOperation = co.toString().toUpperCase();
        return fieldName + UNDERSCORE + conditionalOperation;
    }

    public List<String> getValues(IEntityMapping iEntityMapping, CriteriaOperatorEnum co) {
        String key = getKey(iEntityMapping, co);
        return lookUp.get(key);
    }

    public String getDrlString() {
        return drlString;
    }

    public void getJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<QueryTerm> queryTerms = getQueryTerms(iEntityMappings, criteriaOperatorEnums);
        String json  ="";

        RuleDescription ruleDescription = new RuleDescription();
        ruleDescription.setQueryTerms(queryTerms);

        System.out.println(mapper.writeValueAsString(ruleDescription));


    }

}
