/* All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).*/
package gov.gtas.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.gtas.model.Case;
import gov.gtas.services.CaseDispositionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.model.MessageStatus;
import gov.gtas.vo.ErrorMessageVo;

@RestController
public class NotificationController {

	private final CaseDispositionService caseDispositionService;

	public NotificationController(CaseDispositionService caseDispositionService) {
		this.caseDispositionService = caseDispositionService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/errorMessage")
	public List<ErrorMessageVo> getErrorMessage(){
		Set<MessageStatus> errorStatuses = new HashSet<>();
		return 	null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/hitCount")
	public Integer getHitCount(){
		Set<Case> caseList = caseDispositionService.getOpenCasesWithTimeLeft();
		return caseList.size();
	}
}