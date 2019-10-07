/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.services.ApisMessageService;
import gov.gtas.services.MessageLoaderService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApisDirectoryReader implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ApisDirectoryReader.class);
	/** the watchService that is passed in from above */
	private WatchService fileWatcher;
	private String directoryPath;

	public ApisDirectoryReader(WatchService aWatcher) {
		this.fileWatcher = aWatcher;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	/**
	 * In order to implement a file watcher, we loop forever ensuring requesting to
	 * take the next item from the file watchers queue.
	 */
	@Override
	public void run() {
		ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(CommonServicesConfig.class,
				CachingConfig.class);
		MessageLoaderService svc = ctx.getBean(ApisMessageService.class);
		try {
			// get the first event before looping
			WatchKey key = fileWatcher.take();
			while (key != null) {
				// we have a polled event, now we traverse it and
				// receive all the states from it
				for (WatchEvent event : key.pollEvents()) {
					if (ENTRY_CREATE == event.kind()) {
						FileSystem fromFileSystem = FileSystems.getDefault();
						FileSystem toFileSystem = FileSystems.getDefault();
						// A new Path was created
						Path newPath = ((WatchEvent<Path>) event).context();
						logger.info("New file arrived: " + newPath);
						File f = newPath.toFile();
						Path moveFrom = fromFileSystem.getPath(getDirectoryPath() + File.separator + f.getName());
						Path moveTo = toFileSystem
								.getPath(getDirectoryPath() + File.separator + "OLD" + File.separator + f.getName());
						processNewFile(moveFrom, svc);

						Files.move(moveFrom, moveTo, StandardCopyOption.REPLACE_EXISTING);
						fromFileSystem.close();
						toFileSystem.close();

					} else if (ENTRY_DELETE == event.kind()) {
						logger.info("file moved: " + event.context());
					}
				}
				key.reset();
				key = fileWatcher.take();
				boolean valid = key.reset();

				if (!valid) {
					logger.info("Stopping thread" + valid);
					break;
				}
			}
		} catch (InterruptedException | IOException e) {
			logger.error("error!", e);
		} finally {
			ctx.close();
		}
		logger.info("Stopping thread");
	}

	private void processNewFile(Path p, MessageLoaderService svc) {

		// MessageLoader.processSingleFile(svc, p.toFile());
		logger.info("*****************processing file" + p.toFile().getAbsolutePath());
	}
}
