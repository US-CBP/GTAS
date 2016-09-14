/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialClob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gtas.error.ErrorUtils;

public class LobUtils {
    private static final Logger logger = LoggerFactory.getLogger(LobUtils.class);
    
    public static String convertClobToString(Clob clob) {
    	if (clob == null) {
    		return null;
    	}
    	
        StringBuilder sb = null;
        try {
            Reader reader = clob.getCharacterStream();
            int c = -1;
            sb = new StringBuilder();
            while((c = reader.read()) != -1) {
                 sb.append((char)c);
            }
        } catch (SQLException | IOException e) {
            logger.error(ErrorUtils.getStacktrace(e));
            return null;
        }
        
        return sb.toString();
    }

    public static Clob createClob(String s) {
    	if (s == null) {
    		return null;
    	}
    	
        Clob rv = null;
        try {
            rv = new SerialClob(s.toCharArray());
        } catch (SQLException e) {
            logger.error(ErrorUtils.getStacktrace(e));
            return null;
        }
        return rv;
    }
}
