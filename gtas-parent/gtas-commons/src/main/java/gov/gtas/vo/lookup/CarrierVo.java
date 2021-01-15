/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.lookup;

import java.io.Serializable;

public class CarrierVo implements Serializable {

	private Long id;
	private Long originId;
	private String name;
	private String iata;
	private String icao;
	Boolean archived;

	public CarrierVo(Long id, Long originId, String name, String iata, String icao, Boolean archived) {
		this.id = id;
		this.originId = originId;
		this.name = name;
		this.iata = iata;
		this.icao = icao;
		this.archived = archived;
	}

	public CarrierVo(Long id, Long originId, String name, String iata, String icao) {
		this(id, originId, name, iata, icao, false);
	}

	public CarrierVo() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOriginId() {
		return originId;
	}

	public void setOriginId(Long data) {
		this.originId = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String data) {
		this.name = data;
	}

	public String getIata() {
		return iata;
	}

	public void setIata(String data) {
		this.iata = data;
	}

	public String getIcao() {
		return icao;
	}

	public void setIcao(String data) { this.icao = data; }

	public Boolean getArchived() { return archived; }

	public void setArchived(Boolean archived) { this.archived = archived; }

}
