/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler;

import gov.gtas.job.localFileIntake.InboundQMessageSender;
import gov.gtas.parsers.util.FileUtils;
import gov.gtas.services.LoaderException;
import gov.gtas.job.scheduler.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocalFileIntakeScheduler {

	private static final Logger logger = LoggerFactory.getLogger(LocalFileIntakeScheduler.class);

	@Value("${message.dir.origin}")
	private String messageOriginDir;

	@Value("${message.dir.working}")
	private String messageWorkingDir;

	@Value("${message.dir.error}")
	private String messageErrorDir;

	@Value("${outbound.loader.jms.queue}")
	private String outboundLoaderQueue;

	private final InboundQMessageSender sender;

	public LocalFileIntakeScheduler(InboundQMessageSender sender) {
		this.sender = sender;
	}

	@Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
	public void jobScheduling() {
		processAndQFiles();
	}

	private void processAndQFiles() {
		Path dInputDir = Paths.get(messageOriginDir).normalize();

		File folder = dInputDir.toFile();
		File[] files = folder.listFiles();
		if (files == null) {
			return;
		}

		List<File> fileList = new ArrayList<>(Arrays.asList(files));
		fileList = fileList.stream().filter(f -> !f.isHidden() && f.isFile()).collect(Collectors.toList());
		if (fileList.isEmpty()) {
			return;
		}

		for (File file : fileList) {
			pushToInboundQueueCatchExceptions(file);
		}
	}

	private void pushToInboundQueueCatchExceptions(File file) {
		try {
			pushToInboundQueue(file);
		} catch (LoaderException | IOException e) {
			logger.error("Error pushing file to outbound queue", e);
		}
	}

	private void pushToInboundQueue(File f) throws LoaderException, IOException {
		String filePath = f.getAbsolutePath();
		if (exceedsMaxSize(f)) {
			throw new LoaderException("exceeds max file size");
		}
		byte[] raw = FileUtils.readSmallFile(filePath);
		String tmp = new String(raw, StandardCharsets.UTF_8);
		sender.sendFileContent(outboundLoaderQueue, tmp, f.getName());
		boolean fileDeleted = f.delete();
		if (!fileDeleted) {
			logger.error("file deletion failed");
		}
	}

	private boolean exceedsMaxSize(File f) {
		final long MAX_SIZE = 4294967295L; // raw column can accommodate 4294967295 bytes
		double numBytes = f.length();
		return numBytes > MAX_SIZE;
	}

}
