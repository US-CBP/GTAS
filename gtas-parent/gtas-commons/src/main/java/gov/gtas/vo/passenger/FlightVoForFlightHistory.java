package gov.gtas.vo.passenger;

public class FlightVoForFlightHistory extends FlightVo {

	private String passId;
	private boolean bookingDetail;
	private boolean disabledLink = false;

	public String getPassId() {
		return passId;
	}

	public void setPassId(String passengerId) {
		this.passId = passengerId;
	}

	public boolean isBookingDetail() {
		return bookingDetail;
	}

	public void setBookingDetail(boolean bookingDetail) {
		this.bookingDetail = bookingDetail;
	}

	public void setDisabledLink(boolean disabledLink) {
		this.disabledLink = disabledLink;
	}

	public boolean getDisabledLink() {
		return disabledLink;
	}
}
