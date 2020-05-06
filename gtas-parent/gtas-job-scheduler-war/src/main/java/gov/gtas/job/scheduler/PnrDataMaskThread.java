package gov.gtas.job.scheduler;

import gov.gtas.job.scheduler.service.DataRetentionService;
import gov.gtas.model.*;
import gov.gtas.services.PassengerService;
import gov.gtas.services.PnrMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Component
public class PnrDataMaskThread extends DataSchedulerThread implements Callable<Boolean> {

    private static final Logger logger = LoggerFactory.getLogger(PnrDataMaskThread.class);

    private final PassengerService passengerService;

    private final DataRetentionService dataRetentionService;

    public PnrDataMaskThread(PassengerService passengerService, DataRetentionService dataRetentionService) {
        this.passengerService = passengerService;
        this.dataRetentionService = dataRetentionService;
    }

    @Override
    public Boolean call() {
        long start = System.nanoTime();
        logger.debug("Starting rule running scheduled task");
        boolean success = true;
        try {
            if (getMessageStatuses().isEmpty()) {
                logger.debug("No messages to process, ending masking process");
                return success;
            }
            List<Message> messages = getMessageStatuses().stream().map(MessageStatus::getMessage).collect(Collectors.toList());
            Set<Long> flightIds = getMessageStatuses().stream().map(MessageStatus::getFlightId).collect(Collectors.toSet());
            List<Pnr> apisMessageList = messages
                    .stream()
                    .filter(m -> m instanceof Pnr)
                    .map(m -> (Pnr) m)
                    .collect(Collectors.toList());
            Set<Long> pnrMessageIds = apisMessageList.stream().map(Pnr::getId).collect(Collectors.toSet());

            Set<Passenger> passengers = passengerService.getPassengersFromMessageIds(pnrMessageIds, flightIds);

            Set<DataRetentionStatus> dataRetentionStatuses = new HashSet<>();
            for (Passenger p : passengers) {
                DataRetentionStatus drs = p.getDataRetentionStatus();
                drs.setUpdatedAt(new Date());
                drs.setUpdatedBy("PNR_MASK");
                if (p.getHitDetails().isEmpty()) {
                    drs.setMaskedPNR(true);
                }
                dataRetentionStatuses.add(drs);
            }
            dataRetentionService.saveDataRetentionStatus(dataRetentionStatuses);
            getMessageStatuses().forEach(ms -> ms.setMessageStatusEnum(MessageStatusEnum.PNR_DATA_MASKED));
            logger.debug("Total rule running pnr data masking task took  " + (System.nanoTime() - start) / 1000000 + "m/s.");
        } catch(Exception e) {
            logger.error("", e);
            success = false;
        }
        return success;
    }
}
