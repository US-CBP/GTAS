package gov.gtas.job.scheduler;


import gov.gtas.job.scheduler.service.DataRetentionService;
import gov.gtas.model.*;
import gov.gtas.services.PassengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class ApisDataMaskThread extends DataSchedulerThread implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(ApisDataMaskThread.class);

    private final PassengerService passengerService;

    private final DataRetentionService dataRetentionService;

    public ApisDataMaskThread(PassengerService passengerService, DataRetentionService dataRetentionService) {
        this.passengerService = passengerService;
        this.dataRetentionService = dataRetentionService;
    }


    @Override
    public Boolean call()  {
        boolean success = true;
        try {
            long start = System.nanoTime();
            logger.debug("Starting rule running scheduled task");
            if (getMessageStatuses().isEmpty()) {
                logger.debug("No messages to process, ending masking process");
                return success;
            }
            MessageAndFlightIds messageAndFlightIds = getApisMessageIdsAndFlightIds();
            Set<Passenger> passengers = passengerService.getPassengersFromMessageIds(messageAndFlightIds.getMessageIds(), messageAndFlightIds.getFlightIds());
           getDefaultShareConstraint().createFilter(passengers);
            Set<DataRetentionStatus> dataRetentionStatuses = new HashSet<>();
            for (Passenger p : passengers) {
                DataRetentionStatus drs = p.getDataRetentionStatus();
                drs.setUpdatedAt(new Date());
                drs.setUpdatedBy("APIS_MASK");
                if (!getDefaultShareConstraint().getWhiteListedPassenerIds().contains(p.getId())) {
                    drs.setMaskedAPIS(true);
                }
                dataRetentionStatuses.add(drs);
            }
            dataRetentionService.saveDataRetentionStatus(dataRetentionStatuses);
            getMessageStatuses().forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.APIS_DATA_MASKED));
            dataRetentionService.saveMessageStatus(getMessageStatuses());
            logger.debug("Total time running apis data masking task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
        } catch (Exception e) {
            logger.error("", e);
            success = false;
        }
        return success;
    }


}
