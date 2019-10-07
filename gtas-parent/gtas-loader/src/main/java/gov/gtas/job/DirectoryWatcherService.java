/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryWatcherService {
	private static final Logger logger = LoggerFactory.getLogger(DirectoryWatcherService.class);
	/** change this as appropriate for your file system structure. */
	private static String DIRECTORY_TO_WATCH = null;// C:\\TEST\\APIS

	public static void main(String[] args) throws Exception {
		if (args != null && args.length >= 1) {
			DIRECTORY_TO_WATCH = args[0];
		} else {
			logger.info("Enter Directory Name to Watch : ");
			Scanner scanIn = new Scanner(System.in);
			DIRECTORY_TO_WATCH = scanIn.nextLine();
			scanIn.close();
			logger.info(DIRECTORY_TO_WATCH);
		}

		// get the directory we want to watch, using the Paths singleton class
		Path toWatch = Paths.get(DIRECTORY_TO_WATCH);
		if (toWatch == null) {
			throw new UnsupportedOperationException("Directory not found");
		}
		logger.info("*************DIRCTORY EXIST " + toWatch.getFileName());
		// make a new watch service that we can register interest in
		// directories and files with.
		WatchService gtasWatcher = toWatch.getFileSystem().newWatchService();

		// start the file watcher thread below
		ApisDirectoryReader fileWatcher = new ApisDirectoryReader(gtasWatcher);
		fileWatcher.setDirectoryPath(DIRECTORY_TO_WATCH);
		Thread th = new Thread(fileWatcher, "FileWatcher");
		th.start();

		// register a file
		toWatch.register(gtasWatcher, ENTRY_CREATE, ENTRY_DELETE);
		th.join();
	}

}
