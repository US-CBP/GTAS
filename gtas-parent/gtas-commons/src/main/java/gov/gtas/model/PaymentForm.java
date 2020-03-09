/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "payment_form")
public class PaymentForm implements Serializable {
	private static final long serialVersionUID = 1166723L;

	public PaymentForm() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")
	private Long id;

	@ManyToOne
	private Pnr pnr;

	public Long getPnrId() {
		return pnrId;
	}

	public void setPnrId(Long pnrId) {
		this.pnrId = pnrId;
	}

	@Column(name = "pnr_id", columnDefinition = "bigint unsigned", updatable = false, insertable = false)
	private Long pnrId;

	@Column(name = "payment_type")
	private String paymentType;

	@Column(name = "payment_amount")
	private String paymentAmount;

	@Column(name = "payment_whole_dollar", precision = 18, scale = 2)
	private Integer wholeDollarAmount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public Pnr getPnr() {
		return pnr;
	}

	public void setPnr(Pnr pnr) {
		this.pnr = pnr;
	}

	public Integer getWholeDollarAmount() {
		return wholeDollarAmount;
	}

	public void setWholeDollarAmount(Integer wholeDollarAmount) {
		this.wholeDollarAmount = wholeDollarAmount;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PaymentForm that = (PaymentForm) o;

		if (pnrId != null ? !pnrId.equals(that.pnrId) : that.pnrId != null)
			return false;
		if (paymentType != null ? !paymentType.equals(that.paymentType) : that.paymentType != null)
			return false;
		if (paymentAmount != null ? !paymentAmount.equals(that.paymentAmount) : that.paymentAmount != null)
			return false;
		return wholeDollarAmount != null ? wholeDollarAmount.equals(that.wholeDollarAmount)
				: that.wholeDollarAmount == null;
	}

	@Override
	public int hashCode() {
		int result = pnrId != null ? pnrId.hashCode() : 0;
		result = 31 * result + (paymentType != null ? paymentType.hashCode() : 0);
		result = 31 * result + (paymentAmount != null ? paymentAmount.hashCode() : 0);
		result = 31 * result + (wholeDollarAmount != null ? wholeDollarAmount.hashCode() : 0);
		return result;
	}
}
