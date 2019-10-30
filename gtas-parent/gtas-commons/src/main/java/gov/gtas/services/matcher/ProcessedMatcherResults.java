/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.matcher;

class ProcessedMatcherResults {

	private boolean caseCreated = false;
	private int hitCounter;

	void setHitCounter(int hitCounter) {
		this.hitCounter = hitCounter;
	}

	int getHitCounter() {
		return hitCounter;
	}
}
