package gov.gtas.controller;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.services.VersionService;

@RunWith(MockitoJUnitRunner.class)
public class VersionControllerTest {

	@Mock
	VersionService versionService;

	@InjectMocks
	VersionController versionController;

	@Before
	public void before() {
		initMocks(this);
		Mockito.when(versionService.getApplicationVersionNumber()).thenReturn("1.6");
	}

	@Test
	public void getApplicationVersionNumberTest() {

		String applicationVersionNumber = versionController.getApplicationVersionNumber();
		Assert.assertEquals(applicationVersionNumber, "1.6");
	}

	@Test
	public void getApplicationVersionNumberNotEqualTest() {

		String applicationVersionNumber = versionController.getApplicationVersionNumber();
		Assert.assertNotEquals(applicationVersionNumber, "1.7");
	}

}
