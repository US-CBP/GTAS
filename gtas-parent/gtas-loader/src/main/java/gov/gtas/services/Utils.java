
/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Utils {
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * Move a file to a new directory, renaming it (appends timestamp) if the
	 * filename is already present in the target path.
	 *
	 * @param target
	 * @param file
	 * @return the new File object
	 * @throws LoaderException
	 */
	public static File moveToDirectory(String target, File file) throws LoaderException {
		if (notExists(target) || file == null) {
			return file;
		}
		String fullTarget = target + File.separator + file.getName();
		Path targetPath = Paths.get(fullTarget);

		try {
			// if(Files.exists(targetPath)) {
			// String uniqueFilename = getUniqueFilename(file.getName());
			// targetPath = targetPath.resolveSibling(uniqueFilename);
			// logger.warn("Duplicate file in target dir! File has been renamed: " +
			// targetPath.toString());
			// }

			Files.move(file.toPath(), targetPath, StandardCopyOption.ATOMIC_MOVE);
		} catch (Exception ex) {
			throw new LoaderException("Could not move file to target dir: " + targetPath.toString());
		}

		return targetPath.toFile();
	}

	public static File writeToDisk(String fileName, String fileText, String target) {
		if (notExists(fileName) || notExists(target)) {
			return null;
		}

		Path targetPath = Paths.get(target + File.separator + fileName);
		File f = targetPath.toFile();

		try (FileWriter fw = new FileWriter(f, false)) {
			fw.write(fileText);
		} catch (IOException e) {
			// attempt to write it to an error directory here??
			logger.error("error writing to directory " + target, e);
		}

		return f;
	}

	private static boolean notExists(String str) {
		return str == null || str.isEmpty();
	}
}