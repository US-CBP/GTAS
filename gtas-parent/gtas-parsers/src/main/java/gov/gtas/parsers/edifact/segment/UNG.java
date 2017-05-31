/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact.segment;

import java.util.List;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * UNG: FUNCTIONAL GROUP HEADER
 * <p>
 * Function: To head, identify and specify a Functional Group. If a service
 * bureau, GDS, or other transmitting third party is transmitting the message on
 * behalf an aircraft operator, this segment should specify the identity of the
 * aircraft operator of record (not the transmitter of the message). ￼
 * <p>
 * We don't need any info from UNG for our implementation.
 * <p>
 * Example: UNG+PAXLST+AIRLINE1+NZCS+130628:0900+000000001+UN+D:12B'
 */
public class UNG extends Segment {
    public UNG(List<Composite> composites) {
        super(UNG.class.getSimpleName(), composites);
    }
}
