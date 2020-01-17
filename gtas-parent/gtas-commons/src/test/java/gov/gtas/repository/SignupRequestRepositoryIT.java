package gov.gtas.repository;

import static org.junit.Assert.assertEquals;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.enumtype.SignupRequestStatus;
import gov.gtas.model.PhysicalLocation;
import gov.gtas.model.SignupRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@Transactional
@Rollback
public class SignupRequestRepositoryIT {

	@Autowired
	private SignupRequestRepository signupRequestRepository;

	private SignupRequest signupRequest;

	@Before
	public void setup() {
		this.signupRequest = new SignupRequest();
		PhysicalLocation location = new PhysicalLocation();
		location.setActive(true);
		location.setId(1L);
		location.setName("Test");
		signupRequest.setPhysicalLocation(location);
		signupRequest.setPhysicalLocationId(1L);
		signupRequest.setUsername("test");
		signupRequest.setEmail("test@gmail.com");
	}

	@Test
	public void testExistsSignupRequestByEmailOrUsername() {

		this.signupRequest = this.signupRequestRepository.save(this.signupRequest);
		//
		assertEquals(this.signupRequestRepository.existsSignupRequestByEmailOrUsername(this.signupRequest.getEmail(),
				this.signupRequest.getUsername()), true);
	}

	@Test
	public void testFindNewSignupRequests() {

		this.signupRequest = this.signupRequestRepository.save(this.signupRequest);
		//
		assertEquals(this.signupRequestRepository.findNewSignupRequests(SignupRequestStatus.NEW).size(), 1);
	}
}
