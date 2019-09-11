
/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Date;
import gov.gtas.services.LoaderException;
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
    String fullTarget = target + File.separator + file.getName();
    Path targetPath = Paths.get(fullTarget);
    
    try {
      if(Files.exists(targetPath)) {
        String fileName = file.getName();
        String ext = "." + FilenameUtils.getExtension(fileName);
        String nameOnly = FilenameUtils.getBaseName(fileName);

        targetPath = targetPath.resolveSibling(nameOnly + getTimestamp() + ext);
        logger.warn("Duplicate file in target dir! File has been renamed: " + targetPath.toString());
      }

      Files.move(file.toPath(), targetPath, StandardCopyOption.ATOMIC_MOVE);
    }
    catch (Exception ex) {
      throw new LoaderException("Could not move file to target dir: " + targetPath.toString());
    }

    return targetPath.toFile();
  }

  private static String getTimestamp() {
    return " ~" + new Date().getTime();
  }
} //