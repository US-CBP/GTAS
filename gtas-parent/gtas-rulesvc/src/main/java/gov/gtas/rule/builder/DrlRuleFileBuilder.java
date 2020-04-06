/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import static gov.gtas.rule.builder.RuleTemplateConstants.GLOBAL_RESULT_DECLARATION;
import static gov.gtas.rule.builder.RuleTemplateConstants.IMPORT_PREFIX;
import static gov.gtas.rule.builder.RuleTemplateConstants.NEW_LINE;
import static gov.gtas.rule.builder.RuleTemplateConstants.RULE_PACKAGE_NAME;

import gov.gtas.bo.FlightPassengerLink;
import gov.gtas.bo.match.*;
import gov.gtas.model.RuleHitDetail;
import gov.gtas.model.*;
import gov.gtas.model.udr.Rule;
import gov.gtas.model.udr.UdrRule;
import gov.gtas.model.watchlist.WatchlistItem;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A builder pattern class for constructing a Drools rule "file" (actually a
 * text string) from one or more UDR objects. This DRL string is then compiled
 * into a Knowledge Base (KieBase object).
 */
public class DrlRuleFileBuilder {
	private static final Logger logger = LoggerFactory.getLogger(DrlRuleFileBuilder.class);

	private static final Class<?>[] IMPORT_LIST = { Flight.class, Passenger.class, PassengerDetails.class,
			PassengerTripDetails.class, Document.class, Pnr.class, Address.class, Phone.class, Email.class,
			FrequentFlyer.class, CreditCard.class, BookingDetail.class, Agency.class, DwellTime.class, FlightPax.class,
			Bag.class, PnrAddressLink.class, PnrCreditCardLink.class, PnrEmailLink.class, PnrFrequentFlyerLink.class,
			PnrBookingLink.class, PnrPassengerLink.class, PnrPhoneLink.class, PnrTravelAgencyLink.class,
			PnrDwellTimeLink.class, Seat.class, PaymentForm.class, MutableFlightDetails.class,
			PnrFormOfPaymentLink.class, FlightPassengerLink.class };

	private StringBuilder stringBuilder;

	public DrlRuleFileBuilder() {
		this.stringBuilder = new StringBuilder();
		addPackageAndImport();
		// add the global result declaration;
		this.stringBuilder.append(GLOBAL_RESULT_DECLARATION);
	}

	public DrlRuleFileBuilder addRule(final UdrRule udrRule) {
		logger.debug("DrlRuleFileBuilder - generating DRL code for UDR with title:" + udrRule.getTitle());
		for (Rule rule : udrRule.getEngineRules()) {
			String drl = String.format(rule.getRuleDrl(), udrRule.getId(), rule.getId());
			this.stringBuilder.append(drl).append(StringUtils.LF);
		}
		return this;
	}

	/**
	 * Adds the DRL rule for the watch list item.
	 * 
	 * @param wlItem
	 *            the watch list item.
	 * @return this builder object.
	 */
	public DrlRuleFileBuilder addWatchlistItemRule(final WatchlistItem wlItem) {
		// the rule template saved in the data base record for watch list item
		// has two place holders (for title and for result action)
		String drl = String.format(wlItem.getItemRuleData(), wlItem.getId(), wlItem.getId());

		this.stringBuilder.append(drl).append(StringUtils.LF);
		return this;
	}

	public String build() {
		return this.stringBuilder.toString();
	}

	private void addPackageAndImport() {
		this.stringBuilder.append(RULE_PACKAGE_NAME).append(IMPORT_PREFIX).append(RuleHitDetail.class.getName())
				.append(";").append(NEW_LINE);
		for (Class<?> clazz : IMPORT_LIST) {
			this.stringBuilder.append(IMPORT_PREFIX).append(clazz.getName()).append(";").append(NEW_LINE);
		}
	}
}
