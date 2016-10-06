/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import gov.gtas.enumtype.YesNoEnum;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "white_list")
public class Whitelist extends BaseEntityAudit {
	private static final long serialVersionUID = 1;

	public Whitelist() {
	}

	@Enumerated(value = EnumType.STRING)
	@Column(name = "DEL_FLAG", nullable = false, length = 1)
	private YesNoEnum deleted;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name")
	private String lastName;

	@Column(length = 2)
	private String gender;

	@Temporal(value = TemporalType.DATE)
	private Date dob;

	@Column(name = "citizenship_country")
	private String citizenshipCountry;

	@Column(name = "residency_country")
	private String residencyCountry;

	@Column(name = "document_type", length = 3, nullable = false)
	private String documentType;

	@Column(name = "document_number", nullable = false)
	private String documentNumber;

	@Column(name = "expiration_date")
	@Temporal(value = TemporalType.DATE)
	private Date expirationDate;

	@Column(name = "issuance_date")
	@Temporal(value = TemporalType.DATE)
	private Date issuanceDate;

	@Column(name = "issuance_country")
	private String issuanceCountry;

	@ManyToOne
	@JoinColumn(name = "editor", referencedColumnName = "user_id", nullable = false)
	private User whiteListEditor;

}