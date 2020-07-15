/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.edifact;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ListIterator;

import gov.gtas.parsers.edifact.segment.UNB;
import gov.gtas.parsers.edifact.segment.UNE;
import gov.gtas.parsers.edifact.segment.UNG;
import gov.gtas.parsers.edifact.segment.UNH;
import gov.gtas.parsers.edifact.segment.UNT;
import gov.gtas.parsers.edifact.segment.UNZ;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.EdifactUtils;
import gov.gtas.parsers.util.TextUtils;
import gov.gtas.parsers.vo.MessageVo;

/**
 * The parser takes the output from the Edifact lexer and starts the process of
 * parsing the individual segments and extracting data. This class implements
 * the template pattern so subclasses can implement specific rules for parsing a
 * particular message payload. The generic Edifact segments -- UNB, UNH, etc. --
 * are parsed in this class.
 * 
 * @param <T>
 *            the specific message class that will be returned after parsing.
 */
public abstract class EdifactParser<T extends MessageVo> {
	/** factory for creating segment classes */
	private SegmentFactory segmentFactory;

	/** iterator for segment list */
	private ListIterator<Segment> iter;

	/** output from the edifact lexer. The first segment will always be UNB */
	protected List<Segment> segments;

	protected EdifactLexer lexer;

	/** the final parsed message we ultimately return */
	protected T parsedMessage;

	public EdifactParser() {
	}

	/**
	 * As per ISO 9735, the service segments are sequenced in a message in the
	 * following order:
	 * <ol>
	 * <li>UNA Service String Advice
	 * <li>UNB Interchange Header Segment
	 * <li>UNG Functional Group Header
	 * <li>UNH Message Header
	 * <li>(MESSAGE PAYLOAD/BODY)
	 * <li>UNT Message Trailer
	 * <li>UNE Functional Group Trailer
	 * <li>UNZ Interchange Trailer
	 * </ol>
	 * 
	 * @param message
	 * @return
	 * @throws ParseException
	 */
	public T parse(String message) throws ParseException {
		this.segmentFactory = new SegmentFactory();
		this.lexer = new EdifactLexer(message);
		this.segments = lexer.tokenize();
		this.iter = segments.listIterator();

		String payload = getPayloadText();
		if (payload == null) {
			throw new ParseException("Could not extract message payload");
		}
		String md5 = TextUtils.getMd5Hash(payload, StandardCharsets.US_ASCII);
		this.parsedMessage.setHashCode(md5);
		this.parsedMessage.setRaw(EdifactUtils.prettyPrint(this.lexer.getUna().getSegmentText(), this.segments));

		parseHeader();
		parsePayload();
		parseTrailer();

		return this.parsedMessage;
	}

	private void parseHeader() throws ParseException {
		UNB unb = getMandatorySegment(UNB.class);
		parsedMessage.setTransmissionSource(unb.getSenderIdentification());
		parsedMessage.setTransmissionDate(unb.getDateAndTimeOfPreparation());

		getConditionalSegment(UNG.class);

		UNH unh = getMandatorySegment(UNH.class);
		parsedMessage.setMessageType(unh.getMessageType());
		String ver = String.format("%s.%s", unh.getMessageTypeVersion(), unh.getMessageTypeReleaseNumber());
		parsedMessage.setVersion(ver);
	}

	private void parseTrailer() throws ParseException {
		getMandatorySegment(UNT.class);
		getConditionalSegment(UNE.class);
		getMandatorySegment(UNZ.class);
	}

	/**
	 * Subclasses implement this method to parse the message payload/body that's
	 * specific to the message type.
	 * 
	 * @throws ParseException
	 */
	protected abstract void parsePayload() throws ParseException;

	/**
	 * Retrieve the message payload: text between header and footer.
	 */
	protected abstract String getPayloadText() throws ParseException;

	/*
	 * get* methods below are used by subclasses to traverse the list of segments
	 * returned from the lexer.
	 */

	protected <S extends Segment> S getMandatorySegment(Class<S> clazz) throws ParseException {
		return getMandatorySegment(clazz, null);
	}

	/**
	 * Same as {@link #getMandatorySegment(Class)} but takes the segment name as an
	 * argument. This is useful in cases where the segment name differs from the
	 * actual class it's associated with.
	 */
	protected <S extends Segment> S getMandatorySegment(Class<S> clazz, String segmentName) throws ParseException {
		return getNextSegment(clazz, segmentName, true);
	}

	/**
	 * A conditional segment is an optional segment that may or may not exist. If it
	 * exists, return it; otherwise move the pointer back and return null.
	 */
	protected <S extends Segment> S getConditionalSegment(Class<S> clazz) throws ParseException {
		return getConditionalSegment(clazz, null);
	}

	protected <S extends Segment> S getConditionalSegment(Class<S> clazz, String segmentName) throws ParseException {
		S segment = getNextSegment(clazz, segmentName, false);
		if (segment != null) {
			return segment;
		}

		iter.previous();
		return null;
	}

	/**
	 * helper method for retrieving next segment from segment list.
	 */
	private <S extends Segment> S getNextSegment(Class<S> clazz, String segmentName, boolean mandatory)
			throws ParseException {
		String expectedName = (segmentName != null) ? segmentName : clazz.getSimpleName();

		if (iter.hasNext()) {
			Segment s = iter.next();
			if (expectedName.equals(s.getName())) {
				S rv = segmentFactory.build(s, clazz);
				return rv;
			} else {
				if (mandatory) {
					throw new ParseException("Expected segment " + expectedName + " but got " + s.getName());
				}
			}
		}

		if (mandatory) {
			throw new ParseException("Expected segment " + expectedName + " but no more segments to process");
		}

		return null;
	}
}
