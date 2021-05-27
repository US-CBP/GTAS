/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import gov.gtas.vo.passenger.CaseVo;

import java.util.List;

public class PriorityVettingListDTO {
	private List<CaseVo> cases;

  public PriorityVettingListDTO(List<CaseVo> cases) {
		this.cases = cases;
	}

	public List<CaseVo> getcases() {
		return cases;
	}
}
