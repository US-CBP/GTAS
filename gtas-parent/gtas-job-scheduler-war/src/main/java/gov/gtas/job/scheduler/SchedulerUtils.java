package gov.gtas.job.scheduler;

import gov.gtas.model.MessageStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulerUtils {
    public static Map<Long, List<MessageStatus>> geFlightMessageMap(List<MessageStatus> source) {
        Map<Long, List<MessageStatus>> messageFlightMap = new HashMap<>();
        for (MessageStatus messageStatus : source) {
            Long flightId = messageStatus.getFlightId();
            if (messageFlightMap.containsKey(flightId)) {
                messageFlightMap.get(flightId).add(messageStatus);
            } else {
                List<MessageStatus> messageStatuses = new ArrayList<>();
                messageStatuses.add(messageStatus);
                messageFlightMap.put(flightId, messageStatuses);
            }
        }
        return messageFlightMap;
    }
}
