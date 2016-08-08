/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.search;

import java.util.List;

import org.springframework.stereotype.Service;

import gov.gtas.vo.passenger.PassengerVo;

@Service
public class SearchServiceElastic implements SearchService {
	@Override
	public List<PassengerVo> findPassengers(String query) {
		// TODO Auto-generated method stub
		return null;
	}
}
