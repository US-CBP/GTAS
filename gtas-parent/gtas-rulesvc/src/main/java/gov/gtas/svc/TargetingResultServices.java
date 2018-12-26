package gov.gtas.svc;

import gov.gtas.repository.udr.RuleMetaRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.PassengerService;

public class TargetingResultServices {

    private PassengerService passengerService;
    private AppConfigurationService appConfigurationService;
    private RuleMetaRepository ruleMetaRepository;

    public PassengerService getPassengerService() {
        return passengerService;
    }

    void setPassengerService(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    public AppConfigurationService getAppConfigurationService() {
        return appConfigurationService;
    }

    void setAppConfigurationService(AppConfigurationService appConfigurationService) {
        this.appConfigurationService = appConfigurationService;
    }

    public RuleMetaRepository getRuleMetaRepository() {
        return ruleMetaRepository;
    }

    void setRuleMetaRepository(RuleMetaRepository ruleMetaRepository) {
        this.ruleMetaRepository = ruleMetaRepository;
    }
}
