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
import org.junit.runner.RunWith;
import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import gov.gtas.services.LoaderException;
import gov.gtas.services.Utils;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UtilsIT.class)
@TestPropertySource("classpath:default.application.properties")
public class UtilsIT {

	@Value("${message.dir.origin}")
	private String originstr;

	@Value("${message.dir.processed}")
	private String processedstr;

	@Value("${message.dir.working}")
	private String workingstr;

	@Value("${message.dir.error}")
	private String errorstr;

	private final String TESTFILE = "TESTFILENAME.txt";
	private final String TESTFILE2 = "TESTFILENAME";

	@Before
	public void before() {
	}

	@After
	public void after() {
	}

	@Test
	public void testMoveToDir_ERROR() throws LoaderException {
		String filename = TESTFILE2;
		File test = getOriginFile(filename, "THIS IS SOME TEST TEXT");

		moveToDirWrapper(errorstr, test);
		String pathstr = errorstr + File.separator + filename;

		Assert.isTrue(Files.exists(Paths.get(pathstr)), "File not found in Error dir");
	}

	@Test
	public void testMoveToDir_PROCESSED() throws LoaderException {
		String filename = TESTFILE;
		File test = getOriginFile(filename, "more text stuff");

		moveToDirWrapper(processedstr, test);
		String pathstr = processedstr + File.separator + filename;

		Assert.isTrue(Files.exists(Paths.get(pathstr)), "File not found in Processed dir");
	}

	@Test
	public void testMoveToDir_WORKING() throws LoaderException {
		String filename = TESTFILE;
		File test = getOriginFile(filename, "more text stuff");

		moveToDirWrapper(workingstr, test);
		String pathstr = workingstr + File.separator + filename;

		Assert.isTrue(Files.exists(Paths.get(pathstr)), "File not found in WORKING dir");
	}

	@Test
	public void testMoveToDir_NULL() throws LoaderException {
		String filename = TESTFILE;
		String pathstr = workingstr + File.separator + filename;

		File existing = new File(pathstr);
		existing.delete();

		File test = getOriginFile(filename, "more text stuff");
		moveToDirWrapper(null, test);

		assertFalse("File should not exist in WORKING", Files.exists(Paths.get(pathstr)));
	}

	/**
	 * TestMoveDuplicates tests - Utils.moveToDirectory should overwrite existing
	 * files when moving, but should append a timestamp and save as a new file.
	 * 
	 * @throws LoaderException
	 */
	@Test
	public void testMoveDuplicates_WORKING() throws LoaderException {
		String filename = TESTFILE2;
		File test = getOriginFile(filename, "more text stuff");

		moveToDirWrapper(workingstr, test);
		String originalpathstr = workingstr + File.separator + filename;

		Assert.isTrue(Files.exists(Paths.get(originalpathstr)), "File not found in WORKING dir");

		// repeat with the same filenames, expect 2 files in the target dir.
		File test2 = getOriginFile(filename, "more text stuff");

		File dupeFile = moveToDirWrapper(workingstr, test2);
		Assert.isTrue(Files.exists(dupeFile.toPath()), "DUPE file not found in WORKING dir");
		assertEquals(originalpathstr, dupeFile.toPath().toString());
	}

	@Test
	public void testMoveDuplicates_PROCESSED() throws LoaderException {
		String filename = TESTFILE;
		File test = getOriginFile(filename, "more text stuff");

		moveToDirWrapper(processedstr, test);
		String originalpathstr = processedstr + File.separator + filename;

		Assert.isTrue(Files.exists(Paths.get(originalpathstr)), "File not found in Processed dir");

		// repeat with the same filenames, expect 2 files in the target dir.
		File test2 = getOriginFile(filename, "more text stuff");

		File dupeFile = moveToDirWrapper(processedstr, test2);
		Assert.isTrue(Files.exists(dupeFile.toPath()), "DUPE file not found in Processed dir");
		assertEquals(originalpathstr, dupeFile.toPath().toString());
	}

	@Test
	public void testMoveDuplicates_ERROR() throws LoaderException {
		String filename = TESTFILE;
		File test = getOriginFile(filename, "more text stuff");

		moveToDirWrapper(errorstr, test);
		String originalpathstr = errorstr + File.separator + filename;

		Assert.isTrue(Files.exists(Paths.get(originalpathstr)), "File not found in ERROR dir");

		// repeat with the same filenames, expect 2 files in the target dir.
		File test2 = getOriginFile(filename, "more text stuff");

		File dupeFile = moveToDirWrapper(errorstr, test2);
		Assert.isTrue(Files.exists(dupeFile.toPath()), "DUPE file not found in ERROR dir");
		assertEquals(originalpathstr, dupeFile.toPath().toString());
	}

	// ---------------------------
	// PRIVATE METHODS
	// ---------------------------

	// Wrapper for the method under test, Utils.moveToDir.
	// Ensures moved files get deleted on exit since we are actually writing to disk
	// for integration tests. Calling Utils.moveToDir directly will require manual
	// cleanup.
	private File moveToDirWrapper(String target, File file) throws LoaderException {
		File movedFile = gov.gtas.services.Utils.moveToDirectory(target, file);
		movedFile.deleteOnExit();
		return movedFile;
	}

	// Creates a new file in the origin directory, deletes it on test completion.
	private File getOriginFile(String fileName, String fileText) throws LoaderException {
		File file = new File(Paths.get(originstr) + File.separator + fileName);

		file.deleteOnExit();
		try {
			FileWriter fw = new FileWriter(file, false);
			fw.write(fileText);
			fw.close();
		} catch (Exception ex) {
		}

		return file;
	}

} //
