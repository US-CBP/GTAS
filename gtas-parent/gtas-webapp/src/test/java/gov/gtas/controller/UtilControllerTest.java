package gov.gtas.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.gtas.common.UserLocationSetting;
import gov.gtas.common.UserLocationStatus;
import gov.gtas.common.WebAppConfig;
import gov.gtas.controller.config.TestMvcRestServiceWebConfig;
import gov.gtas.services.security.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMvcRestServiceWebConfig.class, WebAppConfig.class })
@WebAppConfiguration
public class UtilControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@Mock
	private UserLocationSetting userLocationSetting;

	private UserLocationStatus userLocationStatus;

	@InjectMocks
	private UtilController utilController;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(utilController)
				.defaultRequest(get("/").contextPath("/gtas").accept(MediaType.APPLICATION_JSON)).build();

		Authentication authentication = Mockito.mock(Authentication.class);
		// Mockito.whens() for your authorization object
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(authentication.getName()).thenReturn("gtasUser");

		userLocationStatus = Mockito.mock(UserLocationStatus.class);
		Mockito.when(userLocationStatus.getPrimaryLocationAirport()).thenReturn("IAD");

	}

	@Test
	public void getFlightDirectionsTest() throws Exception {

		when(userService.isAdminUser("gtasUser")).thenReturn(true);

		mockMvc.perform(get("/gtas/flightdirectionlist")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"));

	}

	@Test
	public void getFlightDirectionsAdminTest() throws Exception {

		when(userService.isAdminUser("gtasUser")).thenReturn(true);

		mockMvc.perform(get("/gtas/flightdirectionlist")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=UTF-8"))
				.andExpect(jsonPath("$.adminUser").value(true))
				.andExpect(jsonPath("$.userLocation").value(IsNull.nullValue()))
				.andExpect(jsonPath("$.flightDirectionList", hasSize(3)));

	}

}
