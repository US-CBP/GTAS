package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import gov.gtas.enumtype.SignupRequestStatus;
import gov.gtas.model.SignupLocation;
import gov.gtas.model.SignupRequest;
import gov.gtas.repository.SignupRequestRepository;
import gov.gtas.services.dto.SignupRequestDTO;


@RunWith(MockitoJUnitRunner.class)
public class SignupRequestServiceTest {
	@InjectMocks
	SignupRequestServiceImpl signupRequestService;
	@Mock
	SignupRequestRepository signupRequestRepository;

	@Before
	public void before() {
		 MockitoAnnotations.initMocks(signupRequestRepository);
		 ReflectionTestUtils.setField(signupRequestService, "signupRequestRepository", signupRequestRepository);
		
	}

	@Test
	public void testSearch() {
		Map<String, Object> params = new HashMap<>();
		params.put("status", "NEW");
		
		when(signupRequestRepository.findAll(any())).thenReturn(getSignupRequests());
		
		List<SignupRequestDTO> requests = signupRequestService.search(params);
		
		assertNotNull(requests.get(0));
		assertEquals("test", requests.get(0).getUsername());

	}
	
	@Test
	public void testfindById() {
		Optional<SignupRequest> request = Optional.of(getSignupRequests().get(0));
		
		when(signupRequestRepository.findById(1L)).thenReturn(request);
		
		assertNotNull(signupRequestService.findById(1L));
		assertEquals("test", signupRequestService.findById(1L).getUsername());
	}
	
	
	private List<SignupRequest> getSignupRequests() {
		List<SignupRequest> requests = new ArrayList<>();
		SignupRequest request = new SignupRequest();
		SignupLocation location = new SignupLocation();
		location.setName("ADD");
		location.setId(1L);
		
		request.setUsername("test");
		request.setFirstName("TestF");
		request.setLastName("TestL");
		request.setEmail("test@test.test");
		request.setSignupLocationId(1L);
		request.setSupervisor("Testsuper");
		request.setStatus(SignupRequestStatus.NEW);
		request.setSignupLocation(location);
		
		requests.add(request);
		
		return requests;
		
		
	}

}
