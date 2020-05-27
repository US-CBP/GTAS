package gov.gtas.job.scheduler;

import gov.gtas.model.*;

import java.util.HashSet;
import java.util.Set;

public class PnrFieldsToScrub {
    private Set<FrequentFlyer> frequentFlyers = new HashSet<>();
    private Set<FrequentFlyerDataRetentionPolicyAudit> frequentFlyersDataRetentionPolicy = new HashSet<>();
    private Set<EmailDataRetentionPolicyAudit> emailsDataRetentionPolicy = new HashSet<>();
    private Set<Email> emails = new HashSet<>();
    private Set<PhoneDataRetentionPolicyAudit> phoneDataRetentionPolicy = new HashSet<>();
    private Set<Phone> phones = new HashSet<>();
    private Set<CreditCardDataRetentionPolicyAudit> creditCardAudits = new HashSet<>();
    private Set<CreditCard> creditCard = new HashSet<>();
    private Set<AddressDataRetentionPolicyAudit> addressAudits = new HashSet<>();
    private Set<Address> addresses = new HashSet<>();

    public void setFrequentFlyers(Set<FrequentFlyer> frequentFlyers) {
        this.frequentFlyers = frequentFlyers;
    }

    public Set<FrequentFlyer> getFrequentFlyers() {
        return frequentFlyers;
    }

    public void setFrequentFlyersDataRetentionPolicy(Set<FrequentFlyerDataRetentionPolicyAudit> frequentFlyersDataRetentionPolicy) {
        this.frequentFlyersDataRetentionPolicy = frequentFlyersDataRetentionPolicy;
    }

    public Set<FrequentFlyerDataRetentionPolicyAudit> getFrequentFlyersDataRetentionPolicy() {
        return frequentFlyersDataRetentionPolicy;
    }

    public void setEmailsDataRetentionPolicy(Set<EmailDataRetentionPolicyAudit> emailsDataRetentionPolicy) {
        this.emailsDataRetentionPolicy = emailsDataRetentionPolicy;
    }

    public Set<EmailDataRetentionPolicyAudit> getEmailsDataRetentionPolicy() {
        return emailsDataRetentionPolicy;
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public Set<Email> getEmails() {
        return emails;
    }

    public void setPhoneDataRetentionPolicy(Set<PhoneDataRetentionPolicyAudit> phoneDataRetentionPolicy) {
        this.phoneDataRetentionPolicy = phoneDataRetentionPolicy;
    }

    public Set<PhoneDataRetentionPolicyAudit> getPhoneDataRetentionPolicy() {
        return phoneDataRetentionPolicy;
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
    }

    public Set<Phone> getPhones() {
        return phones;
    }

    public void setCreditCardAudits(Set<CreditCardDataRetentionPolicyAudit> creditCardAudits) {
        this.creditCardAudits = creditCardAudits;
    }

    public Set<CreditCardDataRetentionPolicyAudit> getCreditCardAudits() {
        return creditCardAudits;
    }

    public void setCreditCard(Set<CreditCard> creditCard) {
        this.creditCard = creditCard;
    }

    public Set<CreditCard> getCreditCard() {
        return creditCard;
    }

    public void setAddressAudits(Set<AddressDataRetentionPolicyAudit> addressAudits) {
        this.addressAudits = addressAudits;
    }

    public Set<AddressDataRetentionPolicyAudit> getAddressAudits() {
        return addressAudits;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }
}
