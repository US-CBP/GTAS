/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.dto.FlightDto;
import gov.gtas.dto.PaxDto;

import java.util.List;

public class GenHelper {
	private static String newline = System.getProperty("line.separator");
	private static String mNum = "0" + GenUtil.getRandomNumber(999) + "A" + GenUtil.getRandomNumber(999);
	private static int counter = 0;

	public static void preparePnrData(List<FlightDto> flightList) {
		// int counter=0;
		for (FlightDto f : flightList) {

			String carrier = f.getCarrier();
			String origin = f.getEmbark();
			String dest = f.getDebark();
			String fNumber = f.getFlightNum();
			String dString = f.getToDay();
			for (PaxDto p : f.getPaxList()) {
				counter++;
				StringBuilder sb = new StringBuilder();
				PnrGen.buildHeader(carrier, sb);
				PnrGen.buildMessage("22", sb);
				PnrGen.buildOrigDestinations(carrier, origin, dest, fNumber, dString, sb);
				PnrGen.buildEqn(1, sb);
				// PnrGen.buildSrc(carrier, origin,dest,fNumber,dString,1,f,sb);
				PnrGen.buildPnrSrcData(f, p, sb);
				PnrGen.buildFooter(sb);
				PnrGen.writeToFile(counter, sb);
				sb = null;
			}
		}

	}

	public static void prepareApisData(List<FlightDto> flights) {
		PnrGen.buildApisMessages(flights, counter);
	}
}
