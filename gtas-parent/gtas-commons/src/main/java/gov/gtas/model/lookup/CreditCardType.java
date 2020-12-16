/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import gov.gtas.model.BaseEntity;
import org.springframework.cache.annotation.Cacheable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Cacheable
@Entity
@Table(name = "credit_card_type", indexes = { @Index(columnList = "code", name = "cctype_code_index") })
public class CreditCardType extends BaseEntity {
	private Long originId;

	@Column(length = 2)
	private String code;

	private String description;

	private Boolean archived;

	public CreditCardType() {
	}

	public CreditCardType(Long id, Long originId, String code, String description, Boolean archived) {
		this.id = id;
		this.originId = originId;
		this.code = code;
		this.description = description;
		this.archived = archived;
	}

	public Long getId() { return id; }
	public void setId(Long id) {
		this.id= id;
	}

	public Long getOriginId() { return originId; }
	public void setOriginId(Long originId) {
		this.originId = originId;
	}

	public String getCode() { return code; }
	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() { return description; }
	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getArchived() { return archived; }
	public void setArchived(Boolean archived) { this.archived = archived; }

	@Override
	public int hashCode() {
		return Objects.hash(this.code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreditCardType other = (CreditCardType) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.code;
	}
}
