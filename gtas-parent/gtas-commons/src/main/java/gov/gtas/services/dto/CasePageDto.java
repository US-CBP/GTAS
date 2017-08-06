/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import gov.gtas.vo.passenger.CaseVo;

import java.util.List;

public class CasePageDto {

    private List<CaseVo> cases;
    private long totalCases;
    public CasePageDto(List<CaseVo> cases, long totalCases) {
        this.cases = cases;
        this.totalCases = totalCases;
    }
    public List<CaseVo> getcases() {
        return cases;
    }
    public long gettotalCases() {
        return totalCases;
    }
}
