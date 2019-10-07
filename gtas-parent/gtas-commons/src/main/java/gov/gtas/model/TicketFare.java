package gov.gtas.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Table(name = "ticket_fare")
public class TicketFare implements Serializable {

	private static final long serialVersionUID = 1L;

	public TicketFare() {

	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Basic(optional = false)
	@Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")
	private Long id;

	@Column(name = "payment_amount")
	private String paymentAmount;

	@Column(name = "currency_code")
	private String currencyCode;

	@Column(name = "ticket_number")
	private String ticketNumber = "0";

	@Column(name = "ticket_type")
	private String ticketType;

	@Column(name = "number_booklets")
	private String numberOfBooklets;

	@Column(name = "ticketless")
	private boolean ticketless;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Passenger passenger;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentAmount() {
		return paymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(String ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public String getNumberOfBooklets() {
		return numberOfBooklets;
	}

	public void setNumberOfBooklets(String numberOfBooklets) {
		this.numberOfBooklets = numberOfBooklets;
	}

	public boolean isTicketless() {
		return ticketless;
	}

	public void setTicketless(boolean ticketless) {
		this.ticketless = ticketless;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TicketFare))
			return false;
		TicketFare that = (TicketFare) o;
		return isTicketless() == that.isTicketless() && Objects.equals(getPaymentAmount(), that.getPaymentAmount())
				&& Objects.equals(getCurrencyCode(), that.getCurrencyCode())
				&& Objects.equals(getTicketNumber(), that.getTicketNumber())
				&& Objects.equals(getTicketType(), that.getTicketType())
				&& Objects.equals(getNumberOfBooklets(), that.getNumberOfBooklets());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPaymentAmount(), getCurrencyCode(), getTicketNumber(), getTicketType(),
				getNumberOfBooklets(), isTicketless());
	}
}
