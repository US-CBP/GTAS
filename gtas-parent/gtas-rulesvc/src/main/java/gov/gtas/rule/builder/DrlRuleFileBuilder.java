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
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.match.PnrAddressLink;
import gov.gtas.bo.match.PnrBookingDetailLink;
import gov.gtas.bo.match.PnrCreditCardLink;
import gov.gtas.bo.match.PnrDwellTimeLink;
import gov.gtas.bo.match.PnrEmailLink;
import gov.gtas.bo.match.PnrFrequentFlyerLink;
import gov.gtas.bo.match.PnrPassengerLink;
import gov.gtas.bo.match.PnrPhoneLink;
import gov.gtas.bo.match.PnrTravelAgencyLink;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.Bag;
import gov.gtas.model.BookingDetail;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Document;
import gov.gtas.model.DwellTime;
import gov.gtas.model.Email;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPax;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Passenger;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.Seat;
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
    private static final Logger logger = LoggerFactory
            .getLogger(DrlRuleFileBuilder.class);

    private static final Class<?>[] IMPORT_LIST = { Flight.class,
            Passenger.class, Document.class, Pnr.class, Address.class,
            Phone.class, Email.class, FrequentFlyer.class, CreditCard.class,BookingDetail.class,
            Agency.class, DwellTime.class, FlightPax.class, Bag.class, PnrAddressLink.class, PnrCreditCardLink.class,
            PnrEmailLink.class, PnrFrequentFlyerLink.class,PnrBookingDetailLink.class,
            PnrPassengerLink.class, PnrPhoneLink.class,
            PnrTravelAgencyLink.class,PnrDwellTimeLink.class,
            Seat.class};

    private StringBuilder stringBuilder;

    public DrlRuleFileBuilder() {
        this.stringBuilder = new StringBuilder();
        addPackageAndImport();
        // add the global result declaration;
        this.stringBuilder.append(GLOBAL_RESULT_DECLARATION);
    }

    public DrlRuleFileBuilder addRule(final UdrRule udrRule) {
        logger.info("DrlRuleFileBuilder - generating DRL code for UDR with title:"
                + udrRule.getTitle());
        for (Rule rule : udrRule.getEngineRules()) {
            String drl = String.format(rule.getRuleDrl(), udrRule.getId(),
                    rule.getId());
            this.stringBuilder.append(drl).append(StringUtils.LF);
        }
        return this;
    }
    /**
     * Adds the DRL rule for the watch list item.
     * @param wlItem the watch list item.
     * @return this builder object.
     */
    public DrlRuleFileBuilder addWatchlistItemRule(final WatchlistItem wlItem) {
        //the rule template saved in the data base record for watch list item
        // has two place holders (for title and for result action)
        String drl = String.format(wlItem.getItemRuleData(), wlItem.getId(), wlItem.getId());
        
        this.stringBuilder.append(drl).append(StringUtils.LF);
        return this;
    }

    public String build() {
        return this.stringBuilder.toString();
    }

    private void addPackageAndImport() {
        this.stringBuilder.append(RULE_PACKAGE_NAME)
                .append(IMPORT_PREFIX)
                .append(RuleHitDetail.class.getName())
                .append(";").append(NEW_LINE);
        for (Class<?> clazz : IMPORT_LIST) {
            this.stringBuilder.append(IMPORT_PREFIX).append(clazz.getName())
                    .append(";").append(NEW_LINE);
        }
    }
}
