/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.model.dto.ViewUpdateDTo;
import gov.gtas.services.PriorityVettingListService;
import gov.gtas.services.dto.CaseCommentRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import gov.gtas.model.Attachment;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.AttachmentRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.HitCategoryService;
import gov.gtas.services.dto.PriorityVettingListDTO;
import gov.gtas.services.dto.PriorityVettingListRequest;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@RestController
public class PriorityVettingListController {

	private final HitCategoryService hitCategoryService;

	private final PriorityVettingListService priorityVettingListService;

	private final AttachmentRepository attachmentRepo;

	private final Logger logger = LoggerFactory.getLogger(PriorityVettingListController.class);
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PriorityVettingListRequest.DATE_FORMAT);
	private final ObjectMapper objectMapper = new ObjectMapper().setDateFormat(simpleDateFormat);

	public PriorityVettingListController(HitCategoryService hitCategoryService,
			PriorityVettingListService priorityVettingListService, AttachmentRepository attachmentRepo) {
		this.hitCategoryService = hitCategoryService;
		this.priorityVettingListService = priorityVettingListService;
		this.attachmentRepo = attachmentRepo;
	}

	@RequestMapping(value = "/hits", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PriorityVettingListDTO getAll(@RequestParam("requestDto") String requestDto)
			throws IOException {
		final PriorityVettingListRequest request = objectMapper.readValue(requestDto, PriorityVettingListRequest.class);
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		return priorityVettingListService.generateDtoFromRequest(request, userId);
	}

	@RequestMapping(value = "/hits", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@RequestBody ViewUpdateDTo requestDto) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		priorityVettingListService.update(requestDto, userId);
	}

	// getOneHistDisp
	@RequestMapping(method = RequestMethod.POST, value = "/getOneHistDisp", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PriorityVettingListDTO getOneHistDisp(@RequestBody PriorityVettingListRequest request,
			HttpServletRequest hsr) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		return new PriorityVettingListDTO(new ArrayList<>(), 0L);
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
		Iterable<HitCategory> _tempIterable = hitCategoryService.findAll();
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
	public @ResponseBody boolean updateHistDisp(@RequestBody PriorityVettingListRequest request,
			HttpServletRequest hsr) {
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
	public @ResponseBody void createManualCase(@RequestBody PriorityVettingListRequest request,
			HttpServletRequest hsr) {
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

}
