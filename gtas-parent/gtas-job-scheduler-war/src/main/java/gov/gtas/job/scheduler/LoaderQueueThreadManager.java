/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gov.gtas.parsers.paxlst.segment.unedifact.DTM;
import gov.gtas.parsers.paxlst.segment.unedifact.LOC;
import gov.gtas.parsers.paxlst.segment.unedifact.TDT;
import gov.gtas.parsers.pnrgov.segment.TVL_L0;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.repository.AppConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.exception.ParseException;

import static gov.gtas.services.GtasLoaderImpl.*;

@Component
public class LoaderQueueThreadManager {

    private final ApplicationContext ctx;
    
    private static final int  DEFAULT_THREADS_ON_LOADER = 5;

    private ExecutorService exec;

    private static ConcurrentMap<String, BlockingQueue<Message<?>>> bucketBucket = new ConcurrentHashMap<>();

    static final Logger logger = LoggerFactory.getLogger(LoaderQueueThreadManager.class);

    @Autowired
	public LoaderQueueThreadManager(ApplicationContext ctx, AppConfigurationRepository appConfigurationRepository) {
		this.ctx = ctx;

		/**
		 * Fail safe and fall back to 5 number of threads when the database
		 * configuration is set incorrectly
		 */
		int maxNumOfThreads = DEFAULT_THREADS_ON_LOADER;

		try {

			maxNumOfThreads = Integer.parseInt(
					appConfigurationRepository.findByOption(AppConfigurationRepository.THREADS_ON_LOADER).getValue());

		} catch (Exception e) {
			logger.error(String.format(
					"Failed to load application configuration: '%1$s' from the database... Number of threads set to use %2$s",
					AppConfigurationRepository.THREADS_ON_LOADER, DEFAULT_THREADS_ON_LOADER));
		}

		this.exec = Executors.newFixedThreadPool(maxNumOfThreads);
	}

    void receiveMessages(Message<?> message) throws ParseException {
        String[] primeFlightKeyArray = generatePrimeFlightKey(message);

        //Construct label for individual buckets out of concatenated array values from prime flight key generation
        String primeFlightKey = primeFlightKeyArray[0] + primeFlightKeyArray[1] + primeFlightKeyArray[2] + primeFlightKeyArray[3] + primeFlightKeyArray[4];
        // bucketBucket is a bucket of buckets. It holds a series of queues that are processed sequentially.
        // This solves the problem where-in which we cannot run the risk of trying to save/update the same flight at the same time. This is done
        // by shuffling all identical flights into the same queue in order to be processed sequentially. However, by processing multiple
        // sequential queues at the same time, we in essence multi-thread the process for all non-identical prime flights
        BlockingQueue<Message<?>> potentialBucket = bucketBucket.get(primeFlightKey);
        if (potentialBucket == null) {
            // Is not existing bucket, make bucket, stuff in bucketBucket,
            logger.debug("New Queue Created For Prime Flight: " + primeFlightKey);
            BlockingQueue<Message<?>> queue = new ArrayBlockingQueue<>(1024);
            queue.offer(message); // TODO: offer returns false if can't enter the queue, need to make sure we don'tlose messages and have it wait for re-attempt when there is space.
            bucketBucket.putIfAbsent(primeFlightKey, queue);
            // Only generate workers on a per queue basis
            LoaderWorkerThread worker = ctx.getBean(LoaderWorkerThread.class);
            worker.setQueue(queue);
            worker.setMap(bucketBucket); // give map reference and key in order to kill queue later
            worker.setPrimeFlightKeyArray(primeFlightKeyArray);
            worker.setPrimeFlightKey(primeFlightKey);
            exec.execute(worker);
        } else {
            // Is existing bucket, place same prime flight message into bucket
            logger.debug("Existing Queue Found! Placing message inside...");
            potentialBucket.offer(message);
            // No need to execute worker here, if queue exists then worker is already on it.
        }
    }

    /*
    Crafts prime flight key out of TVL0 line of a PNR message or DTM LOC and TDT of an APIS message.
    Key is the following
    primeFlightKeyArray[0] = PRIME FLIGHT ORIGIN
    primeFlightKeyArray[1] = PRIME FLIGHT DESTINATION
    primeFlightKeyArray[2] = PRIME FLIGHT CARRIER
    primeFlightKeyArray[3] = PRIME FLIGHT NUMBER
    primeFlightKeyArray[4] = PRIME FLIGHT ETD DATE AS A STRING LONG VALUE
    */
    private String[] generatePrimeFlightKey(Message<?> message) throws ParseException {
        String[] primeFlightKeyArray = new String[6];
        List<Segment> messageSegments = getMessageSegments(message);
        boolean apisMessage = true;
        // Arbitrarily attempt to read prime flight from PNR first.
        for (Segment segment : messageSegments) {
            // Extract the prime flight information from a PNR message.
            // This will mirror prime flight array result of an APIS message.
            // PNR and APIS messages relating to the same prime flight
            // will always generate the same label.
            if (segment.getName().equalsIgnoreCase("TVL")) {
                apisMessage = false;
                TVL_L0 tvl = new TVL_L0(segment.getComposites());
                primeFlightKeyArray[PRIME_FLIGHT_ORIGIN] = tvl.getOrigin().trim();
                primeFlightKeyArray[PRIME_FLIGHT_DESTINATION] = tvl.getDestination().trim();
                primeFlightKeyArray[PRIME_FLIGHT_CARRIER] = tvl.getCarrier().trim();
                String primeFlightNumber = tvl.getFlightNumber().trim();
                primeFlightNumber = addZerosToPrimeFlightIfNeeded(primeFlightNumber);
                primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING] = primeFlightNumber;
                primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG] = Long.toString(flightDate(tvl));
                primeFlightKeyArray[ETD_DATE_WITH_TIMESTAMP] = Long.toString(tvl.getEtd().getTime());
                break;
            }
        }

        // If the attempt to parse a PNR doesn't result in a prime flight key attempt to read segments as an APIS message.
        if (apisMessage) {
            int locCount = 0;
            boolean primeFlightArrivalFound = false;
            boolean primeFlightDepartFound = false;
            boolean primeFlightDepartDateFound = false;
            for (Segment seg : messageSegments) {
                // Extract the prime flight information from an APIS message.
                // This will mirror prime flight array result of a PNR message.
                // PNR and APIS messages relating to the same prime flight
                // will always generate the same label.
                switch (seg.getName()) {
                    case "TDT":
                        // TDT is the parent of LOC and DTM. We are basing processing the loop off the messages in the
                        // messages below. This means the information relating the TDT can be overwritten several times
                        // before finding a prime flight.
                        TDT tdt = new TDT(seg.getComposites());
                        primeFlightKeyArray[PRIME_FLIGHT_CARRIER] = tdt.getC_carrierIdentifier();
                        String primeFlightNumber = tdt.getFlightNumber().trim();
                        primeFlightNumber = addZerosToPrimeFlightIfNeeded(primeFlightNumber);
                        primeFlightKeyArray[PRIME_FLIGHT_NUMBER_STRING] = primeFlightNumber;
                        break;
                    case "LOC":
                        LOC loc = new LOC(seg.getComposites());
                        // The arrival airport corresponds to the prime flight's arrival airport.
                        if (loc.getFunctionCode() == LOC.LocCode.ARRIVAL_AIRPORT) {
                            primeFlightKeyArray[PRIME_FLIGHT_DESTINATION] = loc.getLocationNameCode();
                            primeFlightArrivalFound = true;
                        // The departure airport corresponds with the prime flight's departure airport.
                        } else if (loc.getFunctionCode() == LOC.LocCode.DEPARTURE_AIRPORT) {
                            primeFlightKeyArray[PRIME_FLIGHT_ORIGIN] = loc.getLocationNameCode();
                            primeFlightDepartFound = true;
                        }
                        break;
                    case "DTM":
                        DTM dtm = new DTM(seg.getComposites());
                        // Take advantage that the next DTM after the prime flight departure airport will hold the prime flight ETD.
                        if (dtm.getDtmCode() == DTM.DtmCode.DEPARTURE && primeFlightDepartFound) {
                            primeFlightKeyArray[ETD_DATE_NO_TIMESTAMP_AS_LONG] = Long.toString(DateUtils.stripTime(dtm.getDtmValue()).getTime());
                            primeFlightKeyArray[ETD_DATE_WITH_TIMESTAMP] = Long.toString(dtm.getDtmValue().getTime());
                            primeFlightDepartDateFound = true;
                        }
                        break;
                    default:
                        break;
                }
                if (primeFlightArrivalFound && primeFlightDepartFound && primeFlightDepartDateFound) {
                    //Prime flight generated - stop processing message!
                    break;
                }
            }
        }
        return primeFlightKeyArray;
    }

    private long flightDate(TVL_L0 tvl) {
        return DateUtils.stripTime(tvl.getEtd()).getTime();
    }

    private List<Segment> getMessageSegments(Message<?> message) {
        List<Segment> segments = new ArrayList<>();
        EdifactLexer lexer = new EdifactLexer((String) message.getPayload());
        try {
            segments = lexer.tokenize();
        } catch (ParseException e) {
            logger.error("error tokenizing segments", e);
        }
        return segments;
    }

    private String addZerosToPrimeFlightIfNeeded(String primeFlightNumber) {
        StringBuilder primeFlightNumberBuilder = new StringBuilder(primeFlightNumber);
        while (primeFlightNumberBuilder.length() < 4) {
            primeFlightNumberBuilder.insert(0, "0");
        }
        return primeFlightNumberBuilder.toString();
    }
}
