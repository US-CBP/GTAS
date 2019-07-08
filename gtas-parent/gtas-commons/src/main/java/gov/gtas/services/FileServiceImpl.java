/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

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
    public String[][] getLogZipList(String logType){
      String directory = logPath + "\\" + logType;
  
      try {
        File[] files = new File(directory).listFiles(new FileFilter() {
          @Override
          public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase().endsWith(".zip");
          }
      });
  
      String[][] attributes = new String[files.length][4];
      int idx = 0;
  
        for (File file: files) {
          BasicFileAttributes attrib = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
  
          attributes[idx][0] = file.getName();
          attributes[idx][1] = Long.toString(attrib.size());
          attributes[idx][2] = attrib.creationTime().toString();
          attributes[idx][3] = attrib.lastModifiedTime().toString();
          idx++;
        }
        return attributes;
      }
      catch (Exception ex) {
        logger.error("FileServiceImpl.getLogZipList - unable to access files in directory '" + logPath
        + "\\" + logType + "'. Directory must be set in application.properties. Error message: " + ex);
      }
  
      return null;
    }
  
    //GET ZIP BINARY
    @Override
    public byte[] getLogZip(String logType, String logFile) {
      String filename = logFile.endsWith(".zip") ? logFile : logFile + ".zip";
  
      try {
        String path = logPath + "\\" + logType + "\\" + filename;
        File file = new File(path);
        
        byte[] output = Files.readAllBytes(file.toPath());
  
        return output;
      }
      catch (Exception ex) {
        logger.error("FileServiceImpl.getLogZip - unable to send zip binary from '" + logPath
        + "\\" + logType + "\\" + filename + "'. Directory must be set in application.properties. Error message: " + ex);
      }
      return null;
    }
  
    public File getLogZip2(String logType, String logFile) {
      String filename = logFile.endsWith(".zip") ? logFile : logFile + ".zip";
  
      try {
        String path = logPath + "\\" + logType + "\\" + filename;
        File file = new File(path);
        return file;
      }
      catch (Exception ex) {
        logger.error("FileServiceImpl.getLogZip - unable to send zip binary from '" + logPath
        + "\\" + logType + "\\" + filename + "'. Directory must be set in application.properties. Error message: " + ex);
      }
      return null;
    }

}
