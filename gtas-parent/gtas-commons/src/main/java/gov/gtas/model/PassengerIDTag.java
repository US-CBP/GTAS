/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import org.springframework.cache.annotation.Cacheable;

import javax.persistence.*;

@Entity
@Table(name = "passenger_id_tag", indexes = { @Index(columnList = "idTag", name = "id_index"),
		@Index(columnList = "doc_hash_id", name = "doc_hash_id_index") })
public class PassengerIDTag extends BaseEntityAudit {
	private static final long serialVersionUID = 1L;

	@Column(name = "idTag")
	private String idTag;

	@Column(name = "tamr_id")
	private String tamrId;

	public String getTamrId() {
		return tamrId;
	}

	public void setTamrId(String tamrId) {
		this.tamrId = tamrId;
	}

	@Column(name = "pax_id", columnDefinition = "bigint unsigned")
	private Long pax_id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pax_id", updatable = false, insertable = false)
	private Passenger passenger;

	@Column(name = "doc_hash_id")
	private String docHashId;

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	// public Set<Passenger> getPassengers() {
	// return passengers;
	// }
	//
	// public void setPassengers(Set<Passenger> passengers) {
	// this.passengers = passengers;
	// }

	public Long getPax_id() {
		return pax_id;
	}

	public void setPax_id(Long pax_id) {
		this.pax_id = pax_id;
	}

	public String getDocHashId() {
		return docHashId;
	}

	public void setDocHashId(String docHashId) {
		this.docHashId = docHashId;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}
}
