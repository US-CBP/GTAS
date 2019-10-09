package gov.gtas.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class VersionServiceTest {

	@InjectMocks
	VersionServiceImpl versionService;

	@Before
	public void before() {

		ReflectionTestUtils.setField(versionService, "applicationVersionNumber", "1.6");
	}

	@Test
	public void getApplicationVersionNumberTest() {
		versionService.getApplicationVersionNumber();
		Assert.assertEquals(versionService.getApplicationVersionNumber(), "1.6");
	}

	@Test
	public void getApplicationVersionNumberTest2() {
		versionService.getApplicationVersionNumber();
		Assert.assertNotEquals(versionService.getApplicationVersionNumber(), "1.7");
	}

}
