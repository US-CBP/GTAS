/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.vo.passenger;

public class CountDownVo {
	private final String countDownTimer;
	private final boolean closeToCountDown;
	private final long millisecondsFromDate;

	public CountDownVo(String countDownTimer, boolean closeToCountDown, long millisecondsFromDate) {
		this.countDownTimer = countDownTimer;
		this.closeToCountDown = closeToCountDown;
		this.millisecondsFromDate = millisecondsFromDate;
	}

	public String getCountDownTimer() {
		return countDownTimer;
	}

	public boolean isCloseToCountDown() {
		return closeToCountDown;
	}

	public long getMillisecondsFromDate() {
		return millisecondsFromDate;
	}

	@Override
	public String toString() {
		return "CountDownVo{" + "countDownTimer='" + countDownTimer + '\'' + ", closeToCountDown=" + closeToCountDown
				+ ", millisecondsFromDate=" + millisecondsFromDate + '}';
	}
}
