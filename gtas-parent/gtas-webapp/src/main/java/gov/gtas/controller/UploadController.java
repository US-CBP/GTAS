/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.Attachment;
import gov.gtas.model.Passenger;
import gov.gtas.repository.AttachmentRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.vo.passenger.AttachmentVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_VIEW_PASSENGER;

@Controller
public class UploadController {
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	@Autowired
	private PassengerRepository passengerRepo;

	@Autowired
	private AttachmentRepository attRepo;

	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/attachments")
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public @ResponseBody JsonServiceResponse uploadAttachments(@RequestParam("file") Set<MultipartFile> files,
			@RequestParam("paxId") String paxId, @RequestParam("descriptions") List<String> descriptions)
			throws IOException, SQLException {
		String failureFileNames = "";
		int failureCount = 0;

		if (files.isEmpty()) {
			logger.info("empty files!");
			return new JsonServiceResponse(Status.FAILURE, "Failed to upload attachment: No File was found");
		}
		int descriptionArrayCounter = 0;
		for (MultipartFile file:files) {
			try {
				storeAttachmentToPassenger(file, descriptions.get(descriptionArrayCounter), paxId);
			} catch (SQLException e) {
				descriptionArrayCounter++;
				failureFileNames += ", "+file.getOriginalFilename();
				failureCount++;
			}
			descriptionArrayCounter++;
		}
		if(failureCount == 0) {
			return new JsonServiceResponse(Status.SUCCESS, "Successfully uploaded all files");
		} else {
			return new JsonServiceResponse(Status.FAILURE, "There were files that failed to upload! count: "+failureCount+
					" Failed file names: "+failureFileNames);
		}
	}

	private void storeAttachmentToPassenger(MultipartFile file, String desc, String paxId)
			throws IOException, SQLException {
		if (!file.isEmpty()) {
			// Build attachment to be added to pax
			Attachment attachment = new Attachment();
			attachment.setContentType(file.getContentType());
			attachment.setDescription(desc);
			attachment.setFilename(file.getOriginalFilename());
			attachment.setName(file.getName());
			byte[] bytes = file.getBytes();
			Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
			attachment.setContent(blob);

			// Grab pax to add attachment to it
			Passenger pax = passengerRepo.findOne(Long.parseLong(paxId));
			/*
			 * if(pax.getAttachments() != null){ pax.getAttachments().add(attachment); }
			 * else{ Set<Attachment> tmpSet = new HashSet<Attachment>();
			 * tmpSet.add(attachment); pax.setAttachments(tmpSet); }
			 * passengerRepo.save(pax);
			 */
			attachment.setPassenger(pax);
			attRepo.save(attachment);
		}
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/attachments", method = RequestMethod.GET)
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public @ResponseBody List<AttachmentVo> getAttachments(@RequestParam String paxId)
			throws SQLException {
		List<Attachment> returnSet = attRepo.findAllAttachmentsByPassengerId(Long.parseLong(paxId));

		List<AttachmentVo> attVoList = new ArrayList<AttachmentVo>();
		for (Attachment a : returnSet) {
			AttachmentVo attVo = new AttachmentVo();
			// Turn blob into byte[], as input stream is not serializable
			attVo.setContent(a.getContent().getBytes(1, (int) a.getContent().length()));
			attVo.setId(a.getId());
			attVo.setContentType(a.getContentType());
			attVo.setDescription(a.getDescription());
			attVo.setFilename(a.getFilename());
			// Drop blob from being held in memory after each set
			a.getContent().free();
			// Add to attVoList to be returned to front-end
			attVoList.add(attVo);
		}
		return attVoList;
	}

	@RequestMapping(value = "/attachment", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public @ResponseBody ResponseEntity<Resource> downloadAttachment(@RequestParam Long attachmentId)
			throws SQLException, IOException {
		Optional<Attachment> a1 = attRepo.findById(attachmentId);
		if(a1.isPresent()) {
			Attachment a = a1.get();
			AttachmentVo attVo = new AttachmentVo();
			// Turn blob into byte[], as input stream is not serializable
			attVo.setContent(a.getContent().getBytes(1, (int) a.getContent().length()));
			attVo.setId(a.getId());
			attVo.setContentType(a.getContentType());
			attVo.setFilename(a.getFilename());
			// Drop blob from being held in memory after each set
			a.getContent().free();

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(attVo.getContentType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attVo.getFilename() + "\"")
					.body(new ByteArrayResource(attVo.getContent()));
		}
		else return null;
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/attachments/{id}", method = RequestMethod.DELETE)
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public @ResponseBody JsonServiceResponse deleteAttachment(@PathVariable String id) {
		JsonServiceResponse response;

		// Insure id is not null/empty/ or less than 1
		if (id == null || id.isEmpty() || Long.parseLong(id) < 1) {
			return new JsonServiceResponse(Status.FAILURE, "Invalid attachment id");
		}
		// Attempt removal
		attRepo.deleteById(Long.parseLong(id));

		return new JsonServiceResponse(Status.SUCCESS, "Successfully deleted attachment with id: " + id);
	}

	//This method is separate to get pure metadata
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/attachmentsmeta", method = RequestMethod.GET)
	@PreAuthorize(PRIVILEGES_ADMIN_AND_VIEW_PASSENGER)
	public @ResponseBody List<AttachmentVo> getAttachmentsMetaData(@RequestParam String paxId)
			throws SQLException {
		List<Attachment> returnSet = attRepo.findAllAttachmentsByPassengerId(Long.parseLong(paxId));

		List<AttachmentVo> attVoList = new ArrayList<AttachmentVo>();
		for (Attachment a : returnSet) {
			AttachmentVo attVo = new AttachmentVo();
			attVo.setId(a.getId());
			attVo.setContentType(a.getContentType());
			attVo.setDescription(a.getDescription());
			attVo.setFilename(a.getFilename());
			// Add to attVoList to be returned to front-end
			attVoList.add(attVo);
		}
		return attVoList;
	}

}
