/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.controller;

import gov.gtas.constants.Constants;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.FlightSearchVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class UtilControllerTest {

    private static final String EXPECTED_AIRPORT = "foo";
    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpSession httpSession;

    @Mock
    UserService userService;

    @InjectMocks
    UtilController utilController;

    @Before
    public void before(){
        initMocks(this);
        Mockito.when(userService.isAdminUser("admin")).thenReturn(true);
        Mockito.when(userService.isAdminUser("notAdmin")).thenReturn(false);
        Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);
        Mockito.when(httpSession.getAttribute(Constants.USER_PRIMARY_LOCATION)).thenReturn(EXPECTED_AIRPORT);
    }

    @Test
    public void adminPath() {
        FlightSearchVo flightSearchVo = utilController.getFlightSearchVo(httpServletRequest, "admin");
        List<FlightSearchVo.FlightDirectionVo> flightDirectionVos = flightSearchVo.getFlightDirectionList();
        Set<String> actual = flightDirectionVos.stream().map(FlightSearchVo.FlightDirectionVo::getCode).collect(Collectors.toCollection(HashSet::new));
        Set<String> expected = new HashSet<>();
        expected.add("A");
        expected.add("I");
        expected.add("O");
        Assert.assertTrue(flightSearchVo.isAdminUser());
        Assert.assertEquals(actual, expected);
        Assert.assertNull(flightSearchVo.getUserLocation());
    }

    @Test
    public void nonAdminPath() {
        FlightSearchVo flightSearchVo = utilController.getFlightSearchVo(httpServletRequest, "notAdmin");
        List<FlightSearchVo.FlightDirectionVo> flightDirectionVos = flightSearchVo.getFlightDirectionList();
        Set<String> actual = flightDirectionVos.stream().map(FlightSearchVo.FlightDirectionVo::getCode).collect(Collectors.toCollection(HashSet::new));
        Set<String> expected = new HashSet<>();
        expected.add("I");
        expected.add("O");
        Assert.assertFalse(flightSearchVo.isAdminUser());
        Assert.assertEquals(actual, expected);
        Assert.assertEquals(EXPECTED_AIRPORT, flightSearchVo.getUserLocation());
    }

}
