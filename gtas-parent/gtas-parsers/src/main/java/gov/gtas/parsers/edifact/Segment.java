/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent Edifact segment class. An Edifact segment consists of a segment name
 * followed by zero or more {@code Composites}. If the constructor receives an
 * empty list of composites, it indicates that the segment is used as a 'marker'
 * segment.
 * 
 * @see Composite
 */
public class Segment {
    protected static final Logger logger = LoggerFactory.getLogger(Segment.class);

    /** the segment name (NAD, UNC, CNT, etc.) */
    private final String name;

    /** original segment text, including any composites. Optional */
    private String text;

    /** list of segment fields/composites */
    private final List<Composite> composites;

    public Segment(String name, List<Composite> composites) {
        this(name, "", composites);
    }
    
    public Segment(String name, String text, List<Composite> composites) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        
        this.name = name;
        this.text = text;
        if (CollectionUtils.isNotEmpty(composites)) {
            this.composites = composites;            
        } else {
            this.composites = new ArrayList<>();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Composite> getComposites() {
        return this.composites;
    }

    /**
     * A "safe" getter for retrieving a composite by index.
     * @param index 0-based index of composite to retrieve
     * @return the composite given by the index; null if it does not exist.
     */
    public Composite getComposite(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        } else if (index >= numComposites()) {
            return null;
        } else {
            return this.composites.get(index);
        }
    }
    
    public int numComposites() {
        return this.composites.size();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
