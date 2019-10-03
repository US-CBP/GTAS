/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

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

import gov.gtas.model.Attachment;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.AttachmentRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.HitCategoryService;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import gov.gtas.services.security.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
	private HitCategoryService ruleCatService;

	@Autowired
	private UserService userService;

	@Autowired
	private AttachmentRepository attachmentRepo;

	private final Logger logger = LoggerFactory.getLogger(CaseDispositionController.class);

	@RequestMapping(value = "/getAllCaseDispositions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody CasePageDto getAll(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {

		return new CasePageDto(new ArrayList<>(),0L);
	}

	// getOneHistDisp
	@RequestMapping(method = RequestMethod.POST, value = "/getOneHistDisp", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody CasePageDto getOneHistDisp(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		return new CasePageDto(new ArrayList<>(),0L);
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
	public List<HitCategory> getRuleCats() throws Exception {

		List<HitCategory> _tempRuleCatList = new ArrayList<HitCategory>();
		Iterable<HitCategory> _tempIterable = ruleCatService.findAll();
		if (_tempIterable != null) {
			_tempRuleCatList = StreamSupport.stream(_tempIterable.spliterator(), false).collect(Collectors.toList());
		}
		for (HitCategory _tempRuleCat : _tempRuleCatList) {
			// _tempRuleCat.setHitsDispositions(null);
		}
		return _tempRuleCatList;
	}

	// updateHistDisp
	@RequestMapping(method = RequestMethod.POST, value = "/updateHistDisp")
	public @ResponseBody boolean updateHistDisp(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
		return true;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/addCaseComment")
	public @ResponseBody boolean addCaseComment(@RequestBody CaseCommentRequestDto caseCommentRequestDto) {
		return true;
	}

	// updateHistDispAttachments
	@RequestMapping(method = RequestMethod.POST, value = "/updateHistDispAttachments")
	public void updateHistDispAttachments(@RequestParam("file") MultipartFile file,
			@RequestParam("flightId") String flightId, @RequestParam("paxId") String paxId,
			@RequestParam("caseId") Long caseId, @RequestParam("hitId") String hitId,
			@RequestParam("caseComments") String caseComments, @RequestParam("status") String status,
			@RequestParam("validHit") String validHit, @RequestParam("caseDisposition") String caseDisposition) {
	}

	@RequestMapping(method = RequestMethod.POST, value = "/createManualCase")
	public @ResponseBody void createManualCase(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
	}

	// updateCase
	@RequestMapping(method = RequestMethod.POST, value = "/updateCase")
	public Map<String, Object> updateCase(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) throws ParseException {
		HashMap _tempMap = new HashMap();

		return _tempMap;
	}

	@RequestMapping(value = "/hitdispositionstatuses", method = RequestMethod.GET)
	public @ResponseBody List<Object> getHitDispositionStatuses() {
		return new ArrayList<>();
	}

	@RequestMapping(value = "/casedisposition", method = RequestMethod.GET)
	public @ResponseBody List<Object> getCaseDispositionStatuses() {
		return new ArrayList<>();
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passenger/caseHistory/{paxId}", method = RequestMethod.GET)
	public List<CaseVo> getPassengerCaseHistory(@PathVariable(value = "paxId") Long paxId) {
		return new ArrayList<>();
	}

}
