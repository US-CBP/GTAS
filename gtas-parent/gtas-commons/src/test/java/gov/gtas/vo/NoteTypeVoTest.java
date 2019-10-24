/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.services.dto.PriorityVettingListRequest;
import org.junit.Test;
import org.springframework.util.Assert;

import java.io.IOException;

public class NoteTypeVoTest {

	private String noteTypeRequestExample = "{\"plainTextNote\":\"dgdfgd\\n\",\"rtfNote\":\"<div><!--block-->dgdfgd</div>\",\"passengerId\":\"131\",\"noteType\":[{\"id\":1,\"noteType\":\"GENERAL_PASSENGER\"}]}";

	private String exampleOfPVLRequest = "{\"pageSize\":\"10\",\"pageNumber\":\"1\",\"displayStatusCheckBoxes\":{\"NEW\":true,\"RE_OPENED\":true,\"REVIEWED\":false},\"ruleCatFilter\":[{\"name\":\"General\",\"value\":true},{\"name\":\"Terrorism\",\"value\":true},{\"name\":\"World Health\",\"value\":true},{\"name\":\"Federal Law Enforcement\",\"value\":true},{\"name\":\"Local Law Enforcement\",\"value\":true}],\"myRulesOnly\":false,\"ruleTypes\":{\"WATCHLIST\":true,\"USER_RULE\":true,\"GRAPH_RULE\":true,\"PARTIAL_WATCHLIST\":false},\"withTimeLeft\":true,\"etaStart\":\"2019-10-24T01:02:02.410Z\",\"etaEnd\":\"2019-10-25T01:17:02.410Z\",\"sort\":[{\"column\":\"countDown\",\"dir\":\"asc\"}]}";

	@Test
	public void marshallObjectTest() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		PriorityVettingListRequest pvlRequest = objectMapper.readValue(exampleOfPVLRequest,
				PriorityVettingListRequest.class);
		NoteVo noteTypeVo = objectMapper.readValue(noteTypeRequestExample, NoteVo.class);
		Assert.notNull(noteTypeVo, "must marshall");
	}
}
