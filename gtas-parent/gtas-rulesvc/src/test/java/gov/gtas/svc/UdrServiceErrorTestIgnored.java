/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.constant.RuleErrorConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.error.CommonServiceException;
import gov.gtas.error.CommonValidationException;
import gov.gtas.model.User;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.JsonToDomainObjectConverter;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.HitCategoryService;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.services.udr.RulePersistenceService;
import gov.gtas.svc.util.UdrServiceHelper;
import gov.gtas.util.DateCalendarUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Ignore
public class UdrServiceErrorTestIgnored {

	private static final Logger logger = LoggerFactory.getLogger(UdrServiceErrorTestIgnored.class);

	private static final String TEST_JSON = "{ \"details\": {"
			+ "  \"@class\": \"gov.gtas.model.udr.json.QueryObject\"," + " \"condition\": \"OR\"," + " \"rules\": ["
			+ " {" + "   \"@class\": \"QueryTerm\"," + " \"entity\": \"%s\"," + " \"field\": \"%s\","
			+ " \"operator\": \"%s\"," + " \"type\": \"string\"," + " \"value\": [\"John\"]" + " }," + " {"
			+ " \"@class\": \"QueryTerm\"," + " \"entity\": \"Passenger\"," + " \"field\": \"lastName\","
			+ " \"operator\": \"EQUAL\"," + " \"type\": \"string\"," + " \"value\": [\"Jones\"]" + "}" + "]" + "},"
			+ " \"summary\": {" + " \"title\": \"Hello Rule 1\"," + " \"description\": \"This is a test\","
			+ " \"startDate\": \"%s\"," + " \"endDate\": null," + " \"author\": \"jpjones\"," + " \"enabled\": false"
			+ "}" + "}";
	private UdrService udrService;

	@Mock
	private RulePersistenceService mockRulePersistenceSvc;

	@Mock
	private UserService mockUserService;

	@Mock
	private UserServiceUtil mockUserServiceUtil;

	@Mock
	private AuditLogPersistenceService mockAuditLogPersistenceService;

	@Mock
	private RuleManagementService mockRuleManagementService;

	@Mock
	private HitCategoryService mockRuleCatService;

	@Before
	public void setUp() throws Exception {
		udrService = new UdrServiceImpl();
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(udrService, "rulePersistenceService", mockRulePersistenceSvc);
		HitCategory mockRuleCat = getMockRuleCat();
		Mockito.when(mockRuleCatService.findById(1L)).thenReturn(mockRuleCat);
		ReflectionTestUtils.setField(udrService, "userService", mockUserService);
		ReflectionTestUtils.setField(udrService, "auditLogPersistenceService", mockAuditLogPersistenceService);
	}

	private HitCategory getMockRuleCat() {
		HitCategory ruleCat = new HitCategory();
		ruleCat.setId(1L);
		return ruleCat;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = Exception.class)
	public void testCreateBadUser() {
		String testUser = "foo";
		when(mockUserService.findById(testUser)).thenReturn(null);
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec();
		spec.getSummary().setAuthor(testUser);
		when(mockUserService.fetchUser(testUser)).thenThrow(CommonServiceException.class);
		udrService.createUdr(testUser, spec);
		verify(mockUserService, times(1)).fetchUser(testUser);
	}

	@Test(expected = Exception.class)
	public void testCreateNullDetails() {
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec();
		spec.setDetails(null);
		String authorId = spec.getSummary().getAuthor();
		User author = new User();
		author.setUserId(authorId);
		when(mockUserService.fetchUser(authorId)).thenReturn(author);
		udrService.createUdr(authorId, spec);
		verify(mockUserService, times(1)).fetchUser(authorId);
	}

	@Test(expected = Exception.class)
	public void testCreateNullSummary() {
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec();
		String authorId = spec.getSummary().getAuthor();
		User author = new User();
		author.setUserId(authorId);
		spec.setSummary(null);
		udrService.createUdr(authorId, spec);
	}

	@Test(expected = Exception.class)
	public void testCreateBadJsonCondition() {
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec();
		String authorId = spec.getSummary().getAuthor();
		User author = new User();
		author.setUserId(authorId);
		spec.getDetails().setCondition("foo");
		when(mockUserService.fetchUser(authorId)).thenReturn(author);
		udrService.createUdr(authorId, spec);
		verify(mockUserService, times(1)).fetchUser(authorId);
	}

	@Test(expected = Exception.class)
	public void testCreateBadJsonRules() {
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec();
		String authorId = spec.getSummary().getAuthor();
		User author = new User();
		author.setUserId(authorId);
		spec.getDetails().setRules(null);
		when(mockUserService.fetchUser(authorId)).thenReturn(author);
		udrService.createUdr(authorId, spec);
		verify(mockUserService, times(1)).fetchUser(authorId);
	}

	@Test
	public void testCreateTodayDateOk() {
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec();
		String authorId = spec.getSummary().getAuthor();
		User author = new User();
		author.setUserId(authorId);
		try {
			when(mockUserService.fetchUser(authorId)).thenReturn(author);
			String today = DateCalendarUtils.formatJsonDate(new Date());
			logger.info(today);
			spec.getSummary().setStartDate(DateCalendarUtils.parseJsonDate(today));
			spec.getSummary().setEndDate(DateCalendarUtils.parseJsonDate(today));
			UdrRule rule = JsonToDomainObjectConverter.createUdrRuleFromJson(spec, author);
			rule.setId(1L);
			when(mockRulePersistenceSvc.create(any(UdrRule.class), any())).thenReturn(rule);
			List<UdrRule> rlList = new LinkedList<UdrRule>();
			rlList.add(rule);
			when(mockRulePersistenceSvc.findAll()).thenReturn(rlList);
			udrService.createUdr(authorId, spec);
		} catch (Exception ex) {
			logger.error("error!", ex);
			fail("Not Expecting Exception");
		}
		verify(mockUserService, times(1)).fetchUser(authorId);
		// verify(mockUserService, times(1)).findById(authorId);
		verify(mockRulePersistenceSvc).create(any(), any());
		verify(mockRulePersistenceSvc).findAll();
		verify(mockRuleManagementService).createKnowledgeBaseFromUdrRules(any(), any(), any());

	}

	@Test
	@Ignore
	public void testCreateYesterdayDateError() {
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec();
		String authorId = spec.getSummary().getAuthor();

		UserData authorData = new UserData(authorId, null, null, null, 0, null,"", false, false, false, "");
		User author = new User();
		author.setUserId(authorId);
		try {
			String yesterday = DateCalendarUtils.formatJsonDate(new Date(System.currentTimeMillis() - 86400000L));
			spec.getSummary().setStartDate(DateCalendarUtils.parseJsonDate(yesterday));
			UdrRule rule = JsonToDomainObjectConverter.createUdrRuleFromJson(spec, author);
			rule.setId(1L);
			when(mockRulePersistenceSvc.create(any(UdrRule.class), any())).thenReturn(rule);
			when(mockUserService.findById(authorId)).thenReturn(authorData);
			when(mockUserServiceUtil.mapUserEntityFromUserData(authorData)).thenReturn(author);
			udrService.createUdr(authorId, spec);
			fail("Expecting exception");
		} catch (CommonServiceException cse) {
			assertEquals(RuleErrorConstants.PAST_START_DATE_ERROR_CODE, cse.getErrorCode());
		} catch (Exception ex) {
			logger.error("error!", ex);
			fail("Not Expecting Exception");
		}
		verify(mockUserService, times(0)).findById(authorId);
	}

	@Test
	public void testInvalidEntity() {
		String authorId = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			// de-serialize
			// add arbitrary offset to counteract Jackson GMT interpretation
			String startDate = DateCalendarUtils.formatJsonDate(new Date(System.currentTimeMillis() + 864000000L));

			UdrSpecification testObj = mapper.readValue(String.format(TEST_JSON, "foo", "bar", "equal", startDate),
					UdrSpecification.class);
			assertNotNull(testObj);
			authorId = testObj.getSummary().getAuthor();
			UserData authorData = new UserData(authorId, null, null, null, 0, null, "", false, false, false, "");
			User author = new User();
			author.setUserId(authorId);
			UdrRule rule = JsonToDomainObjectConverter.createUdrRuleFromJson(testObj, author);
			UdrServiceHelper.addEngineRulesToUdrRule(rule, testObj);
			rule.setId(1L);
			when(mockRulePersistenceSvc.create(any(UdrRule.class), any())).thenReturn(rule);
			when(mockUserService.findById(authorId)).thenReturn(authorData);
			when(mockUserServiceUtil.mapUserEntityFromUserData(authorData)).thenReturn(author);
			udrService.createUdr(authorId, testObj);
			fail("Expecting exception");
		} catch (CommonValidationException cve) {
			assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, cve.getErrorCode());
		} catch (Exception ex) {
			logger.error("error!", ex);
			fail("Not Expecting Exception");
		}
		verify(mockUserService, times(0)).findById(authorId);
	}

	@Test
	public void testInvalidAttribute() {
		String authorId = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			// de-serialize

			// add arbitrary offset to counteract Jackson GMT interpretation
			String startDate = DateCalendarUtils.formatJsonDate(new Date(System.currentTimeMillis() + 864000000L));

			UdrSpecification testObj = mapper.readValue(String.format(TEST_JSON, "Pax", "bar", "equal", startDate),
					UdrSpecification.class);
			assertNotNull(testObj);
			authorId = testObj.getSummary().getAuthor();
			UserData authorData = new UserData(authorId, null, null, null, 0, null,"", false, false, false, "");
			User author = new User();
			author.setUserId(authorId);
			UdrRule rule = JsonToDomainObjectConverter.createUdrRuleFromJson(testObj, author);
			UdrServiceHelper.addEngineRulesToUdrRule(rule, testObj);
			rule.setId(1L);
			when(mockRulePersistenceSvc.create(any(UdrRule.class), any())).thenReturn(rule);
			when(mockUserService.fetchUser(authorId)).thenReturn(author);
			udrService.createUdr(authorId, testObj);
			fail("Expecting exception");
		} catch (CommonValidationException cve) {
			assertEquals(CommonErrorConstants.JSON_INPUT_VALIDATION_ERROR_CODE, cve.getErrorCode());
		} catch (Exception ex) {
			logger.error("error!", ex);
			fail("Not Expecting Exception");
		}
		verify(mockUserService, times(0)).fetchUser(authorId);
	}

	@Test
	public void testValidEntityAttribute() {
		String authorId = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			// de-serialize
			// add arbitrary offset to counteract Jackson GMT interpretation
			String startDate = DateCalendarUtils.formatJsonDate(new Date(System.currentTimeMillis() + 864000000L));

			UdrSpecification testObj = mapper.readValue(String.format(TEST_JSON, EntityEnum.PASSENGER.getEntityName(),
					PassengerMapping.DEBARKATION.getFieldName(), "equal", startDate), UdrSpecification.class);
			assertNotNull(testObj);
			authorId = testObj.getSummary().getAuthor();
			UserData authorData = new UserData(authorId, null, null, null, 0, null, "", false, false, false, "");

			User author = new User();
			author.setUserId(authorId);
			UdrRule rule = JsonToDomainObjectConverter.createUdrRuleFromJson(testObj, author);
			rule.setId(1L);
			when(mockRulePersistenceSvc.create(any(UdrRule.class), any())).thenReturn(rule);
			List<UdrRule> rlList = new LinkedList<UdrRule>();
			rlList.add(rule);
			when(mockRulePersistenceSvc.findAll()).thenReturn(rlList);
			when(mockUserService.fetchUser(authorId)).thenReturn(author);
			udrService.createUdr(authorId, testObj);
		} catch (Exception ex) {
			logger.error("error!", ex);
			fail("Not Expecting Exception");
		}
		verify(mockUserService, times(1)).fetchUser(authorId);
		verify(mockRulePersistenceSvc, times(1)).create(any(UdrRule.class), any());
		verify(mockRulePersistenceSvc).findAll();
		verify(mockRuleManagementService).createKnowledgeBaseFromUdrRules(any(), any(), any());
	}
}
