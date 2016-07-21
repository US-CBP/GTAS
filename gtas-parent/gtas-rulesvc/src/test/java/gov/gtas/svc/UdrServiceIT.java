/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc;

import static gov.gtas.constant.DomainModelConstants.UDR_UNIQUE_CONSTRAINT_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.gtas.config.RuleServiceConfig;
import gov.gtas.constant.RuleConstants;
import gov.gtas.enumtype.ConditionEnum;
import gov.gtas.enumtype.Status;
import gov.gtas.error.ErrorUtils;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.udr.json.JsonUdrListElement;
import gov.gtas.model.udr.json.QueryEntity;
import gov.gtas.model.udr.json.QueryObject;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.JsonToDomainObjectConverter;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserData;
import gov.gtas.services.security.UserService;
import gov.gtas.services.security.UserServiceUtil;
import gov.gtas.services.udr.RulePersistenceService;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Integration tests for the UDR management service.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RuleServiceConfig.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class UdrServiceIT {

	private static final String RULE_TITLE1 = "Hello Rule 1";
	private static final String RULE_DESCRIPTION1 = "This is a test";
	private static final String RULE_TITLE2 = "Hello Rule 2";
	private static final String RULE_DESCRIPTION2 = "This is a test2";

	private static final String USERID_1 = "phenry";
	private static final String USER1_FIRST_NAME = "Patrick";
	private static final String USER1_LAST_NAME = "Henry";

	private static final String USERID_2 = "jpjones";
	private static final String USER2_FIRST_NAME = "John";
	private static final String USER2_LAST_NAME = "Jones";

	@Autowired
	UdrService udrService;

	@Autowired
	RulePersistenceService ruleService;

	@Autowired
	UserService userService;

	@Autowired
	UserServiceUtil userServiceUtil;

	@Autowired
	private AuditLogPersistenceService auditLogPersistenceService;

	@Test
	@Transactional
	public void testCreateUdr() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
	}

	@Test
	@Transactional
	public void testCopyUdrWithLongTitle() {
		User user = createUser();
		String title = "12345678901234567890";// max length title (20
		// characters)
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), title, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());

		assertTrue(resp.getResult() instanceof Long);
		Long id = (Long) resp.getResult();

		resp = udrService.copyUdr(user.getUserId(), id);
		assertEquals(Status.SUCCESS, resp.getStatus());
		id = (Long) resp.getResult();
		UdrSpecification newspec = udrService.fetchUdr(id);
		assertNotNull(newspec);
		assertEquals(title.substring(3) + "##1", newspec.getSummary()
				.getTitle());
	}

	@Test
	@Transactional
	public void testCopyUdr() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());

		assertTrue(resp.getResult() instanceof Long);
		Long id = (Long) resp.getResult();

		resp = udrService.copyUdr(user.getUserId(), id);
		assertEquals(Status.SUCCESS, resp.getStatus());
		id = (Long) resp.getResult();
		UdrSpecification newspec = udrService.fetchUdr(id);
		assertNotNull(newspec);
		assertEquals(spec.getSummary().getTitle() + "##1", newspec.getSummary()
				.getTitle());

		// second copy
		resp = udrService.copyUdr(user.getUserId(), id);
		assertEquals(Status.SUCCESS, resp.getStatus());
		id = (Long) resp.getResult();
		newspec = udrService.fetchUdr(id);
		assertNotNull(newspec);
		assertEquals(spec.getSummary().getTitle() + "##2", newspec.getSummary()
				.getTitle());

	}

	@Test
	@Transactional
	public void testCreateDuplicateUdr() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		try {
			udrService.createUdr(user.getUserId(), spec);
			fail("Expecting Exception");
		} catch (JpaSystemException jse) {
			assertTrue(ErrorUtils.isConstraintViolationException(jse,
					UDR_UNIQUE_CONSTRAINT_NAME));
		}
	}

	@Test
	@Transactional
	public void testCreateUdrWithSingleConditionAND() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		QueryObject details = spec.getDetails();
		details.setCondition(ConditionEnum.AND.toString());
		List<QueryEntity> terms = details.getRules();
		List<QueryEntity> newterms = new LinkedList<QueryEntity>();
		newterms.add(terms.get(0));
		details.setRules(newterms);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf(resp
				.findResponseDetailValue(RuleConstants.UDR_ID_ATTRIBUTE_NAME));
		assertNotNull("The saved ID is null", id);
		UdrRule rule = ruleService.findById(id);
		assertNotNull(rule);
		List<Rule> engineRules = rule.getEngineRules();
		assertNotNull(engineRules);
		assertEquals(1, engineRules.size());
		String drl = engineRules.get(0).getRuleDrl();
		assertFalse(StringUtils.isEmpty(drl));
		String[] criteria = engineRules.get(0).getRuleCriteria();
		assertNotNull(criteria);
		assertEquals(1, criteria.length);
	}

	@Test
	@Transactional
	public void testCreateUdrWithSingleConditionOR() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		QueryObject details = spec.getDetails();
		details.setCondition(ConditionEnum.OR.toString());
		List<QueryEntity> terms = details.getRules();
		List<QueryEntity> newterms = new LinkedList<QueryEntity>();
		newterms.add(terms.get(0));
		details.setRules(newterms);

		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);

		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf(resp
				.findResponseDetailValue(RuleConstants.UDR_ID_ATTRIBUTE_NAME));
		assertNotNull("The saved ID is null", id);
		UdrRule rule = ruleService.findById(id);
		assertNotNull(rule);
		List<Rule> engineRules = rule.getEngineRules();
		assertNotNull(engineRules);
		assertEquals(1, engineRules.size());
		String drl = engineRules.get(0).getRuleDrl();
		assertFalse(StringUtils.isEmpty(drl));
		String[] criteria = engineRules.get(0).getRuleCriteria();
		assertNotNull(criteria);
		assertEquals(1, criteria.length);
		assertNotNull("Engine Rule has a null Knowledge Base reference",
				engineRules.get(0).getKnowledgeBase());
	}

	@Test
	@Transactional
	public void testFetchSummaryList() {
		User user = createUser();
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		UdrSpecification spec2 = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE2, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService
				.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		resp = udrService.createUdr(user.getUserId(), spec2);
		assertEquals(Status.SUCCESS, resp.getStatus());
		List<JsonUdrListElement> listResp = udrService.fetchUdrSummaryList(user
				.getUserId());
		assertNotNull(listResp);
		assertEquals(2, listResp.size());
	}

	@Test
	@Transactional
	public void testFetchUdrById() {
		try {
			User user = createUser();
			UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
					user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
			JsonServiceResponse resp = udrService.createUdr(user.getUserId(),
					spec);
			assertEquals(Status.SUCCESS, resp.getStatus());
			assertNotNull(resp.getResponseDetails());
			Long id = Long.valueOf((String) (resp.getResponseDetails().get(0)
					.getAttributeValue()));
			assertNotNull(id);
			UdrSpecification specFetched = udrService.fetchUdr(id);
			assertNotNull(specFetched);
			assertEquals(spec.getSummary().getTitle(), specFetched.getSummary()
					.getTitle());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Test
	@Transactional
	public void testFetchUdrByAuthorTitle() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1)
				.getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		UdrSpecification specFetched = udrService.fetchUdr(user.getUserId(),
				title);
		assertNotNull(specFetched);
		assertEquals(spec.getSummary().getTitle(), specFetched.getSummary()
				.getTitle());
	}

	@Test
	@Transactional
	public void testCreateAndUpdateMetaOnly() throws Exception {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);

		// create Udr Rule
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1)
				.getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		Long id = Long.valueOf(resp
				.findResponseDetailValue(RuleConstants.UDR_ID_ATTRIBUTE_NAME));
		assertNotNull("The saved ID is not null", id);
		UdrRule rule = ruleService.findById(id);
		List<Rule> engineRules = rule.getEngineRules();
		assertNotNull(engineRules);
		assertEquals(3, engineRules.size());
		String drl = engineRules.get(0).getRuleDrl();
		assertFalse(StringUtils.isEmpty(drl));
		String[] criteria = engineRules.get(0).getRuleCriteria();
		assertNotNull(criteria);
		assertEquals(1, criteria.length);
		assertNotNull("Engine Rule has a null Knowledge Base reference",
				engineRules.get(0).getKnowledgeBase());

		// Extract the UDR
		UdrSpecification specFetched = JsonToDomainObjectConverter
				.getJsonFromUdrRule(rule);
		assertNotNull(specFetched);
		specFetched.getSummary().setDescription(RULE_DESCRIPTION2);
		specFetched.setDetails(null);

		udrService.updateUdr(user.getUserId(), specFetched);

		specFetched = udrService.fetchUdr(user.getUserId(), title);
		assertNotNull(specFetched);
		assertEquals(RULE_DESCRIPTION2, specFetched.getSummary()
				.getDescription());
	}

	@Test
	@Transactional
	public void testUpdateAll() {
		User user = createUser();
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1)
				.getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		UdrSpecification specFetched = udrService.fetchUdr(user.getUserId(),
				title);
		UdrSpecification updatedSpec = UdrSpecificationBuilder
				.createSampleSpec2(user.getUserId(), RULE_TITLE1,
						RULE_DESCRIPTION2);
		updatedSpec.setId(specFetched.getId());

		udrService.updateUdr(user.getUserId(), updatedSpec);

		specFetched = udrService.fetchUdr(user.getUserId(), title);
		assertNotNull(specFetched);
		assertEquals(RULE_DESCRIPTION2, specFetched.getSummary()
				.getDescription());

	}

	@Test
	@Transactional
	public void testUpdateByNonAuthor() {
		User user = createUser();
		User user2 = createUser(USERID_2, USER2_FIRST_NAME, USER2_LAST_NAME);
		UdrSpecification spec = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService.createUdr(user.getUserId(), spec);
		assertEquals(Status.SUCCESS, resp.getStatus());
		assertNotNull(resp.getResponseDetails());
		String title = (String) resp.getResponseDetails().get(1)
				.getAttributeValue();
		assertEquals(RULE_TITLE1, title);
		UdrSpecification specFetched = udrService.fetchUdr(user.getUserId(),
				title);
		UdrSpecification updatedSpec = UdrSpecificationBuilder
				.createSampleSpec2(user.getUserId(), RULE_TITLE1,
						RULE_DESCRIPTION2);
		updatedSpec.setId(specFetched.getId());
		udrService.updateUdr(user2.getUserId(), updatedSpec);
		specFetched = udrService.fetchUdr(user.getUserId(), title);
		assertNotNull(specFetched);
		assertEquals(RULE_DESCRIPTION2, specFetched.getSummary()
				.getDescription());
		assertEquals(user.getUserId(), specFetched.getSummary().getAuthor());
	}

	@Test
	@Transactional
	public void testDelete() {
		User user = createUser();
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		UdrSpecification spec2 = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE2, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService
				.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf((String) (resp.getResponseDetails().get(0)
				.getAttributeValue()));

		resp = udrService.createUdr(user.getUserId(), spec2);
		assertEquals(Status.SUCCESS, resp.getStatus());
		List<JsonUdrListElement> listResp = udrService.fetchUdrSummaryList(user
				.getUserId());
		assertNotNull(listResp);
		assertEquals(2, listResp.size());

		udrService.deleteUdr(user.getUserId(), id);

		listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(1, listResp.size());
	}

	@Test
	@Transactional
	public void testDeleteAndRecreate() {
		User user = createUser();
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService
				.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf((String) (resp.getResponseDetails().get(0)
				.getAttributeValue()));

		List<JsonUdrListElement> listResp = udrService.fetchUdrSummaryList(user
				.getUserId());
		assertNotNull(listResp);
		assertEquals(1, listResp.size());

		udrService.deleteUdr(user.getUserId(), id);

		listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(0, listResp.size());

		resp = udrService.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id2 = Long.valueOf((String) (resp.getResponseDetails().get(0)
				.getAttributeValue()));
		assertTrue(id.longValue() != id2.longValue());

		listResp = udrService.fetchUdrSummaryList(user.getUserId());
		assertNotNull(listResp);
		assertEquals(1, listResp.size());
	}

	@Test
	@Transactional
	public void testKnowledgeBaseReferences() {
		User user = createUser(USERID_1, USER1_FIRST_NAME, USER1_LAST_NAME);
		UdrSpecification spec1 = UdrSpecificationBuilder.createSampleSpec(
				user.getUserId(), RULE_TITLE1, RULE_DESCRIPTION1);
		JsonServiceResponse resp = udrService
				.createUdr(user.getUserId(), spec1);
		assertEquals(Status.SUCCESS, resp.getStatus());
		Long id = Long.valueOf((String) (resp.getResponseDetails().get(0)
				.getAttributeValue()));
		UdrRule rule = ruleService.findById(id);
		assertNotNull("Could not fetch saved UDR", rule);
		assertEquals(3, rule.getEngineRules().size());
		assertNotNull(rule.getEngineRules().get(0).getKnowledgeBase());
		assertNotNull(rule.getEngineRules().get(1).getKnowledgeBase());
		assertNotNull(rule.getEngineRules().get(2).getKnowledgeBase());

		// now delete the UDR
		ruleService.delete(id, user.getUserId());
		rule = ruleService.findById(id);
		assertEquals(3, rule.getEngineRules().size());
		assertNull(rule.getEngineRules().get(0).getKnowledgeBase());
		assertNull(rule.getEngineRules().get(1).getKnowledgeBase());
		assertNull(rule.getEngineRules().get(2).getKnowledgeBase());
	}

	private User createUser() {
		return createUser(USERID_1, USER1_FIRST_NAME, USER1_LAST_NAME);
	}

	private User createUser(String userId, String firstName, String lastName) {
		String ROLE_NAME = "user";
		String USER_FNAME = firstName;
		String USER_LASTNAME = lastName;
		String USER_ID = userId;
		Set<RoleData> roles = new HashSet<RoleData>();
		roles.add(new RoleData(1, "ADMIN"));

		UserData usr = new UserData(USER_ID, "password", USER_FNAME,
				USER_LASTNAME, 1, roles, null);
		Role role = new Role();
		role.setRoleDescription(ROLE_NAME);

		User user = userServiceUtil.mapUserEntityFromUserData(userService
				.create(usr));

		return user;
	}

}
