/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import static gov.gtas.constant.GtasSecurityConstants.GTAS_APPLICATION_USERID;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.json.AuditActionData;
import gov.gtas.json.AuditActionTarget;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.services.Loader;
import gov.gtas.services.LoaderStatistics;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Message Loader Scheduler class. Using Spring's Scheduled annotation for
 * scheduling tasks. The class reads configuration values from an external file.
 *
 */
@Component
public class LoaderScheduler {

	private static final Logger logger = LoggerFactory
			.getLogger(LoaderScheduler.class);

	/**
	 * The Enum InputType.
	 */
	public enum InputType {
		TWO_DIRS("two_dirs");
		private final String stringValue;

		private InputType(final String s) {
			stringValue = s;
		}

		@Override
		public String toString() {
			return stringValue;
		}
	}

	@Autowired
	private Loader loader;

	@Autowired
	private ErrorPersistenceService errorPersistenceService;

	@Autowired
	private AuditLogPersistenceService auditLogPersistenceService;

	@Value("${message.dir.origin}")
	private String messageOriginDir;

	@Value("${message.dir.processed}")
	private String messageProcessedDir;

	@Value("${inputType}")
	private String inputType;
	
	@Value("${maxNumofFiles}")
	private int maxNumofFiles;

	/**
	 * Loader Scheduler running on configured schedule
	 */
	@Scheduled(fixedDelayString = "${loader.fixedDelay.in.milliseconds}", initialDelayString = "${loader.initialDelay.in.milliseconds}")
	public void jobScheduling() {
		logger.info("entering jobScheduling()");

		boolean exitStatus = false;
		Path dInputDir = Paths.get(messageOriginDir).normalize();
		File inputDirFile = dInputDir.toFile();
		Path dOutputDir = Paths.get(messageProcessedDir).normalize();
		File outputDirFile = dOutputDir.toFile();

		if (!inputDirFile.exists() || !outputDirFile.exists()) {
			logger.error("directory does not exist.");
			Exception fileNotExist = new RuntimeException(
					"directory does not exist.");
			ErrorDetailInfo errInfo = ErrorHandlerFactory
					.createErrorDetails(fileNotExist);
			errorPersistenceService.create(errInfo);
			exitStatus = true;
		} else if (!inputDirFile.isDirectory() || !outputDirFile.isDirectory()) {
			logger.error("Not a directory: '" + inputDirFile + "'");
			Exception notADirectory = new RuntimeException("Not a directory.");
			ErrorDetailInfo errInfo = ErrorHandlerFactory
					.createErrorDetails(notADirectory);
			errorPersistenceService.create(errInfo);
			exitStatus = true;
		}
		if (exitStatus) {
			Thread.currentThread().interrupt();
		}

		LoaderStatistics stats = new LoaderStatistics();
		if (inputType.equalsIgnoreCase(InputType.TWO_DIRS.name())) {
			processInputAndOutputDirectories(dInputDir, dOutputDir, stats);
		} else {
			logger.warn("No inputType selection.");
		}
		writeAuditLog(stats);
		logger.info("exiting jobScheduling()");
	}

	private void processInputAndOutputDirectories(Path incomingDir,
			Path outgoingDir, LoaderStatistics stats) {
		// No hidden files.
		DirectoryStream.Filter<Path> filter = entry -> {
			File f = entry.toFile();
			return !f.isHidden() && f.isFile();
		};
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(
				incomingDir, filter)) {

			final Iterator<Path> iterator = stream.iterator();
			List<File> files = new ArrayList<>();
			for (int i = 0; iterator.hasNext() && i < maxNumofFiles; i++) {
				files.add(iterator.next().toFile());
			}
			Collections.sort(files,
					LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
			files.stream().forEach(
					f -> {
						processSingleFile(f, stats);
						f.renameTo(new File(outgoingDir.toFile()
								+ File.separator + f.getName()));
					});
			stream.close();
		} catch (IOException ex) {
			logger.error("IOException:" + ex.getMessage(), ex);
			ErrorDetailInfo errInfo = ErrorHandlerFactory
					.createErrorDetails(ex);
			errorPersistenceService.create(errInfo);
		}
	}

	private void processSingleFile(File f, LoaderStatistics stats) {
		logger.info(String.format("Processing %s", f.getAbsolutePath()));
		int[] result = loader.processMessage(f);
		// update loader statistics.
		if (result != null) {
			stats.incrementNumFilesProcessed();
			stats.incrementNumMessagesProcessed(result[0]);
			stats.incrementNumMessagesFailed(result[1]);
		} else {
			stats.incrementNumFilesAborted();
		}
	}

	/**
	 * Writes the audit log with run statistics.
	 * 
	 * @param stats
	 *            the statistics bean.
	 */
	private void writeAuditLog(LoaderStatistics stats) {
		AuditActionTarget target = new AuditActionTarget(
				AuditActionType.LOADER_RUN, "GTAS Message Loader", null);
		AuditActionData actionData = new AuditActionData();
		actionData.addProperty("totalFilesProcessed",
				String.valueOf(stats.getNumFilesProcessed()));
		actionData.addProperty("totalFilesAborted",
				String.valueOf(stats.getNumFilesAborted()));
		actionData.addProperty("totalMessagesProcessed",
				String.valueOf(stats.getNumMessagesProcessed()));
		actionData.addProperty("totalMessagesInError",
				String.valueOf(stats.getNumMessagesFailed()));

		String message = "Message Loader run on " + new Date();
		auditLogPersistenceService.create(AuditActionType.LOADER_RUN, target,
				actionData, message, GTAS_APPLICATION_USERID);
	}
}
