/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import gov.gtas.parsers.ParserTestHelper;
import org.junit.Test;

import gov.gtas.parsers.edifact.EdifactLexer;

import java.io.IOException;
import java.net.URISyntaxException;

public class PnrUtilsTest implements ParserTestHelper {
	private static final String BIG_PNR = "/pnr-messages/bigbigPnr.txt";

	@Test
	public void testgetSinglePnr() {
		String msg = "UNA:+.?*'\n" + "UNB+IATA:1+DL++101209:2100+020A07'\n" + "UNH+1+PNRGOV:10:1:IA+F6C2C268'\n"
				+ "MSG+:22'\n" + "ORG+DL:ATL+52519950'\n" + "TVL+121210:0915+LHR+JFK+DL+324'\n" + "EQN+2'" + "SRC'"
				+ "RCI+DL:MFN4TI1'" + "SRC'" + "RCI+DL:MFN4TI2'" + "SRC'" + "RCI+DL:MFN4TI3'" + "UNT+135+1'\n"
				+ "UNZ+1+020A07'";

		EdifactLexer lexer = new EdifactLexer(msg);
		assertEquals(null, PnrUtils.getSinglePnr(lexer, -1));
		assertEquals("SRC'RCI+DL:MFN4TI1'", PnrUtils.getSinglePnr(lexer, 0));
		assertEquals("SRC'RCI+DL:MFN4TI2'", PnrUtils.getSinglePnr(lexer, 1));
		assertEquals("SRC'RCI+DL:MFN4TI3'", PnrUtils.getSinglePnr(lexer, 2));
		assertEquals(null, PnrUtils.getSinglePnr(lexer, 3));
	}

	@Test
	public void testBigFile() throws IOException, URISyntaxException {
		String msg = getMessageText(BIG_PNR);
		EdifactLexer lexer = new EdifactLexer(msg);
		Object foo = PnrUtils.getSinglePnr(lexer, 46);
		assertNotNull(foo);
	}
}
