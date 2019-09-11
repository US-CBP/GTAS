/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.job.scheduler;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Paths;

import gov.gtas.job.scheduler.Utils;
import gov.gtas.services.LoaderException;

public class UtilsTest {

	@Value("${message.dir.processed}")
	private String processedstr;

	@Value("${message.dir.working}")
	private String workingstr;

	@Value("${message.dir.working}")
	private String errorstr;


    @Before
    public void before() {
    }

    @After
    public void after() {
      
    }

    @Test
    public void testMoveToDir_ERROR() throws LoaderException {
      String filename = "TESTFILENAME";
      File test = getWorkingFile(filename, "THIS IS SOME TEST TEXT");

      Utils.moveToDirectory(errorstr, test);
      String pathstr = errorstr + File.separator + filename;

      Assert.isTrue(Files.exists(Paths.get(pathstr)), "File not found in Error dir");
    }

    @Test
    public void testMoveToDir_PROCESSED() throws LoaderException {
      String filename = "TESTFILENAME2";
      File test = getWorkingFile(filename, "more text stuff");

      Utils.moveToDirectory(processedstr, test);
      String pathstr = errorstr + File.separator + filename;

      Assert.isTrue(Files.exists(Paths.get(pathstr)), "File not found in Processed dir");
    }

    private File getWorkingFile(String fileName, String fileText) {
      File file = new File(Paths.get("").toAbsolutePath().getParent() + File.separator + fileName);

      try {
      FileWriter fw = new FileWriter(file, false);
      fw.write(fileText);
      fw.close();
      }
      catch (Exception ex) {

      }
      
      return file;
    }

}
