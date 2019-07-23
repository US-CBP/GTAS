/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class LogFileVo {
  private String fileName;
  private Long size;
  private Date creationDate;
  private Date lastModified;

  public LogFileVo() {
  }

  public LogFileVo(File file) throws IOException {
      this.fileName = file.getName();

      BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

      this.size = attributes.size();
      this.lastModified = new Date(attributes.lastModifiedTime().toMillis());
      this.creationDate = new Date(attributes.creationTime().toMillis());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date date) {
        this.creationDate = date;
    }

    public Date getLastModified() {
      return lastModified;
  }

  public void setLastModified(Date date) {
      this.lastModified = date;
  }

    
}
