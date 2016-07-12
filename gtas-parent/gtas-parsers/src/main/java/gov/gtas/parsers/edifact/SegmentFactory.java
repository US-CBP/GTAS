/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.ErrorUtils;

/**
 * Build segment classes based on their class name.
 */
public class SegmentFactory {
    public <T extends Segment> T build(Segment s, Class<T> clazz) throws ParseException {
        try {
            Object[] args = { s.getComposites() };
            return clazz.getDeclaredConstructor(List.class).newInstance(args);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t != null) {
                throw new ParseException(ErrorUtils.getStacktrace(t));
            }
            throw new ParseException(ErrorUtils.getStacktrace(e));
            
        } catch (Exception e) {
            throw new ParseException(ErrorUtils.getStacktrace(e));
        }
    }
}
