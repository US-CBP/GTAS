/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.aws;

import java.util.Set;

import com.amazonaws.services.sns.AmazonSNS;

import gov.gtas.model.Passenger;

public class HitNotificationConfig {

	private final AmazonSNS amazonSNS;
	private final Set<Passenger> hits;
	private final String topicArn;
	private final String topicSubject;
	private final Long targetwatchlistId;

	public Long getTargetwatchlistId() {
		return targetwatchlistId;
	}

	public HitNotificationConfig(AmazonSNS amazonSNS, Set<Passenger> hits, String topicArn, String topicSubject,
								 Long targetwatchlistId) {
		this.amazonSNS = amazonSNS;
		this.hits = hits;
		this.topicArn = topicArn;
		this.topicSubject = topicSubject;
		this.targetwatchlistId = targetwatchlistId;
	}

	public AmazonSNS getAmazonSNS() {
		return amazonSNS;
	}

	public Set<Passenger> getHits() {
		return hits;
	}

	public String getTopicArn() {
		return topicArn;
	}

	public String getTopicSubject() {
		return topicSubject;
	}

}
