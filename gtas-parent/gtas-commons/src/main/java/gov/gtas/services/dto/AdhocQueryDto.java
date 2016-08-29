/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.List;
import gov.gtas.vo.passenger.FlightVo;
import gov.gtas.vo.passenger.PassengerVo;


//Multipurpose object that should be able to return either passengers or flights alongside their total number of hits for adhoc query
public class AdhocQueryDto {
	private List<PassengerVo> passengers;
	private List<FlightVo> flights;
	private long totalHits;
	
	public AdhocQueryDto(List<PassengerVo> pass, List<FlightVo> flights, long l){
		this.flights = flights;
		this.passengers = pass;
		this.totalHits = l;
	}

	public List<PassengerVo> getPassengers() {
		return passengers;
	}

	public List<FlightVo> getFlights() {
		return flights;
	}

	public long getTotalHits() {
		return totalHits;
	}
	
}
