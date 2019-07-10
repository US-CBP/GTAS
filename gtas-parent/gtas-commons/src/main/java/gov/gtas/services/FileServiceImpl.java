/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.List;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import gov.gtas.vo.LogFileVo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

@Service
public class FileServiceImpl implements FileService {

  @Value("${logs.dir.root}")
  private String logPath;
  
	private static final Logger logger = LoggerFactory
			.getLogger(FileServiceImpl.class);
  
    //GET LIST OF AVAILABLE LOG TYPES
    @Override
    public String[] getLogTypeList() {
  
      try {
        File[] files = new File(logPath).listFiles(new FileFilter() {
          @Override
          public boolean accept(File file) {
            return file.isDirectory();
          }
      });

      String[] logFolders = new String[files.length];
      int idx = 0;

      for (File file: files) {
        logFolders[idx] = file.getName();
        idx++;
      }

      return logFolders;
    }
    catch (Exception ex) {
      logger.error("FileServiceImpl.getLogTypes - unable to access log types in directory '" + logPath
      + "'. Directory must be set in application.properties. Error message: " + ex);
    }
    return null;
  }
  
    // GET LIST OF AVAILABLE LOG FILES BY LOG TYPE. SHOW ZIP FILES ONLY
    @Override
    public List<LogFileVo> getLogZipList(String logType){
      List<LogFileVo> result = new ArrayList<LogFileVo>();
      Path path = Paths.get(logPath, logType);

      try {
        File[] files = new File(path.toString()).listFiles(new FileFilter() {
          @Override
          public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase().endsWith(".zip");
          }
      });
  
      for (File file: files) {
        result.add(new LogFileVo(file));
      }
    }

      catch (Exception ex) {
        logger.error("FileServiceImpl.getLogZipList - unable to access files in directory '" + path.toString()
         + "'. Directory must be set in application.properties. Error message: " + ex);
      }
  
      return result;
  }
    
    public File getLogZip(String logType, String logFile) {
      String filename = logFile.endsWith(".zip") ? logFile : logFile + ".zip";
  
      try {
        Path path = Paths.get(logPath, logType, filename);
        File file = path.toFile();
        return file;
      }
      catch (Exception ex) {
        logger.error("FileServiceImpl.getLogZip - unable to send zip binary from '" + logPath
        + "\\" + logType + "\\" + filename + "'. Directory must be set in application.properties. Error message: " + ex);
      }
      return null;
    }

}
