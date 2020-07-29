/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.paxlst;

import static org.junit.Assert.assertEquals;

import java.util.List;

import gov.gtas.config.ParserConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.parsers.vo.FlightVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PaxlstParserUSedifactTest {
	private static final Logger logger = LoggerFactory.getLogger(PaxlstParserUSedifactTest.class);
	EdifactParser<ApisMessageVo> parser;



	@Before
	public void setUp() {
		ParserConfig parserConfig = new ParserConfig(false, "VV");
		this.parser = new PaxlstParserUSedifact(parserConfig);
	}

	@Test
	public void testRandomApis() throws ParseException {
		String apis = "UNA:+.? '" + "UNB+UNOA:1+SAMPLAIR:NZ+USCS:US+020131:2359+0201312359++CEDIPAX+A+++0'"
				+ "UNG+PAXLST+SAMPLECOMM:NZ+TECS:US+020131:2359+0201312359+NZ+001:000'"
				+ "UNH+0201312359+PAXLST:001:000:NZ+SA812/020131/2300'"
				+ "CTA+IC+:FRED? SAMPLES+1-703-644-5200:TE+1-703-566-8224:FX'" + "TDT++SA812+40+SA'"
				+ "LOC+005+NZAKL:50'" + "DTM+136+020131+1030+M05'" + "LOC+008+USLAX:50'" + "DTM+132+020131+2200+M05'"
				+ "UNS+D'" + "PDT+P/182345345:990909:US+MANGUS:SIMON:P:590429:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/122222345:970902:US+FEFE:THEODORE:C:560704:U+PAX+NZAKL:USLAX'"
				+ "PDT+P/C76543D:920429:GB+BOYLE:ALVIN::521221:U+PAX+NZAKL:USLAX'"
				+ "PDT+P/E54321A:980831:IE+O?'LEARY:KRIS:ANN:331231:F+PAX+NZAKL:USLAX'" + "UNT+000011+0201312359'"
				+ "UNE+1+0201312359'" + "UNZ+1+0201312359'";

		ApisMessageVo vo = parser.parse(apis);
		List<FlightVo> flights = vo.getFlights();
		assertEquals(1, flights.size());
		assertEquals(4, vo.getPassengers().size());
		logger.info(vo.toString());
	}

	/**
	 * multi-leg journey (multiple loc-dtm's)
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testProgressiveFlightDtm() throws ParseException {
		String apis = "UNA:+.? '" + "UNB+UNOA:1+SAMPLEAIR:NZ+USCS:US+020131:2359+0201312359++CEDIPAX+A+++0'"
				+ "UNG+PAXLST+SAMPLCOMM:NZ+TECS:US+020131:2359+0201312359+NZ+001:000'"
				+ "UNH+0201312359+PAXLST:001:000:NZ+SA812/020131/2300+01:C'"
				+ "CTA+IC+:FRED-SAMPLE+1-703-644-5200:TE+1-703-566-8224:FX'" + "TDT++SA812+40+SA'" + "LOC+5+NZAKL:50'"
				+ "DTM+136+020131+2200+M10'" + "LOC+PT+USHNL:50'" + "DTM+132+020131+1900+P8'" + "LOC+PT+USLAX:50'"
				+ "DTM+132+020131+2300+P5'" + "LOC+8+USJFK:50'" + "DTM+132+020201+0400+P2'" + "UNS+D'"
				+ "PDT+P/121234324:980701:US+JONES:INDIANA::351230'" + "UNT+000008+0201312359'" + "UNE+1+0201312359'"
				+ "UNZ+1+0201312359'";

		ApisMessageVo vo = parser.parse(apis);
		List<FlightVo> flights = vo.getFlights();
		assertEquals(3, flights.size());
		assertEquals(1, vo.getPassengers().size());
		logger.info(vo.toString());
	}

	@Test
	@Ignore // invalid apis message.
	public void testMultiMessagePart1() throws ParseException {
		String apis = "UNA:+.? '" + "UNB+UNOA:1+SAMPLEAIR:NZ+USCS:US+020131:2359+0201312359++CEDIPAX+A+++0'"
				+ "UNG+PAXLST+SAMPLCOMM:NZ+TECS:US+020131:2359+0201312359+NZ+001:000'"
				+ "UNH+0201312359+PAXLST:001:000:NZ+SA812/020131/2300+01:C'"
				+ "CTA+IC+:FRED? SAMPLES+1-703-644-5200:TE+1-703-566-8224:FX'" + "TDT++SA812+40+SA'"
				+ "LOC+005+NZAKL:50'" + "LOC+008+USLAX:50'" + "UNS+D'"
				+ "PDT+P/E234567::NZ+MANGUS:SIMON:P:590429:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/01234567890:950130:US+FIFE:THEODORE:C:560704:U+PAX+NZAKL:USLAX'"
				+ "PDT+P/C76543D:920429:GB+BOYLE:ALVIN::521221:U+PAX+NZAKL:USLAX'"
				+ "PDT+P/E54321A:980831:IE+O?'LEARY:KRIS:ANN:331231:F+PAX+NZAKL:USLAX'"
				+ "PDT+P/678912:900401:US+SULLIVAN:DAVE::550101:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/45892:910908:GB+HENRY:SAMUEL::400201+PAX+NZAKL:USLAX'"
				+ "PDT+P/784310:931010:US+SAMS:HENRY::300110+PAX+NZAKL:USLAX'"
				+ "PDT+P/90324519:991001:BR+RAE:JEAN::410706+PAX+NZAKL:USLAX'"
				+ "PDT+P/223344:920130:US+SI:JUN::441010:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/75641:940409:IR+O?'SHEA:IAN:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/548211:921212:US+REED:ZANE::581212+PAX+NZAKL:USLAX'"
				+ "PDT+P/33213:901220:JP+OSO:OSRA::520203+PAX+NZAKL:USLAX'"
				+ "PDT+P/9192980:901202:US+LEE:LEE::590401+PAX+NZAKL:LAX'"
				+ "PDT+P/124359:910101:US+BROWN:BEN::561002:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/56284:931106:US+CARSON:GERALD::550409+PAX+NZAKL:USLAX'"
				+ "PDT+P/B458211:920105:JP+LYN:JOHN::510901+PAX+NZAKL:USLAX'"
				+ "PDT+P/B4788320:921010:US+SMITH:BARTHOLAMEW::502010+PAX+NZAKL:USLAX'"
				+ "PDT+P/9182838:910204:IE+LEMON:AURTHOR::511215:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/84930:901221:US+FINN:RICK::590701:M+PAX+NZAKL:USLAX'"
				+ "PDT+P/Z41894:951020:AF+RANCH:OOTH::511112:F+PAX+NZAKL:USLAX'" + "UNT+000027+0201312359'"
				+ "UNE+1+0201312359'" + "UNZ+1+0201312359'";

		ApisMessageVo vo = parser.parse(apis);
		List<FlightVo> flights = vo.getFlights();
		// assertEquals(3, flights.size());
		// assertEquals(1, vo.getPassengers().size());
		logger.info(vo.toString());

	}
}
