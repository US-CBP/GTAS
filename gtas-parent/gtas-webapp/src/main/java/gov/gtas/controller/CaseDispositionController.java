/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.model.Role;
import gov.gtas.model.User;
import gov.gtas.services.dto.CaseCommentRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.constants.Constants;
import gov.gtas.model.Attachment;
import gov.gtas.model.Case;
import gov.gtas.model.lookup.CaseDispositionStatus;
import gov.gtas.model.lookup.HitDispositionStatus;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.repository.AttachmentRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.CaseDispositionServiceImpl;
import gov.gtas.services.RuleCatService;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.services.security.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import gov.gtas.vo.passenger.CaseVo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@RestController
public class CaseDispositionController {

	@Autowired
	private CaseDispositionService caseDispositionService;

	@Autowired
	private RuleCatService ruleCatService;

	@Autowired
	private UserService userService;

	@Autowired
	private AttachmentRepository attachmentRepo;

	private final Logger logger = LoggerFactory.getLogger(CaseDispositionController.class);

	@RequestMapping(value = "/getAllCaseDispositions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody CasePageDto getAll(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {

		hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		boolean isAdmin = userService.isAdminUser(userId);

		if (!isAdmin) {
			String userLocation = (String) hsr.getSession().getAttribute(Constants.USER_PRIMARY_LOCATION);
			request.setUserLocation(userLocation);
		}

		CasePageDto casePageDto = caseDispositionService.findAll(request);
		return casePageDto;
	}

	// getOneHistDisp
	@RequestMapping(method = RequestMethod.POST, value = "/getOneHistDisp", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody CasePageDto getOneHistDisp(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		CasePageDto res;
		boolean treatAsOneDay = userService.treatAsOneDay(userId);
		if (treatAsOneDay) {
			res = caseDispositionService.caseWithoutHitDispositions(request);
		} else {
			res = caseDispositionService.findHitsDispositionByCriteria(request);
		}
		return res;
	}

	// getHistDispComments
	@RequestMapping(method = RequestMethod.GET, value = "/getHistDispComments")
	public Map<String, Object> getHistDispComments(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) throws ParseException {
		HashMap _tempMap = new HashMap();

		return _tempMap;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getCurrentServerTime")
	@ResponseBody
	public long getCurrentServerTime() {
		return new Date().getTime();
	}

	@RequestMapping(value = "/getdownload/{id}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getDownloadData(@PathVariable long id) {

		ResponseEntity returnEntity = null;
		try {
			Attachment attachment = attachmentRepo.findByIntegerId((int) id);
			byte[] fileData = attachment.getContent().getBytes(1, (int) attachment.getContent().length());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("charset", "utf-8");
			responseHeaders.setContentType(MediaType.valueOf(attachment.getContentType()));
			responseHeaders.setContentLength(attachment.getContent().length());
			responseHeaders.set("Content-disposition", "attachment; filename=" + attachment.getFilename());
			returnEntity = new ResponseEntity<byte[]>(fileData, responseHeaders, HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("Error retrieving file to download : " + ex.getMessage());

		}

		return returnEntity;
	}

	// getRuleCats
	@RequestMapping(method = RequestMethod.GET, value = "/getRuleCats")
	public List<RuleCat> getRuleCats() throws Exception {

		List<RuleCat> _tempRuleCatList = new ArrayList<RuleCat>();
		Iterable<RuleCat> _tempIterable = ruleCatService.findAll();
		if (_tempIterable != null) {
			_tempRuleCatList = StreamSupport.stream(_tempIterable.spliterator(), false).collect(Collectors.toList());
		}
		for (RuleCat _tempRuleCat : _tempRuleCatList) {
			// _tempRuleCat.setHitsDispositions(null);
		}
		return _tempRuleCatList;
	}

	// updateHistDisp
	@RequestMapping(method = RequestMethod.POST, value = "/updateHistDisp")
	public @ResponseBody boolean updateHistDisp(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
		Case aCase = new Case();
		boolean isUpdateSuccessful = false;
		try {

			logger.info("Updating a case to disposition: " + request.getCaseDisposition() + " and case status: "
					+ request.getStatus());

			aCase = caseDispositionService.addCaseComments(request.getCaseId(), request.getHitId(),
					request.getCaseComments(), request.getStatus(), request.getValidHit(), request.getMultipartFile(),
					GtasSecurityUtils.fetchLoggedInUserId(), request.getCaseDisposition());
			isUpdateSuccessful = true;

		} catch (Exception ex) {
			logger.error("Error updating histDisp!", ex);
		}
		return isUpdateSuccessful;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/addCaseComment")
	public @ResponseBody boolean addCaseComment(@RequestBody CaseCommentRequestDto caseCommentRequestDto) {
		boolean isUpdateSuccessful = false;
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		caseCommentRequestDto.setUser(userId);
		try {
			if ("MISSED".equalsIgnoreCase(caseCommentRequestDto.getCaseStatus())
					&& caseCommentRequestDto.getComment() == null) {
				throw new RuntimeException("Updating to missed MUST have a comment");
			}
			logger.info("Adding case comment");
			caseDispositionService.addGeneralCaseComment(caseCommentRequestDto);
			isUpdateSuccessful = true;
		} catch (Exception ex) {
			logger.error("Error updating general case comment!", ex);
		}
		return isUpdateSuccessful;
	}

	// updateHistDispAttachments
	@RequestMapping(method = RequestMethod.POST, value = "/updateHistDispAttachments")
	public @ResponseBody Case updateHistDispAttachments(@RequestParam("file") MultipartFile file,
			@RequestParam("flightId") String flightId, @RequestParam("paxId") String paxId,
			@RequestParam("caseId") Long caseId, @RequestParam("hitId") String hitId,
			@RequestParam("caseComments") String caseComments, @RequestParam("status") String status,
			@RequestParam("validHit") String validHit, @RequestParam("caseDisposition") String caseDisposition) {
		Case aCase = new Case();
		try {
			MultipartFile multipartFile = file;

			aCase = caseDispositionService.addCaseComments(caseId, Long.parseLong(hitId), caseComments, status,
					validHit, multipartFile, GtasSecurityUtils.fetchLoggedInUserId(), caseDisposition);
		} catch (Exception ex) {
			logger.error("Error in histDispAttachements!", ex);
		}
		// TODO: replace the return type of the method with a DTO object to avoid self
		// referencing loop while converting to JSON by spring
		Case newCase = new Case();
		newCase.setId(aCase.getId());
		newCase.setHitsDispositions(aCase.getHitsDispositions());
		return newCase;
	}

	// createManualCaseAttachments
	// @RequestMapping(method = RequestMethod.POST, value =
	// "/createManualCaseAttachments")
	// public
	// @ResponseBody
	// Case createManualCaseAttachments(@RequestParam("file") MultipartFile file,
	// @RequestParam("flightId") String flightId, @RequestParam("paxId") String
	// paxId,
	// @RequestParam("hitId") String hitId, @RequestParam("caseComments")String
	// caseComments,
	// @RequestParam("status")String status,
	// @RequestParam("validHit")String validHit) {
	// Case aCase = new Case();
	// try {
	// MultipartFile multipartFile = file;
	//
	// aCase = caseDispositionService.addCaseComments(Long.parseLong(flightId),
	// Long.parseLong(paxId),
	// Long.parseLong(hitId), caseComments,
	// status, validHit,
	// multipartFile, GtasSecurityUtils.fetchLoggedInUserId(), null);
	// } catch (Exception ex) {
	// logger.error("Error in create manual case attachments", ex);
	// }
	// return aCase;
	// }

	// createManualCase
	@RequestMapping(method = RequestMethod.POST, value = "/createManualCase")
	public @ResponseBody Case createManualCase(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
		Case aCase = new Case();
		try {

			aCase = caseDispositionService.createManualCase(request.getFlightId(), request.getPaxId(),
					request.getRuleCatId(), request.getCaseComments(), GtasSecurityUtils.fetchLoggedInUserId());
		} catch (Exception ex) {
			logger.error("Error in create manual case", ex);
		}
		return aCase;
	}

	// updateCase
	@RequestMapping(method = RequestMethod.POST, value = "/updateCase")
	public Map<String, Object> updateCase(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) throws ParseException {
		HashMap _tempMap = new HashMap();

		return _tempMap;
	}

	@RequestMapping(value = "/hitdispositionstatuses", method = RequestMethod.GET)
	public @ResponseBody List<HitDispositionStatus> getHitDispositionStatuses() {
		return caseDispositionService.getHitDispositionStatuses();
	}

	@RequestMapping(value = "/casedisposition", method = RequestMethod.GET)
	public @ResponseBody List<CaseDispositionStatus> getCaseDispositionStatuses() {
		return caseDispositionService.getCaseDispositionStatuses();
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passenger/caseHistory/{paxId}", method = RequestMethod.GET)
	public List<CaseVo> getPassengerCaseHistory(@PathVariable(value = "paxId") Long paxId) {

		List<CaseVo> vos = new ArrayList<CaseVo>();

		List<Case> cases = caseDispositionService.getCaseHistoryByPaxId(paxId);

		for (Case _case : cases) {
			CaseVo vo = new CaseVo();
			_case.getFlight().setPnrs(null); // TODO: need to cherry-pick the fields we need to copy to DTO, failed to
												// serialize the lazy loaded entities
			_case.setHitsDispositions(null);
			CaseDispositionServiceImpl.copyIgnoringNullValues(_case, vo);
			vos.add(vo);
		}

		return vos;
	}

}
