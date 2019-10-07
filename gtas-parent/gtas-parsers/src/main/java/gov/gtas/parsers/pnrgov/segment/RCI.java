/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.pnrgov.PnrUtils;

/**
 * <p>
 * RCI: RESERVATION CONTROL INFORMATION
 * <p>
 * The composite will appear at least once and may be repeated up to eight more
 * times.
 * <p>
 * Examples:
 * <ul>
 * <li>SAS passenger record reference.(RCI+SK:12DEF')
 * <li>Galileo and SAS record references.(RCI+SK:123EF+1G:345ABC')
 * <li>Delta is the operating carrier and the PNR was created on 24 February
 * 2010 at 2230 GMT. (RCI+DL:ABC456789::240210:2230')
 * <li>CX is the operating carrier and no PNR was received from the reservation
 * system at a station handled by a ground handler; therefore the CX reservation
 * PNR locator is not available and “DCS reference” is the Reservation Control
 * Type.(RCI+CX:89QM3LABML:C’)
 * </ul>
 */
public class RCI extends Segment {
	private static final String DCS_CONTROL_TYPE = "C";

	public class ReservationControlInfo {
		private String airlineCode;
		private String reservationControlNumber;
		private String reservationControlType;
		private Date timeCreated;
		private boolean isDcsReference;

		public String getAirlineCode() {
			return airlineCode;
		}

		public void setAirlineCode(String airlineCode) {
			this.airlineCode = airlineCode;
		}

		public String getReservationControlNumber() {
			return reservationControlNumber;
		}

		public void setReservationControlNumber(String reservationControlNumber) {
			this.reservationControlNumber = reservationControlNumber;
		}

		public String getReservationControlType() {
			return reservationControlType;
		}

		public void setReservationControlType(String reservationControlType) {
			this.reservationControlType = reservationControlType;
		}

		public Date getTimeCreated() {
			return timeCreated;
		}

		public void setTimeCreated(Date timeCreated) {
			this.timeCreated = timeCreated;
		}

		public boolean isDcsReference() {
			return isDcsReference;
		}

		public void setDcsReference(boolean isDcsReference) {
			this.isDcsReference = isDcsReference;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
	}

	private List<ReservationControlInfo> reservations;

	public RCI(List<Composite> composites) throws ParseException {
		super(RCI.class.getSimpleName(), composites);
		reservations = new ArrayList<>(numComposites());

		for (int i = 0; i < numComposites(); i++) {
			ReservationControlInfo r = new ReservationControlInfo();
			Composite c = getComposite(i);

			r.setAirlineCode(c.getElement(0));
			r.setReservationControlNumber(c.getElement(1));

			String controlType = c.getElement(2);
			r.setReservationControlType(controlType);
			r.setDcsReference(DCS_CONTROL_TYPE.equals(controlType));

			String dt = c.getElement(3);
			if (dt != null) {
				if (c.getElement(4) != null) {
					dt += c.getElement(4);

				}
				r.setTimeCreated(PnrUtils.parseDateTime(dt));
			}

			reservations.add(r);
		}
	}

	public List<ReservationControlInfo> getReservations() {
		return reservations;
	}
}
