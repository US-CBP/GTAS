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
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.util.ParseUtils;

/**
 * <p>
 * DAT: DATE AND TIME INFORMATION
 * <p>
 * To convey information regarding estimated or actual dates and times of
 * operational events.
 * <p>
 * Unless specifically stated otherwise in bilateral agreement, the time is in
 * Universal Time Coordinated (UTC)
 */
public class DAT extends Segment {
	private static final String CHECKIN_FREE_TEXT_CODE = "3";

	public class DatDetails {
		private String type;
		private Date dateTime;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Date getDateTime() {
			return dateTime;
		}

		public void setDateTime(Date dateTime) {
			this.dateTime = dateTime;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
	}

	private List<DatDetails> dateTimes;

	public DAT(List<Composite> composites) throws ParseException {
		super(DAT.class.getSimpleName(), composites);

		this.dateTimes = new ArrayList<>();
		for (Composite c : getComposites()) {
			DatDetails d = new DatDetails();

			String type = c.getElement(0);
			d.setType(type);
			if (CHECKIN_FREE_TEXT_CODE.equals(type)) {
				d.setDateTime(processFreeTextDt(c));
			} else {
				d.setDateTime(processDt(c));
			}

			dateTimes.add(d);
		}
	}

	public static Date processDt(Composite c) throws ParseException {
		String dt = c.getElement(1);
		if (dt != null) {
			String time = c.getElement(2);
			if (time != null) {
				dt += time;
			}
			return PnrUtils.parseDateTime(dt);
		}

		return null;
	}

	/**
	 * e.g. DAT+3:L FT WW D014357 12AUG121423Z 1D5723'
	 */
	public static Date processFreeTextDt(Composite c) throws ParseException {
		String text = c.getElement(1);
		String dt = text.split("\\s+")[4];
		return ParseUtils.parseDateTime(dt, DateUtils.DT_FORMAT_MONTH_GMT);
	}

	public List<DatDetails> getDateTimes() {
		return dateTimes;
	}
}
