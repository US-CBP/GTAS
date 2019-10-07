/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.Passenger;
import gov.gtas.model.UserGroup;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.dto.PriorityVettingListDTO;
import gov.gtas.services.dto.PriorityVettingListRequest;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.CountDownVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class PriorityVettingListServiceImpl implements PriorityVettingListService {

	private final UserService userService;

	private final PassengerRepository passengerRepository;

	public PriorityVettingListServiceImpl(UserService userService, PassengerRepository passengerRepository) {
		this.userService = userService;
		this.passengerRepository = passengerRepository;
	}

	@Override
	@Transactional
	public PriorityVettingListDTO generateDtoFromRequest(PriorityVettingListRequest request, String userId) {
		Set<UserGroup> userGroups = userService.fetchUserGroups(userId);

		Pair<Long, List<Passenger>> immutablePair = passengerRepository.priorityVettingListQuery(request, userGroups);

		long count = immutablePair.getLeft();
		List<Passenger> passengerList = immutablePair.getRight();
		List<CaseVo> caseVOS = new ArrayList<>();
		for (Passenger passenger : passengerList) {
			CaseVo caseVo = new CaseVo();
			Date countDownTo = passenger.getFlight().getFlightCountDownView().getCountDownTimer();
			CountDownCalculator countDownCalculator = new CountDownCalculator();
			CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(countDownTo, 30, 30);
			caseVo.setCountdownTime(countDownTo);
			caseVo.setCountDownTimeDisplay(countDownVo.getCountDownTimer());
			caseVo.setCurrentTime(new Date());
			caseVo.setDob(passenger.getPassengerDetails().getDob());
			caseVo.setDocument("doc");
			caseVo.setFirstName(passenger.getPassengerDetails().getFirstName());
			caseVo.setLastName(passenger.getPassengerDetails().getLastName());
			caseVo.setPaxName(passenger.getPassengerDetails().getFirstName() + " "
					+ passenger.getPassengerDetails().getLastName());
			caseVo.setFlightId(passenger.getFlight().getId());
			caseVo.setPaxId(passenger.getId());
			caseVOS.add(caseVo);
		}
		return new PriorityVettingListDTO(caseVOS, count);
	}
}
