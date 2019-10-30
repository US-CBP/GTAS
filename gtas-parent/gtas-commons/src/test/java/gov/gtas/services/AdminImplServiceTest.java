/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.services.dto.ApplicationStatisticsDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AdminImplServiceTest {

	@Mock
	MessageService messageService;

	@Mock
	HitDetailRepository hitDetailRepository;

	@InjectMocks
	AdminServiceImpl adminService;

	@Before
	public void before() {
		initMocks(messageService);
		initMocks(hitDetailRepository);

		// Make 500 messages with 1 passenger each.
		List<Message> messageList = new ArrayList<>();
		for (long i = 1; i <= 500; i++) {
			Pnr pnr = new Pnr();
			MessageStatus messageStatus = new MessageStatus(i, MessageStatusEnum.ANALYZED);
			pnr.setId(i);
			pnr.setPassengerCount(1);
			pnr.setStatus(messageStatus);
			messageList.add(pnr);
		}
		Mockito.when(messageService.getMostRecent500Messages()).thenReturn(messageList);
		Mockito.when(hitDetailRepository.findFirstByOrderByIdDesc()).thenReturn(null);
	}

	@Test
	public void happyPath() {
		adminService.createApplicationStatisticsDto();
	}

	@Test
	public void passengerCountTest() {
		ApplicationStatisticsDTO asDTO = adminService.createApplicationStatisticsDto();
		Assert.assertEquals(asDTO.getPassengerCount(), 500);
	}

	@Test
	public void messageAnalyzedTest() {
		ApplicationStatisticsDTO asDTO = adminService.createApplicationStatisticsDto();
		Assert.assertEquals(asDTO.getAnalyzedCount(), 500);
	}

	@Test
	public void messageAnalyzedNegativeTest() {
		// Make 500 messages with 1 passenger each.
		List<Message> messageList = new ArrayList<>();
		for (long i = 1; i <= 500; i++) {
			Pnr pnr = new Pnr();
			MessageStatus messageStatus = new MessageStatus(i, MessageStatusEnum.FAILED_ANALYZING);
			pnr.setId(i);
			pnr.setPassengerCount(1);
			pnr.setStatus(messageStatus);
			messageList.add(pnr);
		}
		Mockito.when(messageService.getMostRecent500Messages()).thenReturn(messageList);
		ApplicationStatisticsDTO asDTO = adminService.createApplicationStatisticsDto();
		Assert.assertNotEquals(asDTO.getAnalyzedCount(), 500);
	}

}
