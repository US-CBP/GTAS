
/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import gov.gtas.parsers.util.FileUtils;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.services.LoaderException;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import org.apache.commons.io.FilenameUtils;

public class Utils {
  private static final Logger logger = LoggerFactory.getLogger(Utils.class);
  
  /**
   * Move a file to a new directory, renaming it (appends timestamp) if the filename is
   * already present in the target path.
   * @param target
   * @param file
   * @return the new File object
   * @throws LoaderException
   */
  public static File moveToDirectory(String target, File file) throws LoaderException {
    if (!exists(target) || file == null) {
      return file;
    }
    String fullTarget = target + File.separator + file.getName();
    Path targetPath = Paths.get(fullTarget);
    
    try {
      // if(Files.exists(targetPath)) {
      //   String uniqueFilename = getUniqueFilename(file.getName());
      //   targetPath = targetPath.resolveSibling(uniqueFilename);
      //   logger.warn("Duplicate file in target dir! File has been renamed: " + targetPath.toString());
      // }

      Files.move(file.toPath(), targetPath, StandardCopyOption.ATOMIC_MOVE);
    }
    catch (Exception ex) {
      throw new LoaderException("Could not move file to target dir: " + targetPath.toString());
    }

    return targetPath.toFile();
  }

  public static File writeToDisk(String fileName, String fileText, String target) {
    if (!exists(fileName) || !exists(target)) {
      return null;
    }

    Path targetPath = Paths.get(target + File.separator + fileName);
    File f = null;
    FileWriter fw = null;

    try {
      // if(Files.exists(targetPath)) {
      //   String uniqueFilename = getUniqueFilename(fileName);
      //   targetPath = targetPath.resolveSibling(uniqueFilename);
      // }

      f = targetPath.toFile();

      fw = new FileWriter(f, false);
      fw.write(fileText);
      fw.close();
    } catch (IOException e) {
      // attempt to write it to an error directory here??
      logger.error("error writing to directory " + target, e);
    } finally {
      try {
        if (fw != null) {
          fw.close();
        }
      } catch (IOException e) {
        logger.error("error writing to directory (closing file) " + target, e);
      }
    }
    
    return f;
  }

  private static String getUniqueFilename(String filename) {
    String ext = FilenameUtils.getExtension(filename).isEmpty() ? "" : "." + FilenameUtils.getExtension(filename);
    String nameOnly = FilenameUtils.getBaseName(filename);

    return nameOnly + getTimestamp() + ext;
  }

  private static String getTimestamp() {
    return " ~" + new Date().getTime();
  }

  public static String getText(String filePath) {
    String text = null;
    if (!exists(filePath)) return text;

    try {
      byte[] raw = FileUtils.readSmallFile(filePath);
      String tmp = new String(raw, StandardCharsets.UTF_8);
      text = ParseUtils.stripStxEtxHeaderAndFooter(tmp);
      }
      catch (IOException ex) {
        //??
      }

    return text;
  }

  public static boolean exists(String str) {
    return str != null && !str.isEmpty();
  }
} //