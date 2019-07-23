/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */

package gov.gtas.logcollector;

public class Constants {
	
	public final static String WINDOWS = "WINDOWS";
	public final static String LINUX = "LINUX";
	
	public final static String WINDOWS_CMD_CONST = "cmd /c";
	public final static String WINDOWS_PARAM_PREFIX = "/";
	public final static String WINDOWS_PARAM_SUFFIX = "=";
	public final static String WINDOWS_PARAM = "param";
	public final static String WINDOWS_PARAM_DELIMITER = ":";
	
	
	public final static String LINUX_CMD_CONST = "/bin/sh";
	public final static String LINUX_PARAM_PREFIX = "-";
	public final static String LINUX_PARAM_SUFFIX = "=";
	public final static String LINUX_PARAM = "param";
	public final static String LINUX_PARAM_DELIMITER = ":";
	
	public final static String LOG_REDIRECTOR = ">";
	public final static String FILE_IND = "file";
	public final static String LEVEL = "level";
	public final static String LOG_FILE_EXT = ".log";

}
