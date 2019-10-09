package gov.gtas.parsers.vo;

import org.apache.commons.lang3.StringUtils;

public class TicketFareVo {

	private Long id;

	private String paymentAmount;

	private String currencyCode;

	private String ticketNumber = "0";

	private String ticketType;

	private String numberOfBooklets;

	private boolean ticketless;

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

	public boolean isValid() {
		boolean valid = true;
		if ((this.ticketNumber.equals("0") && (!this.isTicketless())) && StringUtils.isBlank(this.paymentAmount)) {
			valid = false;
		}
		return valid;
	}

}
