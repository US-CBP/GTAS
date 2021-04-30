/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job;

import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.services.ApisMessageService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.services.PnrMessageService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "loader", name = "enabled")
public class FileReader {

	private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

	@Autowired
	ApisMessageService apisService;

	@Autowired
	PnrMessageService pnrService;

	@Autowired
	ErrorPersistenceService errorService;

	// disable for now
	// @Scheduled(initialDelay=6000,fixedRate=180000)
	public void CheckForNewFile() {
		logger.info("*************************************************************************************");
		logger.info("************************* FILE READING JOB BEGIN AT ************************" + new Date());
		logger.info("*************************************************************************************");
		Properties properties = getSchedulerProperties();
		if (properties.propertyNames().hasMoreElements()) {
			File apisFolder = new File(properties.getProperty("apis.dir.origin"));
			File apisProcessedFolder = new File(properties.getProperty("apis.dir.processed"));
			// File apisWorkingFolder = new
			// File(properties.getProperty("apis.dir.working"));
			File pnrFolder = new File(properties.getProperty("pnr.dir.origin"));
			File pnrProcessedFolder = new File(properties.getProperty("pnr.dir.processed"));
			// File pnrWorkingFolder = new File(properties.getProperty("pnr.dir.working"));

			boolean finished = checkAndMoveApisFiles(apisFolder, apisProcessedFolder);
			if (finished) {
				checkAndMovePnrFiles(pnrFolder, pnrProcessedFolder);
			}
		} else {
			logger.info("**NO PROPERTY FILE FOUND - SHUTTING DOWN THE PROCESS**");
			logger.info("**PLEASE DEFINE PROPERTY FILE AND RESTART - EXITING**");
			System.exit(0);
		}
		logger.info("*************************************************************************************");
		logger.info("************************* FILE READING JOB ENDED AT **********************" + new Date());
		logger.info("*************************************************************************************");
	}

	private void checkAndMovePnrFiles(File folder, File processedFolder) {
		try {
			logger.info("#################################################################################");
			logger.info("#####################  CHECKING AND PROCESSING PNR FILES FROM ###################"
					+ folder.getAbsolutePath());

			if (!folder.isDirectory()) {
				return;
			}

			for (final File fileEntry : folder.listFiles()) {
				if (!fileEntry.isDirectory()) {
					FileSystem fromFileSystem = FileSystems.getDefault();
					FileSystem toFileSystem = FileSystems.getDefault();
					Path moveFrom = fromFileSystem.getPath(fileEntry.getPath());
					Path moveTo = toFileSystem
							.getPath(processedFolder.getPath() + File.separator + fileEntry.getName());
					if (fileEntry.isFile()) {
						logger.info("Reading file from : " + moveFrom);
						logger.info("Moving file after processing to : " + moveTo);
						boolean fileEntryRenamed = fileEntry.renameTo(moveTo.toFile());
						if (!fileEntryRenamed) {
							logger.error("fileEntry rename failed");
						}
					}
					fromFileSystem.close();
					toFileSystem.close();
				}
			}

			logger.info("#####################  FINISHED PROCESSING PNR FILES ############################");
		} catch (Exception e) {
			handleExceptions(e);
			logger.info("Exception saving PNR GOV message file" + e.getMessage());
		}
	}

	private void handleExceptions(Exception e) {
		ErrorDetailInfo errorDetails = ErrorHandlerFactory.createErrorDetails(e);
		try {
			errorDetails = errorService.create(errorDetails); // add the saved
			// ID
		} catch (Exception exception) {
			// possibly DB is down
			logger.error("Error creating error details", e);
		}
	}

	private boolean checkAndMoveApisFiles(File folder, File processedFolder) {
		try {
			logger.info("***********************CHECKING AND PROCESSING APIS FILES FROM**********************"
					+ folder.getAbsolutePath());

			if (!folder.isDirectory()) {
				return true;
			}

			for (final File fileEntry : folder.listFiles()) {
				if (!fileEntry.isDirectory()) {
					FileSystem fromFileSystem = FileSystems.getDefault();
					FileSystem toFileSystem = FileSystems.getDefault();
					Path moveFrom = fromFileSystem.getPath(fileEntry.getPath());
					Path moveTo = toFileSystem
							.getPath(processedFolder.getPath() + File.separator + fileEntry.getName());
					if (fileEntry.isFile()) {
						logger.info("Reading file from : " + moveFrom);
						// MessageLoader.processSingleFile(apisService,
						// fileEntry);
						// apisService.processMessage(moveFrom.toString());
						logger.info("Moving file after processing to : " + moveTo);
						boolean fileEntryRenamed = fileEntry.renameTo(moveTo.toFile());
						if (!fileEntryRenamed) {
							logger.error("fileEntry rename failed");
						}
					}
					fromFileSystem.close();
					toFileSystem.close();
				}
			}
		} catch (Exception e) {
			handleExceptions(e);
			logger.info("Exception saving APIS message file" + e.getMessage());
			return false;
		}

		return true;
	}

	private Properties getSchedulerProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource("loaderScheduler.properties").getFile());
			input = new FileInputStream(file);
			prop.load(input);
		} catch (IOException e1) {
			logger.info("Exception loading loaderScheduler.properties" + e1.getMessage());
		}
		return prop;
	}

	public ApisMessageService getApisService() {
		return apisService;
	}

	public void setApisService(ApisMessageService apisService) {
		this.apisService = apisService;
	}

	public PnrMessageService getPnrService() {
		return pnrService;
	}

	public void setPnrService(PnrMessageService pnrService) {
		this.pnrService = pnrService;
	}
}
