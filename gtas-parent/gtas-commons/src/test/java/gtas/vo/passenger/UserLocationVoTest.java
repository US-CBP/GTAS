package gtas.vo.passenger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import gov.gtas.vo.passenger.UserLocationVo;

@RunWith(MockitoJUnitRunner.class)
public class UserLocationVoTest {

	@InjectMocks
	UserLocationVo userLocationVo;

	@InjectMocks
	UserLocationVo userLocationVo2;

	@InjectMocks
	UserLocationVo userLocationVo3;

	@Before
	public void before() {
		initMocks(userLocationVo);
		initMocks(userLocationVo2);
		initMocks(userLocationVo3);
		userLocationVo.setPrimaryLocation(true);
		userLocationVo2.setPrimaryLocation(false);

	}

	@Test
	public void setUserLocationAssertTrueTest() {
		assertTrue(userLocationVo.isPrimaryLocation());
	}

	@Test
	public void setUserLocationAssertFalseTest() {
		assertFalse(userLocationVo2.isPrimaryLocation());
	}

	@Test
	public void setUserLocationAssertDefaultTest() {
		assertFalse(userLocationVo3.isPrimaryLocation());
	}

}
