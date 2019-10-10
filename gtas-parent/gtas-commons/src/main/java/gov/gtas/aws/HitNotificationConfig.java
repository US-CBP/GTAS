/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.aws;

import java.util.List;

import com.amazonaws.services.sns.AmazonSNS;

import gov.gtas.model.HitsSummary;

public class HitNotificationConfig {

	private final AmazonSNS amazonSNS;
	private final List<HitsSummary> hits;
	private final String topicArn;
	private final String topicSubject;
	private final Long targetwatchlistId;

	public Long getTargetwatchlistId() {
		return targetwatchlistId;
	}

	public HitNotificationConfig(AmazonSNS amazonSNS, List<HitsSummary> hits, String topicArn, String topicSubject,
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

	public List<HitsSummary> getHits() {
		return hits;
	}

	public String getTopicArn() {
		return topicArn;
	}

	public String getTopicSubject() {
		return topicSubject;
	}

}
