/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtils {
    private static final Logger logger = LoggerFactory.getLogger(TextUtils.class);

    private TextUtils() { }
    
    /**
     * Split a string 's' using 'delimiter' but don't split on any delimiters
     * escaped with 'escape' character.  For example, if we call this method
     * with s = "mc?'foo'bar", delimiter = '\'', escape = '?'  the method
     * should return ["mc'foo", "bar"].  Note as a side-effect, the escape
     * characters are removed from the final output.
     */
    public static List<String> splitWithEscapeChar(String s, char delimiter, char escape) {
        String escapedDelimiter = String.format("\\%c\\%c", escape, delimiter);
        final String sentinel = "~XYZ~";
        String tmp = s.replaceAll(escapedDelimiter, sentinel);
        
        String regex = String.format("\\%c", delimiter);
        String[] tmpSplit = tmp.split(regex);
        List<String> rv = new ArrayList<>(tmpSplit.length);
        for (String myString : tmpSplit) {
            rv.add(myString.replaceAll(sentinel, "\\" + delimiter).trim());
        }
        
        return rv;
    }
        
    /**
     * Eliminate all line terminators as well as any whitespace immediately
     * following line terminators.
     * 
     * @param str input string
     * @return a concatenation of all the lines in 'str' excluding any leading
     * or trailing whitespace.
     */
    public static String convertToSingleLine(String str) {
        return str.replaceAll("\\s*[\r\n]+\\s*", "").trim();
    }
    
    /**
     * @param txt input string
     * @param encoding character encoding of the input string
     * @return an md5 hash of the input string
     */
    public static String getMd5Hash(String txt, Charset encoding) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(txt.getBytes(encoding));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("error generating md5hash!", e);
        }
        
        return null;
    }
    
    /**
     * Just like String.indexOf but allows use of a regex.
     * 
     * Returns the index within this string of the first occurrence of the
     * specified character or -1 if the character does not occur.
     */
    public static int indexOfRegex(String regex, CharSequence input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.start();
        }        
        return -1;
    }
    /**
     * Just like String.indexOf but allows use of a regex.
     *
     * @param regex regex to use. Must contain segment name targeted.
     * @param segmentName Segment name used
     * @param message String of text to search for regex.
     *
     * Returns the index in the message where the segment name starts OR returns -1 if
     * segment does not occur.
     */
    public static int indexOfRegex(String regex, String segmentName, CharSequence message) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            CharSequence patternSubString = message.subSequence(matcher.start(), matcher.end());
            Pattern segmentMatch = Pattern.compile(segmentName);
            Matcher segmentMatcher = segmentMatch.matcher(patternSubString);
            segmentMatcher.find(); //segmentName is a part of the regex hit. It is not possible to not find a match.
            int indexRegexStartsMatching = matcher.start();
            int indexSegmentStartsMatching = segmentMatcher.start();
            int startOfSegment = indexRegexStartsMatching + indexSegmentStartsMatching;
            return startOfSegment;
        }
        return -1;
    }
}
