/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.segment.UNA;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.TextUtils;

/**
 * Tokenize a segment text into composites and elements.
 */
public final class SegmentTokenizer {
    private final UNA una;
    
    public SegmentTokenizer(UNA una) {
        this.una = una;
    }
    
    public Segment buildSegment(String segmentText) throws ParseException {
        List<String> tokens = TextUtils.splitWithEscapeChar(
                segmentText, 
                una.getDataElementSeparator(), 
                una.getReleaseCharacter()); 
        if (CollectionUtils.isEmpty(tokens)) { 
            throw new ParseException("Error tokenizing segment text " + segmentText);
        }

        // remove just the segment name
        String segmentName = tokens.remove(0);  
        if (StringUtils.isBlank(segmentName)) {
            throw new ParseException("Illegal segment name " + segmentName);                
        }

        List<Composite> composites = new ArrayList<>(tokens.size());
        for (String cText : tokens) {
            List<String> elements = TextUtils.splitWithEscapeChar(
                    cText, 
                    una.getComponentDataElementSeparator(),
                    una.getReleaseCharacter());
            composites.add(new Composite(elements));
        }

        return new Segment(segmentName, composites);
    }
}
