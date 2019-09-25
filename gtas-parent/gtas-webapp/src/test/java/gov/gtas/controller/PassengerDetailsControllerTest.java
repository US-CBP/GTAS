package gov.gtas.controller;

import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.PnrVo;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class PassengerDetailsControllerTest {

	@Test
	public void passengerVoTest() {
		PassengerDetailsController passengerDetailsController = new PassengerDetailsController();
		PnrVo pnrVo = new PnrVo();
		AddressVo addressVo = new AddressVo();

		ArrayList<AddressVo> addresses = new ArrayList<AddressVo>();
		addresses.add(addressVo);
		pnrVo.setRaw("ADD++702:4327 LEGGETT AVENUE::::PT::904151599751'");
		pnrVo.setAddresses(addresses);

		try {
			assertNull(addressVo.getCity());
			passengerDetailsController.parseRawMessageToSegmentList(pnrVo);
		} catch (Exception e) {
			fail("This method should not throw an exception when City is null!!!");
		}

	}
}
