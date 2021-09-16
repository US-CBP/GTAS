/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class POETileServiceRequest {

    // e.g. 2015-10-02T18:33:03.412Z
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date etaStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private Date etaEnd;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String poeAirport;

	/**
	 * @return the poeAirport
	 */
	public String getPoeAirport() {
		return poeAirport;
	}

	/**
	 * @param poeAirport the poeAirport to set
	 */
	public void setPoeAirport(String poeAirport) {
		this.poeAirport = poeAirport;
	}

	public Date getEtaStart() {
        return etaStart;
    }

    public void setEtaStart(Date etaStart) {
        this.etaStart = etaStart;
    }

    public Date getEtaEnd() {
        return etaEnd;
    }

    public void setEtaEnd(Date etaEnd) {
        this.etaEnd = etaEnd;
    }
}
