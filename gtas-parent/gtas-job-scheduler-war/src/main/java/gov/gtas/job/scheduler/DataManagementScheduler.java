/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.job.scheduler;

import gov.gtas.enumtype.DataManagementTruncation;
import gov.gtas.model.User;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.DataManagementRepository;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.DataManagementService;
import gov.gtas.services.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataManagementScheduler {


    private static final Logger logger = LoggerFactory
            .getLogger(DataManagementScheduler.class);

    @Autowired
    private DataManagementRepository dataManagementRepository;

    @Autowired
    private DataManagementService dataManagementService;

    @Autowired
    private AppConfigurationService appConfigurationService;

    @Autowired
    private UserService userService;

    private Integer DEFAULT_TIME_IN_MONTHS = 6;


    //@Scheduled(fixedDelayString = "${datamanagement.fixedDelay.in.milliseconds}", initialDelayString = "${datamanagement.initialDelay.in.milliseconds}")
    public void jobScheduling() {

        try {

            String truncTypeFlag = appConfigurationService.findByOption(AppConfigurationRepository.DATA_MANAGEMENT_TRUNC_TYPE_FLAG).getValue();
            Long truncTimeFlag = Long.parseLong(appConfigurationService.findByOption(AppConfigurationRepository.DATA_MANAGEMENT_CUT_OFF_TIME_SPAN).getValue());
            DataManagementTruncation passThruTruncTypeFlag;
            User currentUser = userService.fetchUser("admin");

            if (truncTypeFlag.startsWith("API")) {
                passThruTruncTypeFlag = DataManagementTruncation.APIS_ONLY;
            } else if (truncTypeFlag.startsWith("PNR")) {
                passThruTruncTypeFlag = DataManagementTruncation.PNR_ONLY;
            } else {
                passThruTruncTypeFlag = DataManagementTruncation.ALL;
            }

            if(((truncTimeFlag < 1 || (truncTimeFlag > 24)))){
                truncTimeFlag = DEFAULT_TIME_IN_MONTHS.longValue();
            }

            dataManagementService.truncateAllMessageDataByDate(LocalDate.now().minusMonths(truncTimeFlag), currentUser, passThruTruncTypeFlag);

        }catch (IllegalArgumentException iex){
            logger.error(iex.getMessage());

        }catch (Exception ex){
            logger.error(ex.getMessage());
        }
    }


}
