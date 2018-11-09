/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gtas.parsers.edifact.segment.UNA;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.EdifactUtils;
import gov.gtas.parsers.util.TextUtils;

import static gov.gtas.parsers.util.TextUtils.validSegmentStart;

/**
 * Class for tokenizing Edifact files
 */
public final class EdifactLexer {
    private static final Logger logger = LoggerFactory.getLogger(EdifactLexer.class);
    private final UNA una;

    private final String message;

    public EdifactLexer(String message) { 
        this.message = message;
        this.una = EdifactUtils.getUnaSegment(message);
    }
    
    /**
     * Find the starting index of a segment. There are two possibilities: first,
     * the segment name followed by the data element separator, e.g., NAD+.
     * Second, the segment name followed by the segment terminator, .e.g, SRC'.
     * 
     * @return the starting index of the 'segmentName' in 'msg'. Return -1 if
     *         does not exist.
     */
    public int getStartOfSegment(String segmentName) {
        String format;
        String regex;
        if (segmentName.equalsIgnoreCase("UNT")) {
            format = "%s\\s*(\\%c[0-9])";
            regex = String.format(format, segmentName,
                    this.una.getDataElementSeparator());
        } else {
            format = "%s\\s*(\\%c|\\%c)";
            regex = String.format(format, segmentName,
                    this.una.getDataElementSeparator(),
                    this.una.getSegmentTerminator());
        }
        return TextUtils.indexOfRegex(regex, this.message);
    }
    
    /**
     * Return everything from the start of the 'startSegment' to the
     * start of the 'endSegment' trailing header segment.
     */
    public String getMessagePayload(String startSegment, String endSegment) {
        int start = getStartOfSegment(startSegment);
        if (start == -1) {
            return null;
        }

        int end = getStartOfSegment(endSegment);
        if (end == -1) {
            return null;
        }
        
        return this.message.substring(start, end);
    }
    
    /**
     * Tokenize the input message into a list of {@code Segments} 
     */
    public List<Segment> tokenize() throws ParseException {
        SegmentTokenizer segmentTokenizer = new SegmentTokenizer(this.una);

        // start parsing with the UNB segment
        int unbIndex = getStartOfSegment("UNB");
        if (unbIndex == -1) {
            throw new ParseException("Required UNB segment not found");
        }
        
        String tmp = this.message.substring(unbIndex);
        String processedMessage = TextUtils.convertToSingleLine(tmp).toUpperCase();
        List<String> stringSegments = TextUtils.splitWithEscapeChar(
                processedMessage, 
                this.una.getSegmentTerminator(), 
                this.una.getReleaseCharacter());

        Pattern startOfSegmentPattern = getStartOfSegmentPattern();
        List<Segment> segments = new LinkedList<>();
        for (String s : stringSegments) {
            // add the segment terminator back (removed from split)
            Segment segment = segmentTokenizer.buildSegment(s);
            segment.setText(s + this.una.getSegmentTerminator());
            if (validSegmentStart(segment.getText(), startOfSegmentPattern)) {
                segments.add(segment);
            }
        }
        
        return segments;
    }

    private Pattern getStartOfSegmentPattern() {
        String format = "[a-zA-Z]{3}(\\%c|\\%c)";
        String formattedRegex = String.format(format,
                this.una.getDataElementSeparator(),
                this.una.getSegmentTerminator());
        return Pattern.compile(formattedRegex);
    }

    public UNA getUna() {
        return una;
    }

    public String getMessage() {
        return message;
    }
}
